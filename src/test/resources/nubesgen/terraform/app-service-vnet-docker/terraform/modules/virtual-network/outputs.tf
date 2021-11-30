output "virtual_network_id" {
  value       = azurerm_virtual_network.virtual_network.id
  description = "Application Virtual Network"
}

output "app_subnet_id" {
  value       = azurerm_subnet.app_subnet.id
  description = "Application Subnet"
}

output "redis_subnet_id" {
  value       = azurerm_subnet.redis_subnet.id
  description = "Azure Redis Cache subnet"
}
