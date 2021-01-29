variable "resource_group" {
  description = "The resource group"
}

variable "application_name" {
  description = "The name of your application"
}

variable "location" {
  description = "The Azure region where all resources in this example should be created"
}

variable "azure_cosmosdb_mongodb_database" {
  description = "The Cosmos DB with MongoDB API database name"
}

variable "azure_cosmosdb_mongodb_uri" {
  description = "The Cosmos DB with MongoDB API database URI"
}
