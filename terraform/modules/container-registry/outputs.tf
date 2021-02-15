output "container_registry_name" {
  value = azurerm_container_registry.container-registry.name
}

output "container_registry_username" {
  value     = azurerm_container_registry.container-registry.admin_username
  sensitive = true
}

output "container_registry_password" {
  value     = azurerm_container_registry.container-registry.admin_password
  sensitive = true
}
