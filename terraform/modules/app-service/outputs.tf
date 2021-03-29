output "application_hostname" {
  description = "The Web application URL."
  value       = "https://${azurerm_app_service.application.default_site_hostname}"
}
