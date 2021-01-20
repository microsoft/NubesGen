variable "resource_group" {
  description = "The resource group"
  default     = ""
}

variable "application_name" {
  description = "The name of your application"
  default     = ""
}

variable "location" {
  description = "The Azure location where all resources in this example should be created"
  default     = ""
}

variable "administrator_login" {
  description = "The PostgreSQL administrator login"
  default     = "postgresqladmin"
}
