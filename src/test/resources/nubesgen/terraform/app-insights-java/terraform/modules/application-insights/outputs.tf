output "azure_application_insights_instrumentation_key" {
  value       = azurerm_application_insights.insights.instrumentation_key
  description = "The Azure Application Insights instrumentation key"
}
