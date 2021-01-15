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
  default     = "Dynamic"
}

variable "sku_size" {
  description = "The Azure App Service plan size"
  default     = "Y1"
}

variable "database_url" {
  description = "The URL to the database"
}

variable "database_username" {
  description = "The database username"
}

variable "database_password" {
  description = "The database password"
}
