output "application_hostname" {
  value       = module.application.application_hostname
  description = "The Web application URL."
}

output "resource_group" {
  value       = local.resource_group
  description = "The resource group."
}
