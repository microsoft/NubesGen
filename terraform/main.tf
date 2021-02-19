terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.42"
    }
  }
  # backend "azurerm" {}
}

provider "azurerm" {
  features {}
}

locals {
  // If an environment is set up (dev, test, prod...), it is used in the application name
  application_name_no_env   = var.application_name
  application_name_with_env = "${var.environment}-${var.environment}"
  application_name_final    = var.environment == "" ? var.application_name : application_name_with_env
  
  resource_group     = "rg-${locals.application_name_final}-001"
}

resource "azurerm_resource_group" "main" {
  name        = locals.resource_group
  location    = var.location
  tags = {
    "terraform"   = "true"
    "environment" = var.environment
  }
}

module "application" {
  source            = "./modules/app-service"
  resource_group    = locals.resource_group
  application_name  = locals.application_name_final
  environment       = var.environment
  location          = var.location

  azure_storage_account_name  = module.storage-blob.azurerm_storage_account_name
  azure_storage_account_key   = module.storage-blob.azurerm_storage_account_key
  azure_storage_blob_endpoint = module.storage-blob.azurerm_storage_blob_endpoint

  depends_on = [
    module.storage-blob,
    azurerm_resource_group.main
  ]
}

module "application-docker" {
  source            = "./modules/app-service-docker"
  resource_group    = locals.resource_group
  application_name  = locals.application_name_final
  environment       = var.environment
  location          = var.location

  azure_storage_account_name  = module.storage-blob.azurerm_storage_account_name
  azure_storage_account_key   = module.storage-blob.azurerm_storage_account_key
  azure_storage_blob_endpoint = module.storage-blob.azurerm_storage_blob_endpoint

  container_registry_name     = module.container-registry.container_registry_name
  container_registry_username = module.container-registry.container_registry_username
  container_registry_password = module.container-registry.container_registry_password

  depends_on = [
    module.storage-blob,
    module.container-registry,
    azurerm_resource_group.main
  ]
}

module "container-registry" {
  source           = "./modules/container-registry"
  resource_group   = locals.resource_group
  application_name = locals.application_name_final
  environment      = var.environment
  location         = var.location
  
  depends_on = [
    azurerm_resource_group.main
  ]
}

module "storage-blob" {
  source           = "./modules/storage-blob"
  resource_group   = locals.resource_group
  application_name = locals.application_name_final
  environment      = var.environment
  location         = var.location

  depends_on = [
    azurerm_resource_group.main
  ]
}
