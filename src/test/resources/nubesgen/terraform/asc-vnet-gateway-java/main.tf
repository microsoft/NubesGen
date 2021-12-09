terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.75"
    }
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.6"
    }
  }
}

provider "azurerm" {
  features {
    key_vault {
      purge_soft_delete_on_destroy = true
    }
  }
}

provider "azuread" {
}

locals {
  // If an environment is set up (dev, test, prod...), it is used in the application name
  environment           = var.environment == "" ? "dev" : var.environment
  gateway_identity_name = "${var.application_name}-gw-identity"
  dns_names        = ["*.${module.public_ip.fqdn}", module.public_ip.fqdn, local.private_app_fqdn]
  app_domain       = module.public_ip.fqdn
  cert_subject     = "${var.cert_subjects}, CN=${local.app_domain}"
  private_app_fqdn = trimprefix(module.application.application_hostname, "https://")
  # Azure Spring Cloud Resource Provider object id. It is constant and it is required to manage the VNET. 
  azure_spring_cloud_provisioner_object_id = "d2531223-68f9-459e-b225-5592f90d145e"
}

resource "azurecaf_name" "resource_group" {
  name          = var.application_name
  resource_type = "azurerm_resource_group"
  suffixes      = [local.environment]
}

resource "azurerm_resource_group" "main" {
  name     = azurecaf_name.resource_group.result
  location = var.location

  tags = {
    "terraform"        = "true"
    "environment"      = local.environment
    "application-name" = var.application_name
  }
}

resource "azurerm_user_assigned_identity" "gateway_identity" {
  name                = local.gateway_identity_name
  resource_group_name = azurerm_resource_group.main.name
  location            = azurerm_resource_group.main.location
}

module "application" {
  source           = "./modules/spring-cloud"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location

  azure_application_insights_connection_string = module.application-insights.azure_application_insights_connection_string

  vault_id  = module.key-vault.vault_id
  vault_uri = module.key-vault.vault_uri

  virtual_network_id = module.network.virtual_network_id
  app_subnet_id      = module.network.app_subnet_id
  service_subnet_id  = module.network.service_subnet_id
  cidr_ranges        = var.cidr_ranges

  key_vault_certificate_id = module.certificate.certificate_id
  app_domain               = local.app_domain
  depends_on = [
    module.key-vault
  ]
}

module "application-insights" {
  source           = "./modules/application-insights"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
}

module "key-vault" {
  source           = "./modules/key-vault"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location

  subnet_id = module.network.app_subnet_id
  allowed_identities = [{
    tenant_id    = azurerm_user_assigned_identity.gateway_identity.tenant_id
    principal_id = azurerm_user_assigned_identity.gateway_identity.principal_id
    }, {
    tenant_id    = azurerm_user_assigned_identity.gateway_identity.tenant_id
    principal_id = data.azuread_service_principal.azure_spring_cloud_domain_management.object_id
  }]
}

module "network" {
  source           = "./modules/virtual-network"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location

  service_endpoints = ["Microsoft.KeyVault"]

  address_space     = var.address_space
  app_subnet_prefix = var.app_subnet_prefix

  service_subnet_prefix = var.service_subnet_prefix
  gateway_subnet_prefix = var.gateway_subnet_prefix
}

module "public_ip" {
  source           = "./modules/public-ip"
  resource_group   = azurerm_resource_group.main.name
  application_name = var.application_name
  environment      = local.environment
  location         = var.location
}


module "certificate" {
  source           = "./modules/certificate"
  certificate_name = "${var.application_name}-cert"
  key_vault_id     = module.key-vault.vault_id
  dns_names        = local.dns_names
  subject          = local.cert_subject
}

module "application_gateway" {
  source                     = "./modules/application-gateway"
  resource_group             = azurerm_resource_group.main.name
  application_name           = var.application_name
  environment                = local.environment
  location                   = var.location
  network_name               = module.network.network_name
  gateway_frontend_subnet_id = module.network.gateway_subnet_id
  backend_address            = local.private_app_fqdn
  user_managed_identity_id   = azurerm_user_assigned_identity.gateway_identity.id
  key_vault_certificate_id   = module.certificate.certificate_object.secret_id
  host_name                  = local.app_domain
  root_certificate           = module.certificate.certificate_object.certificate_data_base64
  root_certificate_name      = module.certificate.certificate_object.name
  public_ip_id               = module.public_ip.public_ip_id
}
