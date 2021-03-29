output "application_hostname" {
  value       = "https://${azurerm_function_app.application.default_hostname}"
  description = "The Azure Functions application URL."
}
