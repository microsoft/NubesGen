terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.26"
    }
  }
}

resource "azurecaf_name" "container_registry" {
  name          = var.application_name
  resource_type = "azurerm_container_registry"
  suffixes      = [var.environment]
}

resource "azurerm_container_registry" "container-registry" {
  name                = azurecaf_name.container_registry.result
  resource_group_name = var.resource_group
  location            = var.location
  admin_enabled       = true
  sku                 = "Basic"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurecaf_name" "app_service_plan" {
  name          = var.application_name
  resource_type = "azurerm_app_service_plan"
  suffixes      = [var.environment]
}

# This creates the plan that the service use
resource "azurerm_service_plan" "application" {
  name                = azurecaf_name.app_service_plan.result
  resource_group_name = var.resource_group
  location            = var.location

  sku_name = "S1"
  os_type  = "Linux"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurecaf_name" "app_service" {
  name          = var.application_name
  resource_type = "azurerm_app_service"
  suffixes      = [var.environment]
}

# This creates the service definition
resource "azurerm_linux_web_app" "application" {
  name                = azurecaf_name.app_service.result
  resource_group_name = var.resource_group
  location            = var.location
  service_plan_id     = azurerm_service_plan.application.id
  https_only          = true

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }

  site_config {
    application_stack {
      docker_image     = "${azurerm_container_registry.container-registry.name}.azurecr.io/${var.application_name}/${var.application_name}"
      docker_image_tag = "latest"
    }
    always_on        = true
    ftps_state       = "FtpsOnly"
  }

  identity {
    type = "SystemAssigned"
  }

  app_settings = {
    "WEBSITES_ENABLE_APP_SERVICE_STORAGE" = "false"
    "DOCKER_REGISTRY_SERVER_URL"          = "https://${azurerm_container_registry.container-registry.name}.azurecr.io"
    "DOCKER_REGISTRY_SERVER_USERNAME"     = azurerm_container_registry.container-registry.admin_username
    "DOCKER_REGISTRY_SERVER_PASSWORD"     = azurerm_container_registry.container-registry.admin_password
    "WEBSITES_PORT"                       = "8080"

    // Monitoring with Azure Application Insights
    "APPINSIGHTS_INSTRUMENTATIONKEY" = var.azure_application_insights_instrumentation_key

    # These are app specific environment variables

    "DATABASE_URL"      = var.database_url
    "DATABASE_USERNAME" = var.database_username
    "DATABASE_PASSWORD" = var.database_password

    "REDIS_HOST"     = var.azure_redis_host
    "REDIS_PASSWORD" = var.azure_redis_password
    "REDIS_PORT"     = "6380"

    "AZURE_STORAGE_ACCOUNT_NAME"  = var.azure_storage_account_name
    "AZURE_STORAGE_BLOB_ENDPOINT" = var.azure_storage_blob_endpoint
    "AZURE_STORAGE_ACCOUNT_KEY"   = var.azure_storage_account_key

    "MONGODB_DATABASE" = var.azure_cosmosdb_mongodb_database
    "MONGODB_URI"      = var.azure_cosmosdb_mongodb_uri
  }
}

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault_access_policy" "application" {
  key_vault_id = var.vault_id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = azurerm_linux_web_app.application.identity[0].principal_id

  secret_permissions = [
    "Get",
    "List"
  ]
}

resource "azurerm_app_service_virtual_network_swift_connection" "swift_connection" {
  app_service_id = azurerm_linux_web_app.application.id
  subnet_id      = var.subnet_id
}
