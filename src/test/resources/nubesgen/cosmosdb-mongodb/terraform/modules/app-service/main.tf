
# This creates the plan that the service use
resource "azurerm_app_service_plan" "application" {
  name                = "plan-${var.application_name}-001"
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
  name                = "app-${var.application_name}-001"
  resource_group_name = var.resource_group
  location            = var.location
  app_service_plan_id = azurerm_app_service_plan.application.id
  https_only          = true

  tags = {
    "environment" = var.environment
  }

  site_config {
    linux_fx_version          = "JAVA|11-java11"
    always_on                 = false
    use_32_bit_worker_process = true
    ftps_state                = "FtpsOnly"
  }

  app_settings = {
    "WEBSITES_ENABLE_APP_SERVICE_STORAGE" = "false"

    # These are app specific environment variables
    "SPRING_PROFILES_ACTIVE" = "prod,azure"
    "SPRING_SERVER_PORT"     = 80

    "SPRING_DATA_MONGODB_DATABASE" = var.azure_cosmosdb_mongodb_database
    "SPRING_DATA_MONGODB_URI"      = var.azure_cosmosdb_mongodb_uri
  }
}
