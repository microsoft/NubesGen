
locals {
  // An Azure Container Registry name cannot contain hyphens, and is limited to 50 characters long
  azure-container-registry-name = substr(replace(var.application_name, "-", ""), 0, 46)
}

resource "azurerm_container_registry" "container-registry" {
  name                     = "acr${local.azure-container-registry-name}001"
  resource_group_name      = var.resource_group
  location                 = var.location
  admin_enabled            = true
  sku                      = "Basic"

  tags = {
    "environment" = var.environment
  }
}
