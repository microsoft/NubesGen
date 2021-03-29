output "application_hostname" {
  value       = "https://${azurerm_app_service.application.default_site_hostname}"
  description = "The Web application URL."
}
