terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.26"
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

  sku_name                     = "B_Standard_B1ms"
  version                      = "8.0.21"
  backup_retention_days        = 7
  geo_redundant_backup_enabled = false

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

resource "azurecaf_name" "mysql_firewall_rule" {
  name          = var.application_name
  resource_type = "azurerm_mysql_firewall_rule"
  suffixes      = [var.environment]
}

# This rule is to enable the 'Allow access to Azure services' checkbox
resource "azurerm_mysql_flexible_server_firewall_rule" "database" {
  name                = azurecaf_name.mysql_firewall_rule.result
  resource_group_name = var.resource_group
  server_name         = azurerm_mysql_flexible_server.database.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}
