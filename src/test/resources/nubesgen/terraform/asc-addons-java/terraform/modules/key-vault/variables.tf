variable "resource_group" {
  type        = string
  description = "The resource group"
}

variable "application_name" {
  type        = string
  description = "The name of your application"
}

variable "environment" {
  type        = string
  description = "The environment (dev, test, prod...)"
  default     = "dev"
}

variable "location" {
  type        = string
  description = "The Azure region where all resources in this example should be created"
}

variable "redis_password" {
  type        = string
  description = "The Redis password"
}

variable "storage_account_key" {
  type        = string
  description = "The Azure Storage Account key"
}

variable "cosmosdb_account_id" {
  type        = string
  description = "The Cosmos DB account id"
}
variable "cosmosdb_mongodb_database" {
  type        = string
  description = "The Cosmos DB database"
}
variable "cosmosdb_mongodb_key" {
  type        = string
  description = "The Cosmos DB access key"
}
