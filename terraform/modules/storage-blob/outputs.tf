output "azurerm_storage_account_name" {
  description = "Storage Account Name"
  value       = azurerm_storage_account.storage-blob.name
}

output "azurerm_storage_account_key" {
  description = "Storage Account Key (sensitive)"
  value       = azurerm_storage_account.storage-blob.primary_access_key
  sensitive   = true
}

output "azurerm_storage_blob_endpoint" {
  description = "Storage Blob Endpoint"
  value       = azurerm_storage_account.storage-blob.primary_blob_endpoint
}
