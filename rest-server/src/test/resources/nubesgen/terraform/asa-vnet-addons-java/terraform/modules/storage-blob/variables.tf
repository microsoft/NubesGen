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

variable "subnet_id" {
  type        = string
  description = "The subnet from which the access is allowed"
}

variable "myip" {
  type        = string
  description = "The IP address of the current client. It is required to provide access to this client to be able to create containers"
}
