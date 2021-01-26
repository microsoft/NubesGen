terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.42"
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

module "application" {
  source            = "./modules/app-service"
  resource_group    = var.resource_group
  location          = var.location
  application_name  = var.application_name

  azure_cosmosdb_mongodb_database = module.cosmosdb-mongodb.azure_cosmosdb_mongodb_database
  azure_cosmosdb_mongodb_uri      = module.cosmosdb-mongodb.azure_cosmosdb_mongodb_uri

  depends_on = [
    module.cosmosdb-mongodb,
    azurerm_resource_group.main
  ]
}

module "cosmosdb-mongodb" {
  source           = "./modules/cosmosdb-mongodb"
  resource_group   = var.resource_group
  location         = var.location
  application_name = var.application_name
  depends_on = [
    azurerm_resource_group.main
  ]
}
