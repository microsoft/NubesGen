variable "resource_group" {
  type        = string
  description = "The resource group"
  default     = ""
}

variable "application_name" {
  type        = string
  description = "The name of your application"
  default     = ""
}

variable "environment" {
  type        = string
  description = "The environment (dev, test, prod...)"
  default     = "dev"
}

variable "location" {
  type        = string
  description = "The Azure region where all resources in this example should be created"
  default     = ""
}

variable "sku_tier" {
  type        = string
  description = "The Azure Cache for Redis tier"
  default     = "Basic"
}

variable "subnet_id" {
  type = string
  description = "The subnet where the Redis cluster will be deployed. Do not place any other service in this subnet."
}
