variable "application_name" {
  type        = string
  description = "The name of your application"
  default     = "fmiguelascafd"
}

variable "environment" {
  type        = string
  description = "The environment (dev, test, prod...)"
  default     = ""
}

variable "location" {
  type        = string
  description = "The Azure region where all resources in this example should be created"
  default     = "northeurope"
}

variable "address_space" {
  type        = string
  description = "Virtual Network address space"
  default     = "10.11.0.0/16"
}

variable "app_subnet_prefix" {
  type        = string
  description = "Application subnet prefix"
  default     = "10.11.1.0/24"
}

variable "service_subnet_prefix" {
  type        = string
  description = "Azure Spring Cloud service subnet prefix"
  default     = "10.11.2.0/24"
}
variable "gateway_subnet_prefix" {
  type        = string
  description = "Gateway subnet prefix"
  default     = "10.11.3.0/24"
}

variable "cidr_ranges" {
  type        = list(string)
  description = "A list of (at least 3) CIDR ranges (at least /16) which are used to host the Spring Cloud infrastructure, which must not overlap with any existing CIDR ranges in the Subnet. Changing this forces a new resource to be created"
  default     = ["10.4.0.0/16", "10.5.0.0/16", "10.3.0.1/16"]
}

# variable "main_domain" {
#   type = string
#   description = "Application public domain"
#   default = "maxinquepa.com"
# }

# variable "dns_names" {
#     type = list(string)
#     description = "DNS names of the certificate"  
#     default = [ "myapp.mydomain.com", "*.myapp.mydomain.com" ]
# }

variable "cert_subjects" {
    type = string
    description = "Certificate subject"
    # default = "C=US, ST=WA, L=Redmond, O=Contoso, OU=Contoso HR, CN=myapp.mydomain.com"  
    default = "C=US, ST=WA, L=Redmond, O=Contoso, OU=Contoso HR"
}
