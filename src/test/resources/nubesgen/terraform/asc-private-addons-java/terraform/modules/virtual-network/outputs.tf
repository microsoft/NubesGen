output "virtual_network_id" {
  value       = azurerm_virtual_network.virtual_network.id
  description = "Application Virtual Network"
}

output "app_subnet_id" {
  value       = azurerm_subnet.app_subnet.id
  description = "Application Subnet"
}

output "service_subnet_id" {
  value       = azurerm_subnet.service_subnet.id
  description = "Azure Spring Cloud services subnet"
}

output "addons_subnet_id" {
  value       = azurerm_subnet.addons_subnet.id
  description = "Addons subnet"
}

output "redis_subnet_id" {
  value       = azurerm_subnet.redis_subnet.id
  description = "Azure Redis Cache subnet"
}
