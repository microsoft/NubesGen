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
}

provider "azurerm" {
  features {}
}

locals {
  // If an environment is set up (dev, test, prod...), it is used in the application name
  environment = var.environment == "" ? "dev" : var.environment
}

data "http" "myip" {
  url = "http://whatismyip.akamai.com"
}

locals {
  myip = chomp(data.http.myip.body)
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
    "nubesgen-version" = "test"
  }
}

module "application" {
  source           = "./modules/app-service"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location

  database_url      = module.database.database_url
  database_username = "@Microsoft.KeyVault(SecretUri=${module.key-vault.vault_uri}secrets/database-username)"
  database_password = "@Microsoft.KeyVault(SecretUri=${module.key-vault.vault_uri}secrets/database-password)"

  azure_application_insights_instrumentation_key = module.application-insights.azure_application_insights_instrumentation_key

  vault_id = module.key-vault.vault_id

  azure_redis_host     = module.redis.azure_redis_host
  azure_redis_password = "@Microsoft.KeyVault(SecretUri=${module.key-vault.vault_uri}secrets/redis-password)"

  azure_storage_account_name  = module.storage-blob.azurerm_storage_account_name
  azure_storage_blob_endpoint = module.storage-blob.azurerm_storage_blob_endpoint
  azure_storage_account_key   = "@Microsoft.KeyVault(SecretUri=${module.key-vault.vault_uri}secrets/storage-account-key)"

  azure_cosmosdb_mongodb_database = module.cosmosdb-mongodb.azure_cosmosdb_mongodb_database
  azure_cosmosdb_mongodb_uri      = "@Microsoft.KeyVault(SecretUri=${module.key-vault.vault_uri}secrets/cosmosdb-mongodb-uri)"

  subnet_id = module.network.app_subnet_id
}

module "database" {
  source           = "./modules/postgresql"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
  high_availability= false

  subnet_id          = module.network.database_subnet_id
  virtual_network_id = module.network.virtual_network_id
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
  myip      = local.myip
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
  myip             = local.myip
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
  source           = "./modules/virtual-network"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location

  service_endpoints = ["Microsoft.Sql", "Microsoft.AzureCosmosDB", "Microsoft.KeyVault", "Microsoft.Storage", "Microsoft.ContainerRegistry"]

  address_space     = var.address_space
  app_subnet_prefix = var.app_subnet_prefix

  database_subnet_prefix = var.database_subnet_prefix

  redis_subnet_prefix = var.redis_subnet_prefix
}
