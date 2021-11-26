terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.6"
    }
  }
}

resource "azurecaf_name" "virtual_network" {
  name          = var.application_name
  resource_type = "azurerm_virtual_network"
  suffixes      = [var.environment]
}

resource "azurerm_virtual_network" "virtual_network" {
  name                = azurecaf_name.virtual_network.result
  address_space       = [var.address_space]
  location            = var.location
  resource_group_name = var.resource_group
}

resource "azurecaf_name" "service_subnet" {
  name          = var.application_name
  resource_type = "azurerm_subnet"
  suffixes      = [var.environment, "svc"]
}

resource "azurerm_subnet" "service_subnet" {
  name                 = azurecaf_name.service_subnet.result
  resource_group_name  = var.resource_group
  virtual_network_name = azurerm_virtual_network.virtual_network.name
  address_prefixes     = [var.service_subnet_prefix]
}

resource "azurecaf_name" "app_subnet" {
  name          = var.application_name
  resource_type = "azurerm_subnet"
  suffixes      = [var.environment, "app"]
}

resource "azurerm_subnet" "app_subnet" {
  name                 = azurecaf_name.app_subnet.result
  resource_group_name  = var.resource_group
  virtual_network_name = azurerm_virtual_network.virtual_network.name
  address_prefixes     = [var.app_subnet_prefix]
  service_endpoints    = var.service_endpoints
}

resource "azurecaf_name" "gateway_subnet" {
  name          = var.application_name
  resource_type = "azurerm_subnet"
  suffixes      = [var.environment, "gateway"]
}

resource "azurerm_subnet" "gateway_subnet" {
  name                 = azurecaf_name.gateway_subnet.result
  resource_group_name  = var.resource_group
  virtual_network_name = azurerm_virtual_network.virtual_network.name
  address_prefixes     = [var.gateway_subnet_prefix]
  # service_endpoints    = var.service_endpoints
}