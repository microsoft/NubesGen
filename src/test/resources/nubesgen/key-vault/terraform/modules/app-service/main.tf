
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

  identity {
    type = "SystemAssigned"
  }

  app_settings = {
    "WEBSITES_ENABLE_APP_SERVICE_STORAGE" = "false"

    # These are app specific environment variables
    "SPRING_PROFILES_ACTIVE"     = "prod,azure"

    "SPRING_DATASOURCE_URL"      = "jdbc:postgresql://${var.database_url}"
    "SPRING_DATASOURCE_USERNAME" = var.database_username
    "SPRING_DATASOURCE_PASSWORD" = var.database_password
  }
}

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault_access_policy" "application" {
  key_vault_id   = var.vault_id
  tenant_id      = data.azurerm_client_config.current.tenant_id
  object_id      = azurerm_app_service.application.identity[0].principal_id

  secret_permissions = [
    "Get",
    "List"
  ]
}
