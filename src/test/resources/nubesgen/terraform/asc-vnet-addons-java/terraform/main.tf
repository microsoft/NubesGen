terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.75"
    }
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.6"
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

  database_url = module.database.database_url

  azure_application_insights_connection_string = module.application-insights.azure_application_insights_connection_string

  vault_id  = module.key-vault.vault_id
  vault_uri = module.key-vault.vault_uri

  azure_redis_host = module.redis.azure_redis_host

  azure_storage_account_name  = module.storage-blob.azurerm_storage_account_name
  azure_storage_blob_endpoint = module.storage-blob.azurerm_storage_blob_endpoint

  azure_cosmosdb_mongodb_database = module.cosmosdb-mongodb.azure_cosmosdb_mongodb_database

  app_subnet_id     = module.network.app_subnet_id
  service_subnet_id = module.network.service_subnet_id
  cidr_ranges       = var.cidr_ranges
}

module "database" {
  source           = "./modules/postgresql"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
  subnet_id        = module.network.app_subnet_id
}

module "application-insights" {
  source           = "./modules/application-insights"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
}

module "key-vault" {
  source           = "./modules/key-vault"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location

  database_username = module.database.database_username
  database_password = module.database.database_password

  redis_password = module.redis.azure_redis_password

  storage_account_key = module.storage-blob.azurerm_storage_account_key

  cosmosdb_mongodb_uri = module.cosmosdb-mongodb.azure_cosmosdb_mongodb_uri

  subnet_id = module.network.app_subnet_id
}

module "redis" {
  source           = "./modules/redis"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
  subnet_id        = module.network.redis_subnet_id
}

module "storage-blob" {
  source           = "./modules/storage-blob"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
  subnet_id        = module.network.app_subnet_id
}

module "cosmosdb-mongodb" {
  source           = "./modules/cosmosdb-mongodb"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
  subnet_id        = module.network.app_subnet_id
}

module "network" {
  source                = "./modules/virtual-network"
  resource_group        = azurerm_resource_group.main.name
  application_name      = var.application_name
  environment           = local.environment
  location              = var.location
  service_endpoints     = ["Microsoft.Sql", "Microsoft.AzureCosmosDB", "Microsoft.KeyVault", "Microsoft.Storage"]
  address_space         = var.address_space
  app_subnet_prefix     = var.app_subnet_prefix
  service_subnet_prefix = var.service_subnet_prefix
  redis_subnet_prefix   = var.redis_subnet_prefix
}

module "frontdoor" {
  source           = "./modules/frontdoor"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
  app_address      = module.application.spring_cloud_service_app_fqdn
}
