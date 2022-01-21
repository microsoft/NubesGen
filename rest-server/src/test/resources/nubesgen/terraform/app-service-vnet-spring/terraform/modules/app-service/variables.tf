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

variable "subnet_id" {
  type        = string
  description = "The subnet the app can use"
}
