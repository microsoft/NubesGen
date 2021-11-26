output "public_ip_id" {
    value = azurerm_public_ip.public_ip.id  
}
output "fqdn" {
  value = azurerm_public_ip.public_ip.fqdn
}
