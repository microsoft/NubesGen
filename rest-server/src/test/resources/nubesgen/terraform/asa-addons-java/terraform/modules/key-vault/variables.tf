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

variable "cosmosdb_mongodb_uri" {
  type        = string
  description = "The Cosmos DB connection string"
}
