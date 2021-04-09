output "azurerm_storage_account_name" {
  value       = azurerm_storage_account.account.name
  description = "The Azure Blob storage account name."
}

output "azurerm_storage_account_key" {
  value       = azurerm_storage_account.account.primary_access_key
  sensitive   = true
  description = "The Azure Blob storage access key."
}

output "azurerm_storage_blob_endpoint" {
  value       = azurerm_storage_account.account.primary_blob_endpoint
  description = "The Azure Blob storage endpoint."
}