output "database_url" {
  value = "jdbc:sqlserver://${azurerm_mssql_server.database.name}.database.windows.net:1433;database=${azurerm_mssql_database.database.name};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
}

output "database_username" {
  value = "${var.administrator_login}@${azurerm_mssql_server.database.name}"
}

output "database_password" {
  value     = random_password.password.result
  sensitive = true
}
