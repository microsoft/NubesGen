terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.41"
    }
  }
}

provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "main" {
  name     = var.resource_group
  location = var.location
  tags = {
    "terraform" = "true"
  }
}

module "database" {
  source           = "./modules/mysql"
  resource_group   = var.resource_group
  location         = var.location
  application_name = var.application_name
  depends_on = [
    azurerm_resource_group.main
  ]
}

module "compute" {
  source            = "./modules/app-service"
  resource_group    = var.resource_group
  location          = var.location
  application_name  = var.application_name
  database_url      = module.database.database_url
  database_username = module.database.database_username
  database_password = module.database.database_password
  depends_on = [
    azurerm_resource_group.main,
    module.database
  ]
}
