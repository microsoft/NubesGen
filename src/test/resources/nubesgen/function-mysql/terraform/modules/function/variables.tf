variable "resource_group" {
  description = "The resource group"
}

variable "application_name" {
  description = "The name of your application"
}

variable "environment" {
  description = "The environment (dev, test, prod...)"
  default     = "dev"
}

variable "location" {
  description = "The Azure region where all resources in this example should be created"
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
