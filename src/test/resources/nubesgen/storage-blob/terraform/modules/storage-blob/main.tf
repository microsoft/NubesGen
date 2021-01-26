
locals {
  // A storage blob account cannot contain hyphens, and is limited to 23 characters long
  storage-blob-name = substr(replace(var.application_name, "-", ""), 0, 19)
}

resource "azurerm_storage_account" "storage-blob" {
  name                     = "st${local.storage-blob-name}001"
  resource_group_name      = var.resource_group
  location                 = var.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

resource "azurerm_storage_container" "storage-blob" {
  name                  = "stblob${local.storage-blob-name}001"
  storage_account_name  = azurerm_storage_account.storage-blob.name
  container_access_type = "private"
}
