
resource "random_password" "password" {
  length           = 32
  special          = true
  override_special = "_%@"
}

resource "azurerm_mssql_server" "database" {
  name                          = "sql-${var.application_name}-001"
  resource_group_name           = var.resource_group
  location                      = var.location
  version                       = "12.0"

  administrator_login          = var.administrator_login
  administrator_login_password = random_password.password.result

  tags = {
    "environment" = var.environment
  }
}

resource "azurerm_mssql_database" "database" {
  name                = "sqldb-${var.application_name}-001"
  server_id           = azurerm_mssql_server.database.id
  collation           = "SQL_Latin1_General_CP1_CI_AS"

  sku_name                    = "GP_S_Gen5_1"
  min_capacity                = 0.5
  auto_pause_delay_in_minutes = 60
}

# This rule is to enable the 'Allow access to Azure services' checkbox
resource "azurerm_sql_firewall_rule" "database" {
  name                = "sqlfw-${var.application_name}-001"
  resource_group_name = var.resource_group
  server_name         = azurerm_mssql_server.database.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}
