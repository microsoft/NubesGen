terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.24"
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

  sku_name                     = "B_Standard_B1ms"
  storage_mb                   = 32768
  backup_retention_days        = 7
  version                      = "13"
  geo_redundant_backup_enabled = false
  dynamic "high_availability" {
    for_each = local.feature_flags.high_available_type
    content {
      mode = high_availability.value
    }
  }

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }

  lifecycle {
    ignore_changes = [ zone, high_availability.0.standby_availability_zone ]
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

resource "azurecaf_name" "postgresql_firewall_rule" {
  name          = var.application_name
  resource_type = "azurerm_postgresql_flexible_server_firewall_rule"
  suffixes      = [var.environment]
}

# This rule is to enable the 'Allow access to Azure services' checkbox
resource "azurerm_postgresql_flexible_server_firewall_rule" "database" {
  name                = azurecaf_name.postgresql_firewall_rule.result
  server_id           = azurerm_postgresql_flexible_server.database.id
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}
