terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.9"
    }
  }
}

resource "azurecaf_name" "postgresql_server" {
  name          = var.application_name
  resource_type = "azurerm_postgresql_server"
  suffixes      = [var.environment]
}

resource "random_password" "password" {
  length           = 32
  special          = true
  override_special = "_%@"
}

resource "azurerm_postgresql_server" "database" {
  name                = azurecaf_name.postgresql_server.result
  resource_group_name = var.resource_group
  location            = var.location

  administrator_login          = var.administrator_login
  administrator_login_password = random_password.password.result

  sku_name                     = "B_Gen5_1"
  storage_mb                   = 5120
  backup_retention_days        = 7
  geo_redundant_backup_enabled = false
  auto_grow_enabled            = true
  version                      = "11"
  ssl_enforcement_enabled      = true

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurecaf_name" "postgresql_database" {
  name          = var.application_name
  resource_type = "azurerm_postgresql_database"
  suffixes      = [var.environment]
}

resource "azurerm_postgresql_database" "database" {
  name                = azurecaf_name.postgresql_database.result
  resource_group_name = var.resource_group
  server_name         = azurerm_postgresql_server.database.name
  charset             = "UTF8"
  collation           = "English_United States.1252"
}

resource "azurecaf_name" "postgresql_network_rule" {
  name          = var.application_name
  resource_type = "azurerm_postgresql_virtual_network_rule"
  suffixes      = [var.environment]
}

# This rule only allows traffic from the apps VNet
resource "azurerm_postgresql_virtual_network_rule" "network_rule" {
  name                = azurecaf_name.postgresql_network_rule.result
  resource_group_name = var.resource_group
  server_name         = azurerm_postgresql_server.database.name
  subnet_id           = var.subnet_id
}
