terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.10"
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

resource "azurerm_postgresql_flexible_server" "database" {
  name                = azurecaf_name.postgresql_server.result
  resource_group_name = var.resource_group
  location            = var.location

  administrator_login    = var.administrator_login
  administrator_password = random_password.password.result

  sku_name                         = "GP_Standard_D2ds_v4"
  storage_mb                       = 32768
  backup_retention_days            = 7
  geo_redundant_backup_enabled     = false
  version                          = "13"

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

resource "azurerm_postgresql_flexible_server_database" "database" {
  name                = azurecaf_name.postgresql_database.result
  server_id           = azurerm_postgresql_flexible_server.database.id
  charset             = "utf8"
  collation           = "en_US.utf8"
}
