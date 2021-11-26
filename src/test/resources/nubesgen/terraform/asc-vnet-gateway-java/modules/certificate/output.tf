output "certificate_id" {
  value = azurerm_key_vault_certificate.certificate.id
}

output "certificate_object" {
  value = azurerm_key_vault_certificate.certificate
}
