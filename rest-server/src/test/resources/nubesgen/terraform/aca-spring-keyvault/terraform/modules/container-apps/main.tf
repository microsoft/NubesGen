terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.26"
    }
    azuread = {
      source  = "hashicorp/azuread"
      version = "2.41.0"
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
  admin_enabled       = true
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

  identity {
    type = "SystemAssigned"
  }

  secret {
    name  = "registry-credentials"
    value = azurerm_container_registry.container-registry.admin_password
  }

  registry {
    server               = azurerm_container_registry.container-registry.login_server
    username             = azurerm_container_registry.container-registry.admin_username
    password_secret_name = "registry-credentials"
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
      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "prod,azure"
      }
      env {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${var.database_url}"
      }
      env {
        name  = "AZURE_KEYVAULT_ENABLED"
        value = "true"
      }
      env {
        name  = "AZURE_KEYVAULT_URI"
        value = var.vault_uri
      }
    }
    min_replicas = 1
  }
}

data "azuread_service_principal" "application" {
  display_name = azurerm_container_app.application.name
  depends_on = [
    azurerm_container_app.application
  ]
}

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault_access_policy" "application" {
  key_vault_id = var.vault_id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = data.azuread_service_principal.application.id

  secret_permissions = [
    "Get",
    "List"
  ]
}
