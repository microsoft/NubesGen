variable "resource_group" {
  description = "The resource group"
}

variable "application_name" {
  description = "The name of your application"
}

variable "location" {
  description = "The Azure region where all resources in this example should be created"
}

variable "sku_tier" {
  description = "The Azure App Service plan tier"
  default     = "PremiumV2"
}

variable "sku_size" {
  description = "The Azure App Service plan size"
  default     = "P1v2"
}

variable "azure_cosmosdb_mongodb_database" {
  description = "The Cosmos DB with MongoDB API database name"
}

variable "azure_cosmosdb_mongodb_uri" {
  description = "The Cosmos DB with MongoDB API database URI"
}
