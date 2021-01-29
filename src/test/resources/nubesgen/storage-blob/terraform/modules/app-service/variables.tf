variable "resource_group" {
  description = "The resource group"
}

variable "application_name" {
  description = "The name of your application"
}

variable "location" {
  description = "The Azure region where all resources in this example should be created"
}

variable "azure_storage_account_name" {
  description = "The name of the Azure Storage account"
}

variable "azure_storage_account_key" {
  description = "The access key of the Azure Storage account"
}

variable "azure_storage_blob_endpoint" {
  description = "The blob endpoint URL of the Azure Storage account"
}
