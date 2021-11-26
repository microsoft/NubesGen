output "application_url" {
  value       = "https://${azurerm_app_service.application.default_site_hostname}"
  description = "The Web application URL."
}
