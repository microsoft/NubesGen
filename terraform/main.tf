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

module "compute" {
  source            = "./modules/app-service"
  resource_group    = var.resource_group
  application_name  = var.application_name
  location          = var.location
  depends_on = [
    azurerm_resource_group.main
  ]
}
