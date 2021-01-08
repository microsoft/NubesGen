variable "resource_group" {
  description = "The resource group"
}

variable "application_name" {
  description = "The name of your application"
}

variable "location" {
  description = "The Azure location where all resources in this example should be created"
}

variable "sku_tier" {
  description = "The Azure App Service plan tier"
  default     = "PremiumV2"
}

variable "sku_size" {
  description = "The Azure App Service plan size"
  default     = "B1"
}
