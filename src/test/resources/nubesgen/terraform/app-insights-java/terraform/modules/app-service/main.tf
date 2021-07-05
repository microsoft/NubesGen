
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
    tier = "Basic"
    size = "B1"
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
    linux_fx_version = "JAVA|11-java11"
    always_on        = true
    ftps_state       = "FtpsOnly"
  }

  app_settings = {
    "WEBSITES_ENABLE_APP_SERVICE_STORAGE" = "false"

    // Monitoring with Azure Application Insights
    "APPINSIGHTS_INSTRUMENTATIONKEY" = var.azure_application_insights_instrumentation_key

    # These are app specific environment variables
  }
}
