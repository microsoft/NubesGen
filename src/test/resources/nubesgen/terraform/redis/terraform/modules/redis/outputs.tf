output "azure_redis_host" {
  value       = azurerm_redis_cache.redis.hostname
  description = "The Redis server URL."
}

output "azure_redis_password" {
  value       = azurerm_redis_cache.redis.primary_access_key
  sensitive   = true
  description = "The Redis server password."
}
