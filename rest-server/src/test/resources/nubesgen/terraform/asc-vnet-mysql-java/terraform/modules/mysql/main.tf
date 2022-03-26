terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.16"
    }
  }
}

resource "azurecaf_name" "mysql_server" {
  name          = var.application_name
  resource_type = "azurerm_mysql_server"
  suffixes      = [var.environment]
}

resource "random_password" "password" {
  length           = 32
  special          = true
  override_special = "_%@"
}

resource "azurerm_mysql_flexible_server" "database" {
  name                = azurecaf_name.mysql_server.result
  resource_group_name = var.resource_group
  location            = var.location

  administrator_login    = var.administrator_login
  administrator_password = random_password.password.result

  sku_name                     = "GP_Standard_D2ds_v4"
  version                      = "8.0.21"
  backup_retention_days        = 7
  geo_redundant_backup_enabled = true
  high_availability {
    mode = "ZoneRedundant"
  }
  delegated_subnet_id          = var.subnet_id
  private_dns_zone_id          = azurerm_private_dns_zone.database.id
  depends_on                   = [azurerm_private_dns_zone_virtual_network_link.database]

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurerm_mysql_flexible_database" "database" {
  name                = var.database_name
  resource_group_name = var.resource_group
  server_name         = azurerm_mysql_flexible_server.database.name
  charset             = "utf8"
  collation           = "utf8_unicode_ci"
}

resource "azurerm_private_dns_zone" "database" {
  name                = "db1.private.mysql.database.azure.com"
  resource_group_name = var.resource_group
}

resource "azurecaf_name" "private_dns_zone_virtual_network_link" {
  name          = var.application_name
  resource_type = "azurerm_private_dns_zone_virtual_network_link"
  suffixes      = [var.environment]
}

resource "azurerm_private_dns_zone_virtual_network_link" "database" {
  name                  = azurecaf_name.private_dns_zone_virtual_network_link.result
  resource_group_name   = var.resource_group
  private_dns_zone_name = azurerm_private_dns_zone.database.name
  virtual_network_id    = var.virtual_network_id
}
