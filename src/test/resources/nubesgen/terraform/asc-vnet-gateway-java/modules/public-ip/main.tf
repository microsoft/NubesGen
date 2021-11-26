terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.6"
    }
  }
}
resource "azurecaf_name" "public_ip" {
  name          = var.application_name
  resource_type = "azurerm_public_ip"
  suffixes      = [var.environment]
}

resource "azurerm_public_ip" "public_ip" {
  name                = azurecaf_name.public_ip.result
  resource_group_name = var.resource_group
  location            = var.location
  allocation_method   = "Static"
  sku                 = "Standard"
  domain_name_label   = var.application_name
}