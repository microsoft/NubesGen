terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.54"
    }
  }
  # backend "azurerm" {}
}

provider "azurerm" {
  features {}
}

locals {
  application_name = var.environment == "dev" ? var.application_name : "${var.application_name}-${var.environment}"
  resource_group   = "rg-${local.application_name}"
}

resource "random_id" "random" {
  byte_length = 5
}

resource "azurerm_resource_group" "rg" {
  name     = "${local.resource_group}-${lower(random_id.random.hex)}"
  location = var.location
  tags = {
    "terraform"   = "true"
    "environment" = var.environment
  }
}

module "application" {
  source             = "./modules/app-service"
  resource_group     = azurerm_resource_group.rg.name
  application_name   = local.application_name
  unique_suffix_name = lower(random_id.random.hex)
  location           = azurerm_resource_group.rg.location

  tags = azurerm_resource_group.rg.tags

  azure_storage_account_name  = module.storage-blob.azurerm_storage_account_name
  azure_storage_account_key   = module.storage-blob.azurerm_storage_account_key
  azure_storage_blob_endpoint = module.storage-blob.azurerm_storage_blob_endpoint
}

module "storage-blob" {
  source             = "./modules/storage-blob"
  resource_group     = azurerm_resource_group.rg.name
  unique_suffix_name = lower(random_id.random.hex)
  application_name   = local.application_name
  location           = azurerm_resource_group.rg.location
  tags               = azurerm_resource_group.rg.tags
}
