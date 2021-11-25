terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.9"
    }
  }
}

resource "azurecaf_name" "frontdoor" {
  name          = var.application_name
  resource_type = "azurerm_frontdoor"
  suffixes      = [var.environment]
}

resource "azurerm_frontdoor" "frontdoor" {
  name                                         = azurecaf_name.frontdoor.result
  resource_group_name                          = var.resource_group
  enforce_backend_pools_certificate_name_check = false

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }

  routing_rule {
    name               = "routeToApplication"
    accepted_protocols = ["Https"]
    patterns_to_match  = ["/*"]
    frontend_endpoints = ["application-frontend"]
    forwarding_configuration {
      forwarding_protocol = "MatchRequest"
      backend_pool_name   = "application-backend"
    }
  }

  backend_pool_load_balancing {
    name = "applicationLoadBalancingSettings"
  }

  backend_pool_health_probe {
    name = "applicationHealthProbeSettings"
  }

  backend_pool {
    name = "application-backend"
    backend {
      host_header = var.app_address
      address     = var.app_address
      http_port   = 80
      https_port  = 443
    }

    load_balancing_name = "applicationLoadBalancingSettings"
    health_probe_name   = "applicationHealthProbeSettings"
  }

  frontend_endpoint {
    name      = "application-frontend"
    host_name = "${azurecaf_name.frontdoor.result}.azurefd.net"
  }
}
