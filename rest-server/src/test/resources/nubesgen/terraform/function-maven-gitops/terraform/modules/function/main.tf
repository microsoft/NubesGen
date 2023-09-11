terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.26"
    }
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

  sku_name = "Y1"
  os_type  = "Linux"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurecaf_name" "storage_account" {
  random_length = "15"
  resource_type = "azurerm_storage_account"
  suffixes      = [var.environment]
}

resource "azurerm_storage_account" "application" {
  name                             = azurecaf_name.storage_account.result
  resource_group_name              = var.resource_group
  location                         = var.location
  account_tier                     = "Standard"
  account_replication_type         = "LRS"
  enable_https_traffic_only        = true
  allow_nested_items_to_be_public  = false

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurecaf_name" "function_app" {
  name          = var.application_name
  resource_type = "azurerm_function_app"
  suffixes      = [var.environment]
}

# This creates the service definition
resource "azurerm_linux_function_app" "application" {
  name                        = azurecaf_name.function_app.result
  resource_group_name         = var.resource_group
  location                    = var.location
  service_plan_id             = azurerm_service_plan.application.id
  storage_account_name        = azurerm_storage_account.application.name
  storage_account_access_key  = azurerm_storage_account.application.primary_access_key
  https_only                  = true
  functions_extension_version = "~4"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }

  site_config {
    application_stack {
      java_version = "11"
    }
  }

  app_settings = {
    "WEBSITE_RUN_FROM_PACKAGE"    = "1"

    # These are app specific environment variables
    "SPRING_PROFILES_ACTIVE" = "prod,azure"
  }
}
