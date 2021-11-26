terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.6"
    }
  }
}

resource "azurecaf_name" "key_vault" {
  name          = var.application_name
  resource_type = "azurerm_key_vault"
  suffixes      = [var.environment]
}

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault" "application" {
  name                = azurecaf_name.key_vault.result
  resource_group_name = var.resource_group
  location            = var.location

  tenant_id                  = data.azurerm_client_config.current.tenant_id
  soft_delete_retention_days = 90

  sku_name = "standard"

  network_acls {
    default_action             = "Allow"
    bypass                     = "None"
    virtual_network_subnet_ids = [var.subnet_id]
  }

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}


resource "azurerm_key_vault_access_policy" "current_user_policy" {
  key_vault_id = azurerm_key_vault.application.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = data.azurerm_client_config.current.object_id

  secret_permissions = [
    "Set",
    "Get",
    "Delete",
    "Purge",
    "Recover"
  ]

  certificate_permissions = [
    "Backup", 
    "Create",
    "Delete", 
    "DeleteIssuers", 
    "Get", 
    "GetIssuers", 
    "Import", 
    "List",
    "ListIssuers",
    "ManageContacts", 
    "ManageIssuers", 
    "Purge", 
    "Recover", 
    "Restore", 
    "SetIssuers",
    "Update"
  ]
}

resource "azurerm_key_vault_access_policy" "allowed_ids" {
  count        = length(var.allowed_identities)
  key_vault_id = azurerm_key_vault.application.id
  tenant_id    = var.allowed_identities[count.index].tenant_id
  object_id    = var.allowed_identities[count.index].principal_id
  secret_permissions = [
    "Get",
    "List"
  ]

  certificate_permissions = [
    "Get",
    "List"
  ]
}
