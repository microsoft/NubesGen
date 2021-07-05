
resource "azurerm_application_insights" "insights" {
  name                = "appi-${var.application_name}-001"
  location            = var.location
  resource_group_name = var.resource_group
  application_type    = "java"

  tags = {
    "environment" = var.environment
  }
}
