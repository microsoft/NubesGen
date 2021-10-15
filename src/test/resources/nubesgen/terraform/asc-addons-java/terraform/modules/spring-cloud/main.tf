# Azure Spring Cloud is not yet supported in azurecaf_name
locals {
  spring_cloud_service_name = "asc-${var.application_name}"
  spring_cloud_app_name     = "app-${var.application_name}"
  cosmosdb_association_name = "${var.application_name}-cosmos"
  redis_association_name    = "${var.application_name}-redis"
}

# This creates the Azure Spring Cloud that the service use
resource "azurerm_spring_cloud_service" "application" {
  name                = local.spring_cloud_service_name
  resource_group_name = var.resource_group
  location            = var.location
  sku_name            = "B0"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
  trace {
    connection_string = var.azure_application_insights_connection_string
  }
}

# This creates the application definition
resource "azurerm_spring_cloud_app" "application" {
  name                = local.spring_cloud_app_name
  resource_group_name = var.resource_group
  service_name        = azurerm_spring_cloud_service.application.name
  identity {
    type = "SystemAssigned"
  }
}

# This creates the application deployment. Terraform provider doesn't support dotnet yet
resource "azurerm_spring_cloud_java_deployment" "application_deployment" {
  name                = "default"
  spring_cloud_app_id = azurerm_spring_cloud_app.application.id
  cpu                 = 1
  instance_count      = 1
  memory_in_gb        = 1
  runtime_version     = "Java_11"
  environment_variables = {
    "SPRING_PROFILES_ACTIVE" = "prod,azure"

    "AZURE_STORAGE_ACCOUNT_NAME"  = var.azure_storage_account_name
    "AZURE_STORAGE_ACCOUNT_KEY"   = var.azure_storage_account_key
    "AZURE_STORAGE_BLOB_ENDPOINT" = var.azure_storage_blob_endpoint

    "SPRING_REDIS_HOST"     = var.azure_redis_host
    "SPRING_REDIS_PASSWORD" = var.azure_redis_password
    "SPRING_REDIS_PORT"     = "6380"
    "SPRING_REDIS_SSL"      = "true"

    "SPRING_DATA_MONGODB_DATABASE" = var.azure_cosmosdb_mongodb_database
    "SPRING_DATA_MONGODB_URI"      = var.azure_cosmosdb_mongodb_uri
  }
}

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault_access_policy" "application" {
  key_vault_id = var.vault_id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = azurerm_spring_cloud_app.application.identity[0].principal_id

  secret_permissions = [
    "Get",
    "List"
  ]
}
