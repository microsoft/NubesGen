locals {
  // A storage blob cannot contain hyphens, and is limited to 23 characters long
  storage-blob-name = substr(replace(var.application_name, "-", ""), 0, 13)
}

// random string for have unique storage account name


resource "azurerm_storage_account" "account" {
  name                     = "st${local.storage-blob-name}${var.unique_suffix_name}"
  resource_group_name      = var.resource_group
  location                 = var.location
  account_tier             = "Standard"
  account_replication_type = "LRS"

  tags = var.tags
}

resource "azurerm_storage_container" "blob-container" {
  name                  = "stblob${local.storage-blob-name}${var.unique_suffix_name}"
  storage_account_name  = azurerm_storage_account.account.name
  container_access_type = "private"
}
