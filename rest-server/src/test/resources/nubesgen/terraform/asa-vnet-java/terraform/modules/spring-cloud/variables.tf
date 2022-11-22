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

variable "virtual_network_id" {
  type        = string
  description = "Virtual Network ID where Azure Spring Apps will be deployed"
}

variable "app_subnet_id" {
  type        = string
  description = "Azure Spring Apps apps subnet ID"
}

variable "service_subnet_id" {
  type        = string
  description = "Azure Spring Apps services subnet ID"
}

variable "cidr_ranges" {
  type        = list(string)
  description = "A list of (at least 3) CIDR ranges (at least /16) which are used to host the Azure Spring Apps infrastructure, which must not overlap with any existing CIDR ranges in the Subnet. Changing this forces a new resource to be created"
}
