terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.26"
    }
  }
}

locals {
  feature_flags = {
    high_available_type = var.high_availability ? ["ZoneRedundant"] : []
  }
}

resource "azurecaf_name" "postgresql_server" {
  name          = var.application_name
  resource_type = "azurerm_postgresql_flexible_server"
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

  sku_name                     = "GP_Standard_D2ds_v4"
  storage_mb                   = 32768
  backup_retention_days        = 7
  version                      = "13"
  geo_redundant_backup_enabled = true
  dynamic "high_availability" {
    for_each = local.feature_flags.high_available_type
    content {
      mode = high_availability.value
    }
  }
  delegated_subnet_id          = var.subnet_id
  private_dns_zone_id          = azurerm_private_dns_zone.database.id
  depends_on                   = [azurerm_private_dns_zone_virtual_network_link.database]

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurecaf_name" "postgresql_database" {
  name          = var.application_name
  resource_type = "azurerm_postgresql_flexible_server_database"
  suffixes      = [var.environment]
}

resource "azurerm_postgresql_flexible_server_database" "database" {
  name                = azurecaf_name.postgresql_database.result
  server_id           = azurerm_postgresql_flexible_server.database.id
  charset             = "utf8"
  collation           = "en_US.utf8"
}

resource "azurerm_private_dns_zone" "database" {
  name                = "db1.private.postgres.database.azure.com"
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
