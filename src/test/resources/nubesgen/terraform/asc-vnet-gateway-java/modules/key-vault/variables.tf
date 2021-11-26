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

variable "subnet_id" {
  type        = string
  description = "The subnet from which the access is allowed"
}

variable "allowed_identities" {
  type = list(object({
    tenant_id    = string
    principal_id = string
  }))
  description = "list of object ids that can get and list secrets and certificates"
  default     = null
}
