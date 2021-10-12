terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.75"
    }
    azurecaf = {
      source = "aztfmod/azurecaf"
      version = "1.2.6"
    }
  }
}

provider "azurerm" {
  features {}
}

locals {
  // If an environment is set up (dev, test, prod...), it is used in the application name
  environment      = var.environment == "" ? "dev" : var.environment
}

resource "azurecaf_name" "resource_group" {
  name            = var.application_name
  resource_type   = "azurerm_resource_group"
  suffixes        = [local.environment]
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

  azure_application_insights_connection_string = module.application-insights.azure_application_insights_connection_string

  vault_id = module.key-vault.vault_id

  azure_redis_id     = module.redis.azure_redis_id
  azure_redis_password = module.redis.azure_redis_password

  azure_storage_account_name  = module.storage-blob.azurerm_storage_account_name
  azure_storage_blob_endpoint = module.storage-blob.azurerm_storage_blob_endpoint
  azure_storage_account_key   = "@Microsoft.KeyVault(SecretUri=${module.key-vault.vault_uri}secrets/storage-account-key)"

  azure_cosmosdb_mongodb_database = module.cosmosdb-mongodb.azure_cosmosdb_mongodb_database
  azure_cosmosdb_account_id       = module.cosmosdb-mongodb.azure_cosmosdb_account_id
  azure_cosmosdb_mongodb_key      = module.cosmosdb-mongodb.azure_cosmosdb_account_key
}

module "application-insights" {
  source            = "./modules/application-insights"
  resource_group    = azurerm_resource_group.main.name
  application_name  = var.application_name
  environment       = local.environment
  location          = var.location
}

module "key-vault" {
  source           = "./modules/key-vault"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location

  redis_password = module.redis.azure_redis_password

  storage_account_key=module.storage-blob.azurerm_storage_account_key

  cosmosdb_account_id=module.cosmosdb-mongodb.azure_cosmosdb_account_id
  cosmosdb_mongodb_database=module.cosmosdb-mongodb.azure_cosmosdb_mongodb_database
  cosmosdb_mongodb_key=module.cosmosdb-mongodb.azure_cosmosdb_account_key
}

module "redis" {
  source            = "./modules/redis"
  resource_group    = azurerm_resource_group.main.name
  application_name  = var.application_name
  environment       = local.environment
  location          = var.location
}

module "storage-blob" {
  source           = "./modules/storage-blob"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
}

module "cosmosdb-mongodb" {
  source           = "./modules/cosmosdb-mongodb"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
}
