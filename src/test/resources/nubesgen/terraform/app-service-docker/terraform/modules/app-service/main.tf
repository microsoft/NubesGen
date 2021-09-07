terraform {
  required_providers {
    azurecaf = {
      source = "aztfmod/azurecaf"
      version = "1.2.6"
    }
  }
}

locals {
  // An Azure Container Registry name cannot contain hyphens, and is limited to 50 characters long
  azure-container-registry-name = substr(replace(var.application_name, "-", ""), 0, 46)
}

resource "azurerm_container_registry" "container-registry" {
  name                     = "acr${local.azure-container-registry-name}001"
  resource_group_name      = var.resource_group
  location                 = var.location
  admin_enabled            = true
  sku                      = "Basic"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurecaf_name" "app_service_plan" {
  name            = var.application_name
  resource_type   = "azurerm_app_service_plan"
  suffixes        = [var.environment, "001"]
  random_length   = 5
}

resource "azurecaf_name" "app_service" {
  name            = var.application_name
  resource_type   = "azurerm_app_service"
  suffixes        = [var.environment, "001"]
  random_length   = 5
}

# This creates the plan that the service use
resource "azurerm_app_service_plan" "application" {
  name                = azurecaf_name.app_service_plan.result
  resource_group_name = var.resource_group
  location            = var.location

  kind     = "Linux"
  reserved = true

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }

  sku {
    tier = "Standard"
    size = "S1"
  }
}

# This creates the service definition
resource "azurerm_app_service" "application" {
  name                = azurecaf_name.app_service.result
  resource_group_name = var.resource_group
  location            = var.location
  app_service_plan_id = azurerm_app_service_plan.application.id
  https_only          = true

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }

  site_config {
    linux_fx_version = "DOCKER|${azurerm_container_registry.container-registry.name}.azurecr.io/${var.application_name}/${var.application_name}:latest"
    always_on        = true
    ftps_state       = "FtpsOnly"
  }

  app_settings = {
    "WEBSITES_ENABLE_APP_SERVICE_STORAGE" = "false"
    "DOCKER_REGISTRY_SERVER_URL"          = "https://${azurerm_container_registry.container-registry.name}.azurecr.io"
    "DOCKER_REGISTRY_SERVER_USERNAME"     = azurerm_container_registry.container-registry.admin_username
    "DOCKER_REGISTRY_SERVER_PASSWORD"     = azurerm_container_registry.container-registry.admin_password
    "WEBSITES_PORT"                       = "8080"

    # These are app specific environment variables
    "SPRING_PROFILES_ACTIVE"     = "prod,azure"
  }
}
