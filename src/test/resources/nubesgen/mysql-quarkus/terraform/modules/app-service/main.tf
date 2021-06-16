
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
    "QUARKUS_HTTP_PORT" = 80
    "QUARKUS_PROFILE"   = "prod"

    "QUARKUS_DATASOURCE_JDBC_URL" = "jdbc:mysql://${var.database_url}?useUnicode=true&characterEncoding=utf8&useSSL=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
    "QUARKUS_DATASOURCE_USERNAME" = var.database_username
    "QUARKUS_DATASOURCE_PASSWORD" = var.database_password
  }
}
