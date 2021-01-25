output "database_url" {
  value = "${azurerm_mysql_server.database.fqdn}:3306/${azurerm_mysql_database.database.name}"
}

output "database_username" {
  value = "${var.administrator_login}@${azurerm_mysql_server.database.name}"
}

output "database_password" {
  value     = random_password.password.result
  sensitive = true
}
