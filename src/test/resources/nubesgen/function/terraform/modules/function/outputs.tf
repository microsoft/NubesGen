output "application_hostname" {
  value = "https://${azurerm_function_app.compute.default_hostname}"
}
