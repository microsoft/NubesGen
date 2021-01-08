
resource "random_password" "password" {
  length           = 32
  special          = true
  override_special = "_%@"
}

resource "azurerm_postgresql_server" "database" {
  name                = "${var.application_name}-postgresql"
  resource_group_name = var.resource_group
  location            = var.location

  administrator_login          = var.administrator_login
  administrator_login_password = random_password.password.result

  sku_name                     = "B_Gen5_1"
  storage_mb                   = 5120
  backup_retention_days        = 7
  geo_redundant_backup_enabled = false
  auto_grow_enabled            = true
  version                      = "9.5"
  ssl_enforcement_enabled      = true
}

resource "azurerm_postgresql_database" "database" {
  name                = "${var.application_name}-postgresql-db"
  resource_group_name = var.resource_group
  server_name         = azurerm_postgresql_server.database.name
  charset             = "UTF8"
  collation           = "English_United States.1252"
}

# This rule is to enable the 'Allow access to Azure services' checkbox
resource "azurerm_postgresql_firewall_rule" "database" {
  name                = "${var.application_name}-postgresql-firewall"
  resource_group_name = var.resource_group
  server_name         = azurerm_postgresql_server.database.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}
