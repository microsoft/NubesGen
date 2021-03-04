
resource "azurerm_redis_cache" "redis" {
  name                = var.application_name
  resource_group_name = var.resource_group
  location            = var.location
  capacity            = 0
  family              = "C"
  sku_name            = "Standard"
  enable_non_ssl_port = false
  minimum_tls_version = "1.2"

  tags = {
    "environment" = var.environment
  }

  redis_configuration {
  }
}

