output "application_hostname" {
  description = "Application Hostname"
  value       = "https://${azurerm_app_service.application.default_site_hostname}"
}
