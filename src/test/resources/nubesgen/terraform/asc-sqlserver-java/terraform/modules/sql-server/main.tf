terraform {
  required_providers {
    azurecaf = {
      source = "aztfmod/azurecaf"
      version = "1.2.6"
    }
  }
}

resource "azurecaf_name" "mssql_server" {
  name          = var.application_name
  resource_type = "azurerm_mssql_server"
  suffixes      = [var.environment]
}

resource "random_password" "password" {
  length           = 32
  special          = true
  override_special = "_%@"
}

resource "azurerm_mssql_server" "database" {
  name                          = azurecaf_name.mssql_server.result
  resource_group_name           = var.resource_group
  location                      = var.location
  version                       = "12.0"

  administrator_login          = var.administrator_login
  administrator_login_password = random_password.password.result

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurecaf_name" "mssql_database" {
  name          = var.application_name
  resource_type = "azurerm_mssql_database"
  suffixes      = [var.environment]
}

resource "azurerm_mssql_database" "database" {
  name                = azurecaf_name.mssql_database.result
  server_id           = azurerm_mssql_server.database.id
  collation           = "SQL_Latin1_General_CP1_CI_AS"

  sku_name                    = "GP_S_Gen5_1"
  min_capacity                = 0.5
  auto_pause_delay_in_minutes = 60
}

resource "azurecaf_name" "sql_firewall_rule" {
  name          = var.application_name
  resource_type = "azurerm_sql_firewall_rule"
  suffixes      = [var.environment]
}

# This rule is to enable the 'Allow access to Azure services' checkbox
resource "azurerm_sql_firewall_rule" "database" {
  name                = azurecaf_name.sql_firewall_rule.result
  resource_group_name = var.resource_group
  server_name         = azurerm_mssql_server.database.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}
