output "azure_cosmosdb_mongodb_database" {
  value       = azurerm_cosmosdb_mongo_database.cosmosdb.name
  description = "The Cosmos DB database name."
}

output "azure_cosmosdb_mongodb_uri" {
  value       = azurerm_cosmosdb_account.cosmosdb.connection_strings[0]
  sensitive   = true
  description = "The Cosmos DB connection string."
}

output "azure_cosmosdb_account_uri" {
  value       = azurerm_cosmosdb_account.cosmosdb.id
  description = "The Cosmos account resource id."
}

output "azure_cosmosdb_account_key" {
  value       = azurerm_cosmosdb_account.cosmosdb.primary_key
  sensitive   = true
  description = "The Cosmos account access key."
}