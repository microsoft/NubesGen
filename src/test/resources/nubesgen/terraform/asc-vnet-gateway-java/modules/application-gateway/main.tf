terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.6"
    }
  }
}

locals {
  certificate_name               = "${var.application_name}-cert"
  backend_address_pool_name      = "${var.network_name}-beap"
  frontend_port_name             = "${var.network_name}-feport"
  frontend_ip_configuration_name = "${var.network_name}-feip"
  http_setting_name              = "${var.network_name}-be-htst"
  listener_name                  = "${var.network_name}-httplstn"
  request_routing_rule_name      = "${var.network_name}-rqrt"
  redirect_configuration_name    = "${var.network_name}-rdrcfg"
  app_probe                      = "${var.network_name}-probe"
}



resource "azurecaf_name" "app_gateway" {
  name          = var.application_name
  resource_type = "azurerm_application_gateway"
  suffixes      = [var.environment]
}

resource "azurerm_application_gateway" "app_gateway" {
  name                = azurecaf_name.app_gateway.result
  resource_group_name = var.resource_group
  location            = var.location

  identity {
    identity_ids = [var.user_managed_identity_id]
  }

  ssl_certificate {
    name                = local.certificate_name
    key_vault_secret_id = var.key_vault_certificate_id
  }

  autoscale_configuration {
    min_capacity = 0
    max_capacity = 2
  }

  sku {
    name = "Standard_v2"
    tier = "Standard_v2"
  }

  gateway_ip_configuration {
    name      = "my-gateway-ip-configuration"
    subnet_id = var.gateway_frontend_subnet_id # azurerm_subnet.frontend.id
  }

  frontend_port {
    name = local.frontend_port_name
    port = 443
  }

  frontend_ip_configuration {
    name                 = local.frontend_ip_configuration_name
    public_ip_address_id = var.public_ip_id
  }

  backend_address_pool {
    name  = local.backend_address_pool_name
    fqdns = [var.backend_address]
  }

  trusted_root_certificate {
    data = var.root_certificate
    name = var.root_certificate_name
  }

  backend_http_settings {
    name                           = local.http_setting_name
    cookie_based_affinity          = "Disabled"
    path                           = "/"
    port                           = 443
    protocol                       = "Https"
    request_timeout                = 60
    # trusted_root_certificate_names = [var.root_certificate_name]
    host_name                      = var.backend_address
    probe_name                     = local.app_probe
  }

  probe {
    host                = var.backend_address
    port                = 443
    protocol            = "Https"
    name                = local.app_probe
    interval            = 30
    timeout             = 30
    unhealthy_threshold = 3
    path                = "/"
  }

  http_listener {
    name                           = local.listener_name
    frontend_ip_configuration_name = local.frontend_ip_configuration_name
    frontend_port_name             = local.frontend_port_name
    protocol                       = "Https"
    ssl_certificate_name           = local.certificate_name
  }

  request_routing_rule {
    name                       = local.request_routing_rule_name
    rule_type                  = "Basic"
    http_listener_name         = local.listener_name
    backend_address_pool_name  = local.backend_address_pool_name
    backend_http_settings_name = local.http_setting_name
  }
}
