locals {
  // A CosmosDB account is limited to 50 characters long
  cosmos-account-name = substr(var.application_name, 0, 35)
}

resource "azurerm_cosmosdb_account" "cosmosdb" {
  name                = "cosmosacct-${local.cosmos-account-name}-001"
  resource_group_name = var.resource_group
  location            = var.location
  offer_type          = "Standard"
  kind                = "MongoDB"
  enable_free_tier    = true

  tags = {
    "environment" = var.environment
  }

  consistency_policy {
    consistency_level = "Session"
  }

  geo_location {
    failover_priority = 0
    location          = var.location
  }
}

resource "azurerm_cosmosdb_mongo_database" "cosmosdb" {
  name                = "cosmos-${var.application_name}-001"
  resource_group_name = azurerm_cosmosdb_account.cosmosdb.resource_group_name
  account_name        = azurerm_cosmosdb_account.cosmosdb.name
  throughput          = var.throughput
}
