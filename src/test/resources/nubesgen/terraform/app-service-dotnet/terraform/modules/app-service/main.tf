terraform {
  required_providers {
    azurecaf = {
      source = "aztfmod/azurecaf"
      version = "1.2.6"
    }
  }
}

resource "azurecaf_name" "app_service_plan" {
  name            = var.application_name
  resource_type   = "azurerm_app_service_plan"
  suffixes        = [var.environment]
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
    tier = "Free"
    size = "F1"
  }
}

resource "azurecaf_name" "app_service" {
  name            = var.application_name
  resource_type   = "azurerm_app_service"
  suffixes        = [var.environment]
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
    linux_fx_version          = "DOTNETCORE|3.1"
    always_on                 = false
    use_32_bit_worker_process = true
    ftps_state                = "FtpsOnly"
  }

  app_settings = {
    "WEBSITES_ENABLE_APP_SERVICE_STORAGE" = "false"

    # These are app specific environment variables

    "DATABASE_URL"      = var.database_url
    "DATABASE_USERNAME" = var.database_username
    "DATABASE_PASSWORD" = var.database_password
  }
}
