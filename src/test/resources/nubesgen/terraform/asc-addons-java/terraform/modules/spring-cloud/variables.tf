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

variable "azure_application_insights_connection_string" {
  type        = string
  description = "The Azure Application Insights connection string"
}

variable "vault_id" {
  type        = string
  description = "The Azure Key Vault ID"
}

variable "azure_storage_account_name" {
  type        = string
  description = "The name of the Azure Storage account"
}

variable "azure_storage_blob_endpoint" {
  type        = string
  description = "The blob endpoint URL of the Azure Storage account"
}

variable "azure_redis_host" {
  type        = string
  description = "The Azure Cache for Redis hostname"
}

variable "azure_cosmosdb_mongodb_database" {
  type        = string
  description = "The Cosmos DB with MongoDB API database name"
}
