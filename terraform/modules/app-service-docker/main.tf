
# This creates the plan that the service use
resource "azurerm_app_service_plan" "application" {
  name                = "plan-${var.application_name}-002"
  resource_group_name = var.resource_group
  location            = var.location

  kind     = "Linux"
  reserved = true

  tags = {
    "environment" = var.environment
  }

  sku {
    tier = "Free"
    size = "F1"
  }
}

# This creates the service definition
resource "azurerm_app_service" "application" {
  name                = "app-${var.application_name}-002"
  resource_group_name = var.resource_group
  location            = var.location
  app_service_plan_id = azurerm_app_service_plan.application.id
  https_only          = true

  tags = {
    "environment" = var.environment
  }

  site_config {
    ftps_state                = "FtpsOnly"
    always_on                 = false
    linux_fx_version          = "DOCKER|${var.container_registry_name}.azurecr.io/nubesgen/nubesgen:latest"
    use_32_bit_worker_process = true
  }

  app_settings = {
    "WEBSITES_ENABLE_APP_SERVICE_STORAGE" = "false"
    "DOCKER_REGISTRY_SERVER_URL"          = "https://${var.container_registry_name}.azurecr.io"
    "DOCKER_REGISTRY_SERVER_USERNAME"     = var.container_registry_username
    "DOCKER_REGISTRY_SERVER_PASSWORD"     = var.container_registry_password
    "WEBSITES_PORT"                       = "8080"

    # These are app specific environment variables
    "SPRING_PROFILES_ACTIVE"     = "prod,azure"
    "AZURE_STORAGE_ACCOUNT_NAME"  = var.azure_storage_account_name
    "AZURE_STORAGE_ACCOUNT_KEY"   = var.azure_storage_account_key
    "AZURE_STORAGE_BLOB_ENDPOINT" = var.azure_storage_blob_endpoint
  }
}
