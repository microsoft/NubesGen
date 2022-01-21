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

variable "azure_redis_host" {
  type        = string
  description = "The Azure Cache for Redis hostname"
}

variable "azure_redis_password" {
  type        = string
  description = "The Azure Cache for Redis password"
}
