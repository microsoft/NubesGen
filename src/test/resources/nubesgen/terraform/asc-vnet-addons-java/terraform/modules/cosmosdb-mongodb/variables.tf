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

variable "throughput" {
  type        = number
  description = "The throughput of the MongoDB database (RU/s)"
  default     = 400
}

variable "subnet_id" {
  type = string
  description = "The subnet from which the access is allowed"
}
