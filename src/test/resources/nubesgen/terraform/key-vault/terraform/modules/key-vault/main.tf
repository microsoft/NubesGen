terraform {
  required_providers {
    azurecaf = {
      source = "aztfmod/azurecaf"
      version = "1.2.6"
    }
  }
}

resource "random_string" "key_vault" {
  length  = 20
  special = false
  upper   = false
}

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault" "application" {
  name                = "kv-${random_string.key_vault.result}"
  resource_group_name = var.resource_group
  location            = var.location

  tenant_id                  = data.azurerm_client_config.current.tenant_id
  soft_delete_retention_days = 90

  sku_name = "standard"

  access_policy {
    tenant_id = data.azurerm_client_config.current.tenant_id
    object_id = data.azurerm_client_config.current.object_id

    secret_permissions = [
      "Set",
      "Get",
      "Delete",
      "Purge",
      "Recover"
    ]
  }

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurerm_key_vault_secret" "database_username" {
  name         = "database-username"
  value        = var.database_username
  key_vault_id = azurerm_key_vault.application.id
}

resource "azurerm_key_vault_secret" "database_password" {
  name         = "database-password"
  value        = var.database_password
  key_vault_id = azurerm_key_vault.application.id
}
