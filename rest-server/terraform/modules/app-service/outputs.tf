output "application_url" {
  value       = "https://${azurerm_linux_web_app.application.default_hostname}"
  description = "The Web application URL."
}
