terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.9"
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

resource "azurecaf_name" "private_endpoint" {
  name          = var.application_name
  resource_type = "azurerm_private_endpoint"
  suffixes      = [var.environment, "storage"]
}

resource "azurerm_private_endpoint" "private_endpoint" {
  name                = azurecaf_name.private_endpoint.result
  location            = var.location
  resource_group_name = var.resource_group
  subnet_id           = var.subnet_id

  private_service_connection {
    name                           = "${azurerm_storage_account.storage-blob.name}-pe"
    is_manual_connection           = false
    private_connection_resource_id = azurerm_storage_account.storage-blob.id
    subresource_names              = ["Blob"]
  }
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
