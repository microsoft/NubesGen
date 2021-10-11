terraform {
  required_providers {
    azurecaf = {
      source = "aztfmod/azurecaf"
      version = "1.2.6"
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

resource "azurerm_mysql_server" "database" {
  name                = azurecaf_name.mysql_server.result
  resource_group_name = var.resource_group
  location            = var.location

  administrator_login          = var.administrator_login
  administrator_login_password = random_password.password.result

  sku_name                          = "B_Gen5_1"
  storage_mb                        = 5120
  version                           = "5.7"
  auto_grow_enabled                 = true
  backup_retention_days             = 7
  geo_redundant_backup_enabled      = false
  infrastructure_encryption_enabled = false
  public_network_access_enabled     = true
  ssl_enforcement_enabled           = true
  ssl_minimal_tls_version_enforced  = "TLS1_2"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurecaf_name" "mysql_database" {
  name          = var.application_name
  resource_type = "azurerm_mysql_database"
  suffixes      = [var.environment]
}

resource "azurerm_mysql_database" "database" {
  name                = azurecaf_name.mysql_database.result
  resource_group_name = var.resource_group
  server_name         = azurerm_mysql_server.database.name
  charset             = "utf8"
  collation           = "utf8_unicode_ci"
}

resource "azurecaf_name" "mysql_firewall_rule" {
  name          = var.application_name
  resource_type = "azurerm_mysql_firewall_rule"
  suffixes      = [var.environment]
}

# This rule is to enable the 'Allow access to Azure services' checkbox
resource "azurerm_mysql_firewall_rule" "database" {
  name                = azurecaf_name.mysql_firewall_rule.result
  resource_group_name = var.resource_group
  server_name         = azurerm_mysql_server.database.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}
