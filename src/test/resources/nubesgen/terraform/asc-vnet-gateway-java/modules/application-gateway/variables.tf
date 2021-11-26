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
}

variable "location" {
  type        = string
  description = "The Azure region where all resources in this example should be created"
}

variable "network_name" {
  type        = string
  description = "Virtual network name"
}

variable "gateway_frontend_subnet_id" {
  type        = string
  description = "Gateway subnet id"
}

variable "backend_address" {
  type        = string
  description = "target address to send the requests"
}

variable "user_managed_identity_id" {
  type        = string
  description = "User managed identity id to assign to application gateway"
}

variable "key_vault_certificate_id" {
  type        = string
  description = "certificate id used for SSL"
}

variable "root_certificate"{
  type = string
  description = "Root certificate file content"
  sensitive = true
}

variable "root_certificate_name" {
  type = string
  description = "Root certificate name"
}

variable "host_name" {
  type = string
  description = "public hostname"  
}

variable "public_ip_id" {
  type=string
  description = "Public IP azure resource id"
  
}
