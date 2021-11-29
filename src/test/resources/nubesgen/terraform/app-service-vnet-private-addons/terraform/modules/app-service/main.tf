terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.9"
    }
  }
}

resource "azurecaf_name" "app_service_plan" {
  name          = var.application_name
  resource_type = "azurerm_app_service_plan"
  suffixes      = [var.environment]
}

# This creates the plan that the service use
resource "azurerm_app_service_plan" "application" {
  name                = azurecaf_name.app_service_plan.result
  resource_group_name = var.resource_group
  location            = var.location

  kind     = "Linux"
  reserved = true

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }

  sku {
    tier = "Standard"
    size = "S1"
  }
}

resource "azurecaf_name" "app_service" {
  name          = var.application_name
  resource_type = "azurerm_app_service"
  suffixes      = [var.environment]
}

# This creates the service definition
resource "azurerm_app_service" "application" {
  name                = azurecaf_name.app_service.result
  resource_group_name = var.resource_group
  location            = var.location
  app_service_plan_id = azurerm_app_service_plan.application.id
  https_only          = true

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }

  site_config {
    linux_fx_version = "JAVA|11-java11"
    always_on        = true
    ftps_state       = "FtpsOnly"
  }

  identity {
    type = "SystemAssigned"
  }

  app_settings = {
    "WEBSITES_ENABLE_APP_SERVICE_STORAGE" = "false"

    // Monitoring with Azure Application Insights
    "APPINSIGHTS_INSTRUMENTATIONKEY" = var.azure_application_insights_instrumentation_key

    # These are app specific environment variables
    "SPRING_PROFILES_ACTIVE" = "prod,azure"

    "SPRING_DATASOURCE_URL"      = "jdbc:postgresql://${var.database_url}"
    "SPRING_DATASOURCE_USERNAME" = var.database_username
    "SPRING_DATASOURCE_PASSWORD" = var.database_password

    "SPRING_REDIS_HOST"     = var.azure_redis_host
    "SPRING_REDIS_PASSWORD" = var.azure_redis_password
    "SPRING_REDIS_PORT"     = "6380"
    "SPRING_REDIS_SSL"      = "true"

    "AZURE_STORAGE_ACCOUNT_NAME"  = var.azure_storage_account_name
    "AZURE_STORAGE_BLOB_ENDPOINT" = var.azure_storage_blob_endpoint
    "AZURE_STORAGE_ACCOUNT_KEY"   = var.azure_storage_account_key

    "SPRING_DATA_MONGODB_DATABASE" = var.azure_cosmosdb_mongodb_database
    "SPRING_DATA_MONGODB_URI"      = var.azure_cosmosdb_mongodb_uri
  }
}

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault_access_policy" "application" {
  key_vault_id = var.vault_id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = azurerm_app_service.application.identity[0].principal_id

  secret_permissions = [
    "Get",
    "List"
  ]
}

resource "azurerm_app_service_virtual_network_swift_connection" "swift_connection" {
  app_service_id = azurerm_app_service.application.id
  subnet_id      = var.subnet_id
}
