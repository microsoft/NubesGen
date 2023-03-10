terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.24"
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

resource "azurecaf_name" "application-environment" {
  name          = var.application_name
  resource_type = "azurerm_container_app_environment"
  suffixes      = [var.environment]
}

resource "azurerm_container_app_environment" "application" {
  name                       = azurecaf_name.application-environment.result
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

resource "azurecaf_name" "application" {
  name          = var.application_name
  resource_type = "azurerm_container_app"
  suffixes      = [var.environment]
}

resource "azurerm_container_app" "application" {
  name                         = azurecaf_name.application.result
  container_app_environment_id = azurerm_container_app_environment.application.id
  resource_group_name          = var.resource_group
  revision_mode                = "Single"

  lifecycle {
    ignore_changes = [
      template.0.container["image"]
    ]
  }

  ingress {
    external_enabled = true
    target_port      = 8080
    traffic_weight {
      percentage = 100
      latest_revision = true
    }
  }

  template {
    container {
      name   = azurecaf_name.application.result
      image  = "ghcr.io/microsoft/nubesgen/nubesgen-native:main"
      cpu    = 0.25
      memory = "0.5Gi"
    }
    min_replicas = 1
  }
}
