terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.86"
    }
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.9"
    }
  }
}

provider "azurerm" {
  features {}
}

locals {
  // If an environment is set up (dev, test, prod...), it is used in the application name
  environment = var.environment == "" ? "dev" : var.environment
}

resource "azurecaf_name" "resource_group" {
  name          = var.application_name
  resource_type = "azurerm_resource_group"
  suffixes      = [local.environment]
}

resource "azurerm_resource_group" "main" {
  name     = azurecaf_name.resource_group.result
  location = var.location

  tags = {
    "terraform"        = "true"
    "environment"      = local.environment
    "application-name" = var.application_name
  }
}

module "application" {
  source           = "./modules/spring-cloud"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location

  database_url      = module.database.database_url
  database_username = module.database.database_username
  database_password = module.database.database_password

  virtual_network_id = module.network.virtual_network_id
  app_subnet_id      = module.network.app_subnet_id
  service_subnet_id  = module.network.service_subnet_id
  cidr_ranges        = var.cidr_ranges
}

module "database" {
  source           = "./modules/sql-server"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
  subnet_id        = module.network.app_subnet_id
}

module "network" {
  source           = "./modules/virtual-network"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location

  service_endpoints = ["Microsoft.Sql"]

  address_space     = var.address_space
  app_subnet_prefix = var.app_subnet_prefix

  service_subnet_prefix = var.service_subnet_prefix
}
