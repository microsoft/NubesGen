terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.16"
    }
  }
}

resource "azurecaf_name" "cosmosdb_account" {
  name          = var.application_name
  resource_type = "azurerm_cosmosdb_account"
  suffixes      = [var.environment]
}

resource "azurerm_cosmosdb_account" "cosmosdb" {
  name                = azurecaf_name.cosmosdb_account.result
  resource_group_name = var.resource_group
  location            = var.location
  offer_type          = "Standard"
  kind                = "MongoDB"
  enable_free_tier    = true

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }

  consistency_policy {
    consistency_level = "Session"
  }

  geo_location {
    failover_priority = 0
    location          = var.location
  }

  is_virtual_network_filter_enabled = true

  virtual_network_rule {
    id = var.subnet_id
  }
}

# azurerm_cosmosdb_mongo_database isn't implemented yet in azurecaf_name
resource "azurerm_cosmosdb_mongo_database" "cosmosdb" {
  name                = "cosmos-${var.application_name}-001"
  resource_group_name = azurerm_cosmosdb_account.cosmosdb.resource_group_name
  account_name        = azurerm_cosmosdb_account.cosmosdb.name
  throughput          = var.throughput
}
