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

variable "sku_tier" {
  type        = string
  description = "The Azure App Service plan tier"
  default     = "PremiumV2"
}

variable "sku_size" {
  type        = string
  description = "The Azure App Service plan size"
  default     = "P1v2"
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

variable "tags" {
  type        = map(string)
  description = "Tags of app service"
}