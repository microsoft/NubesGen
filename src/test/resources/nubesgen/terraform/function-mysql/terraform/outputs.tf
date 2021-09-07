output "application_hostname" {
  value       = module.application.application_hostname
  description = "The Web application URL."
}

output "resource_group" {
  value       = azurecaf_name.resource_group.name
  description = "The resource group."
}
