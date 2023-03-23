variable "application_name" {
  type        = string
  description = "The name of your application"
  default     = "nubesgen"
}

variable "terraform_storage_account" {
  type        = string
  description = "When using an Azure back-end, the name of the Azure Storage Account that stores the Terraform state"
  default     = ""
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

variable "container_certificate" {
  type        = string
  sensitive   = true
  description = "The domain certificate for Azure Container Apps"
}

variable "container_certificate_password" {
  type        = string
  sensitive   = true
  description = "The password for the domain certificate for Azure Container Apps"
}
