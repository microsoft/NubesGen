
data "azurerm_client_config" "current" {}

resource "azurerm_key_vault" "application" {
  name                = "kv-${var.application_name}-001"
  resource_group_name = var.resource_group
  location            = var.location

  tenant_id                   = data.azurerm_client_config.current.tenant_id
  soft_delete_retention_days  = 90

  sku_name = "standard"

  access_policy {
    tenant_id = data.azurerm_client_config.current.tenant_id
    object_id = data.azurerm_client_config.current.object_id

    secret_permissions = [
      "set",
      "get",
      "delete",
      "purge",
      "recover"
    ]
  }

  tags = {
    "environment" = var.environment
  }
}

resource "azurerm_key_vault_secret" "database" {
  name         = "database_url"
  value        = var.database_url
  key_vault_id = azurerm_key_vault.application.id
}

resource "azurerm_key_vault_secret" "database" {
  name         = "database_username"
  value        = var.database_username
  key_vault_id = azurerm_key_vault.application.id
}

resource "azurerm_key_vault_secret" "database" {
  name         = "database_password"
  value        = var.database_password
  key_vault_id = azurerm_key_vault.application.id
}
