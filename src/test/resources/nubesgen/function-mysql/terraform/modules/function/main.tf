
# This creates the plan that the service use
resource "azurerm_app_service_plan" "application" {
  name                = "plan-${var.application_name}-001"
  resource_group_name = var.resource_group
  location            = var.location

  kind     = "Linux"
  reserved = true

  sku {
    tier = var.sku_tier
    size = var.sku_size
  }
}

locals {
  // A storage blob cannot contain hyphens, and is limited to 23 characters long
  storage-app-blob-name = substr(replace(var.application_name, "-", ""), 0, 16)
}

resource "azurerm_storage_account" "application" {
  name                      = "stapp${local.storage-app-blob-name}001"
  resource_group_name       = var.resource_group
  location                  = var.location
  account_tier              = "Standard"
  account_replication_type  = "LRS"
  enable_https_traffic_only = true
  allow_blob_public_access  = true
}

# This creates the service definition
resource "azurerm_function_app" "application" {
  name                       = "func-${var.application_name}-001"
  resource_group_name        = var.resource_group
  location                   = var.location
  app_service_plan_id        = azurerm_app_service_plan.application.id
  storage_account_name       = azurerm_storage_account.application.name
  storage_account_access_key = azurerm_storage_account.application.primary_access_key
  os_type                    = "linux"
  https_only                 = true

  site_config {
    linux_fx_version = "java|11"
  }

  app_settings = {
    "WEBSITE_RUN_FROM_PACKAGE"    = "1"
    "FUNCTIONS_EXTENSION_VERSION" = "~3"
    "FUNCTIONS_WORKER_RUNTIME"    = "java"

    # These are app specific environment variables
    "SPRING_PROFILES_ACTIVE"     = "prod,azure"

    "SPRING_DATASOURCE_URL"      = "jdbc:mysql://${var.database_url}?useUnicode=true&characterEncoding=utf8&useSSL=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
    "SPRING_DATASOURCE_USERNAME" = var.database_username
    "SPRING_DATASOURCE_PASSWORD" = var.database_password
  }
}
