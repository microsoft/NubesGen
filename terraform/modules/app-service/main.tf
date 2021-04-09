# This creates the plan that the service use
resource "azurerm_app_service_plan" "plan" {
  name                = "plan-${var.application_name}-${var.unique_suffix_name}"
  resource_group_name = var.resource_group
  location            = var.location

  kind     = "Linux"
  reserved = true

  tags = var.tags

  sku {
    tier = var.sku_tier
    size = var.sku_size
  }
}

# This creates the service definition
resource "azurerm_app_service" "application" {
  name                = "${var.application_name}-${var.unique_suffix_name}"
  resource_group_name = var.resource_group
  location            = var.location
  app_service_plan_id = azurerm_app_service_plan.plan.id
  https_only          = true

  tags = var.tags

  site_config {
    ftps_state       = "FtpsOnly"
    always_on        = true
    linux_fx_version = "JAVA|11-java11"
  }

  app_settings = {
    "WEBSITES_ENABLE_APP_SERVICE_STORAGE" = "false"

    # These are app specific environment variables
    "SPRING_PROFILES_ACTIVE"      = "prod,azure"
    "AZURE_STORAGE_ACCOUNT_NAME"  = var.azure_storage_account_name
    "AZURE_STORAGE_ACCOUNT_KEY"   = var.azure_storage_account_key
    "AZURE_STORAGE_BLOB_ENDPOINT" = var.azure_storage_blob_endpoint

    "APPINSIGHTS_INSTRUMENTATIONKEY" = azurerm_application_insights.insight.instrumentation_key
  }
}

# create application insight for monitoring the app service
resource "azurerm_application_insights" "insight" {
  name                = "${var.application_name}-${var.unique_suffix_name}"
  resource_group_name = var.resource_group
  location            = var.location
  application_type    = "web"
  tags                = var.tags
}