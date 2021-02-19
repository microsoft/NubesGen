variable "application_name" {
  description = "The name of your application"
  default     = "nubesgen"
}

variable "environment" {
  description = "The environment (dev, test, prod...)"
  default     = ""
}

variable "location" {
  description = "The Azure region where all resources in this example should be created"
  default     = "westeurope"
}
