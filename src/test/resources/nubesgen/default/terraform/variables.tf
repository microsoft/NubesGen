variable "resource_group" {
  description = "The resource group"
  default     = "test-resource-group"
}

variable "application_name" {
  description = "The name of your application"
  default     = "nubesgen-testapp"
}

variable "location" {
  description = "The Azure location where all resources in this example should be created"
  default     = "westeurope"
}
