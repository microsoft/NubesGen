variable "application_name" {
  type        = string
  description = "The name of your application"
  default     = "nubesgen-testapp-vnet"
}

variable "environment" {
  type        = string
  description = "The environment (dev, test, prod...)"
  default     = ""
}

variable "location" {
  type        = string
  description = "The Azure region where all resources in this example should be created"
  default     = "westeurope"
}

variable "address_space" {
  type        = string
  description = "Virtual Network address space"
  default     = "10.11.0.0/16"
}
variable "app_subnet_prefix" {
  type        = string
  description = "Application subnet prefix"
  default     = "10.11.0.0/16"
}
