terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "3.72.0"
    }
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.26"
    }
  }
  backend "azurerm" {}
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
    "nubesgen-version" = "0.14.2-SNAPSHOT"

    // Name of the Azure Storage Account that stores the Terraform state
    "terraform_storage_account" = var.terraform_storage_account
  }
}

module "application" {
  source           = "./modules/container-apps"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location

  azure_storage_account_name     = module.storage-blob.azurerm_storage_account_name
  azure_storage_blob_endpoint    = module.storage-blob.azurerm_storage_blob_endpoint
  azure_storage_account_key      = module.storage-blob.azurerm_storage_account_key
  custom_domain_name             = var.custom_domain_name
  container_certificate          = var.container_certificate
  container_certificate_password = var.container_certificate_password
}

module "storage-blob" {
  source           = "./modules/storage-blob"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
}
