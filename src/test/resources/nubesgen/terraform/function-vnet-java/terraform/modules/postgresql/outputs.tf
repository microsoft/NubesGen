output "database_url" {
  value       = "${azurerm_postgresql_server.database.fqdn}:5432/${azurerm_postgresql_database.database.name}"
  description = "The PostgreSQL server URL."
}

output "database_username" {
  value       = "${var.administrator_login}@${azurerm_postgresql_server.database.name}"
  description = "The PostgreSQL server user name."
}

output "database_password" {
  value       = random_password.password.result
  sensitive   = true
  description = "The PostgreSQL server password."
}
