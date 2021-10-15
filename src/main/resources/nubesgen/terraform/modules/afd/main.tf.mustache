terraform {
  required_providers {
    azurecaf = {
      source = "aztfmod/azurecaf"
      version = "1.2.6"
    }
  }
}

resource "azurecaf_name" "afd" {
  name            = var.application_name
  resource_type   = "azurerm_frontdoor"
  suffixes        = [local.environment]  
}

resource "azurerm_frontdoor" "afd" {
  name                                         = azurecaf_name.afd.result
  resource_group_name                          = var.resource_group
  enforce_backend_pools_certificate_name_check = false

  routing_rule {
    name               = "routeToApp"
    accepted_protocols = ["Http", "Https"]
    patterns_to_match  = ["/*"]
    frontend_endpoints = ["appFrontEnd"]
    forwarding_configuration {
      forwarding_protocol = "MatchRequest"
      backend_pool_name   = "appBackend"
    }
  }

  backend_pool_load_balancing {
    name = "appLoadBalancingSettings"
  }

  backend_pool_health_probe {
    name = "appHealthProbeSettings"
  }

  backend_pool {
    name = "appBackend"
    backend {
      host_header = var.app_address
      address     = var.app_address
      http_port   = 80
      https_port  = 443
    }

    load_balancing_name = "appLoadBalancingSettings"
    health_probe_name   = "appHealthProbeSettings"
  }

  frontend_endpoint {
    name      = "appFrontendEndpoint"
    host_name = "${var.application_name}.azurefd.net"
  }
}