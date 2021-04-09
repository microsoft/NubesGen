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

variable "unique_suffix_name" {
  type        = string
  description = "Unique random suffix"
}

variable "location" {
  type        = string
  description = "The Azure region where all resources in this example should be created"
  default     = ""
}

variable "tags" {
  type        = map(string)
  description = "Tags of the storage"
}