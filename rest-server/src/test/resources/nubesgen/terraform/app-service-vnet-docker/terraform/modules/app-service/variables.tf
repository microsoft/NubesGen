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

variable "database_url" {
  type        = string
  description = "The URL to the database"
}

variable "database_username" {
  type        = string
  description = "The database username"
}

variable "database_password" {
  type        = string
  description = "The database password"
}

variable "azure_application_insights_instrumentation_key" {
  type        = string
  description = "The Azure Application Insights instrumentation key"
}

variable "vault_id" {
  type        = string
  description = "The Azure Key Vault ID"
}

variable "azure_storage_account_name" {
  type        = string
  description = "The name of the Azure Storage account"
}

variable "azure_storage_account_key" {
  type        = string
  description = "The access key of the Azure Storage account"
}

variable "azure_storage_blob_endpoint" {
  type        = string
  description = "The blob endpoint URL of the Azure Storage account"
}

variable "azure_redis_host" {
  type        = string
  description = "The Azure Cache for Redis hostname"
}

variable "azure_redis_password" {
  type        = string
  description = "The Azure Cache for Redis password"
}

variable "azure_cosmosdb_mongodb_database" {
  type        = string
  description = "The Cosmos DB with MongoDB API database name"
}

variable "azure_cosmosdb_mongodb_uri" {
  type        = string
  description = "The Cosmos DB with MongoDB API database URI"
}

variable "subnet_id" {
  type        = string
  description = "The subnet the app can use"
}
