output "database_url" {
  value       = "${azurerm_mssql_server.database.name}.database.windows.net:1433;database=${azurerm_mssql_database.database.name};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
  description = "The Azure SQL server URL."
}

output "database_username" {
  value       = "${var.administrator_login}@${azurerm_mssql_server.database.name}"
  description = "The Azure SQL server user name."
}

output "database_password" {
  value       = random_password.password.result
  sensitive   = true
  description = "The Azure SQL server password."
}
