terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.16"
    }
  }
}

resource "azurecaf_name" "storage_account" {
  name          = var.application_name
  resource_type = "azurerm_storage_account"
  suffixes      = [var.environment]
}

resource "azurerm_storage_account" "storage-blob" {
  name                     = azurecaf_name.storage_account.result
  resource_group_name      = var.resource_group
  location                 = var.location
  account_tier             = "Standard"
  account_replication_type = "LRS"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurerm_storage_account_network_rules" "storage_only_app_traffic" {
  storage_account_id = azurerm_storage_account.storage-blob.id

  default_action             = "Deny"
  virtual_network_subnet_ids = [var.subnet_id]
  ip_rules                   = [var.myip]
}

resource "azurecaf_name" "storage_container" {
  name          = var.application_name
  resource_type = "azurerm_storage_container"
  suffixes      = [var.environment]
}

resource "azurerm_storage_container" "storage-blob" {
  name                  = azurecaf_name.storage_container.result
  storage_account_name  = azurerm_storage_account.storage-blob.name
  container_access_type = "private"
}
