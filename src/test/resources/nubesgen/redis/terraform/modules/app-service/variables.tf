variable "resource_group" {
  description = "The resource group"
}

variable "application_name" {
  description = "The name of your application"
}

variable "location" {
  description = "The Azure region where all resources in this example should be created"
}

variable "azure_redis_host" {
  description = "The Azure Cache for Redis hostname"
}

variable "azure_redis_password" {
  description = "The Azure Cache for Redis password"
}
