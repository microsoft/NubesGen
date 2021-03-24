terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.52"
    }
  }
  # backend "azurerm" {}
}

provider "azurerm" {
  features {}
}

locals {
  // If an environment is set up (dev, test, prod...), it is used in the application name
  environment      = var.environment == "" ? "dev" : var.environment
  application_name = var.environment == "" ? var.application_name : "${var.application_name}-${local.environment}"
  resource_group   = "rg-${local.application_name}-001"
}

resource "azurerm_resource_group" "main" {
  name        = local.resource_group
  location    = var.location
  tags = {
    "terraform"   = "true"
    "environment" = local.environment
  }
}

module "application" {
  source            = "./modules/app-service"
  resource_group    = local.resource_group
  application_name  = local.application_name
  environment       = local.environment
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
  resource_group    = local.resource_group
  application_name  = local.application_name
  environment       = local.environment
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
  resource_group   = local.resource_group
  application_name = local.application_name
  environment      = local.environment
  location         = var.location

  depends_on = [
    azurerm_resource_group.main
  ]
}

module "storage-blob" {
  source           = "./modules/storage-blob"
  resource_group   = local.resource_group
  application_name = local.application_name
  environment      = local.environment
  location         = var.location

  depends_on = [
    azurerm_resource_group.main
  ]
}
