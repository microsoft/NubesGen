variable "key_vault_id" {
    type = string
    description = "Azure Key Vault resource id"  
}

variable "certificate_name" {
    type = string
    description = "Certificate name"
}

variable "dns_names" {
    type = list(string)
    description = "DNS names of the certificate"  
}

variable "subject" {
    type = string
    description = "Certificate subject"  
}