terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.26"
    }
  }
}

resource "azurecaf_name" "application_insights" {
  name          = var.application_name
  resource_type = "azurerm_application_insights"
  suffixes      = [var.environment]
}

resource "azurerm_application_insights" "application_insights" {
  name                = azurecaf_name.application_insights.result
  location            = var.location
  resource_group_name = var.resource_group
  application_type    = "other"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}
