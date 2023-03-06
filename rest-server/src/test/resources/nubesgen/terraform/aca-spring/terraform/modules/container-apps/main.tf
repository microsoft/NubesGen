terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.23"
    }
  }
}

resource "azurecaf_name" "container_registry" {
  name          = var.application_name
  resource_type = "azurerm_container_registry"
  suffixes      = [var.environment]
}

resource "azurerm_container_registry" "container-registry" {
  name                = azurecaf_name.container_registry.result
  resource_group_name = var.resource_group
  location            = var.location
  sku                 = "Basic"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurecaf_name" "log_analytics_workspace" {
  name          = var.application_name
  resource_type = "azurerm_log_analytics_workspace"
  suffixes      = [var.environment]
}

resource "azurerm_log_analytics_workspace" "application" {
  name                = azurecaf_name.log_analytics_workspace.result
  resource_group_name = var.resource_group
  location            = var.location
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

# As Azure Container Apps is not supported yet, we're using the naming from Azure App Service
resource "azurecaf_name" "app_service" {
  name          = var.application_name
  resource_type = "azurerm_app_service"
  suffixes      = [var.environment]
}

resource "azurerm_container_app_environment" "application" {
  name                       = "${azurecaf_name.app_service.result}-environment"
  resource_group_name        = var.resource_group
  location                   = var.location
  log_analytics_workspace_id = azurerm_log_analytics_workspace.application.id
}

resource "azurecaf_name" "user_assigned_identity" {
  name          = var.application_name
  resource_type = "azurerm_user_assigned_identity"
  suffixes      = [var.environment]
}

resource "azurerm_user_assigned_identity" "application" {
  name                = azurecaf_name.user_assigned_identity.result
  resource_group_name = var.resource_group
  location            = var.location
}

resource "azurerm_role_assignment" "application" {
  scope                = azurerm_container_registry.container-registry.id
  role_definition_name = "acrpull"
  principal_id         = azurerm_user_assigned_identity.application.principal_id
  depends_on = [
    azurerm_user_assigned_identity.application
  ]
}

resource "azurerm_container_app" "application" {
  name                         = azurecaf_name.app_service.result
  container_app_environment_id = azurerm_container_app_environment.application.id
  resource_group_name          = var.resource_group
  revision_mode                = "Single"

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.application.id]
  }

  ingress {
    external_enabled = true
    target_port      = 8080
    traffic_weight {
      percentage = 100
    }
  }

  registry {
    server   = azurerm_container_registry.container-registry.login_server
    identity = azurerm_user_assigned_identity.application.id
  }

  template {
    container {
      name   = azurecaf_name.app_service.result
      image  = "${azurerm_container_registry.container-registry.name}.azurecr.io/${var.application_name}/${var.application_name}"
      cpu    = 0.25
      memory = "0.5Gi"
      /*
      liveness_probe {
        path      = "/actuator/health/liveness"
        port      = 8080
        transport = "HTTPS"
      }
      readiness_probe {
        path      = "/actuator/health/readiness"
        port      = 8080
        transport = "HTTPS"
      }
      */
    }
    min_replicas = 1
  }
}
