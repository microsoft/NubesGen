terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.26"
    }
  }
}

resource "azurecaf_name" "redis_cache" {
  name          = var.application_name
  resource_type = "azurerm_redis_cache"
  suffixes      = [var.environment]
}

resource "azurerm_redis_cache" "redis" {
  name                = azurecaf_name.redis_cache.result
  resource_group_name = var.resource_group
  location            = var.location
  capacity            = 0
  family              = "C"
  sku_name            = "Standard"
  enable_non_ssl_port = false
  minimum_tls_version = "1.2"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }

  redis_configuration {
  }
}
