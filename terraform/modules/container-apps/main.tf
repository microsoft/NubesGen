terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.26"
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

resource "azurerm_container_app_environment_certificate" "application" {
  name                         = "nubesgen-dev-container-certificate"
  container_app_environment_id = azurerm_container_app_environment.application.id
  certificate_blob_base64      = var.container_certificate
  certificate_password         = var.container_certificate_password
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
    custom_domain {
      name           = var.custom_domain_name
      certificate_id = azurerm_container_app_environment_certificate.application.id
    }
  }

  secret {
    name  = "azure-storage-account-key"
    value = var.azure_storage_account_key
  }

  template {
    container {
      name   = azurecaf_name.application.result
      image  = "ghcr.io/microsoft/nubesgen/nubesgen-native:main"
      cpu    = 0.25
      memory = "0.5Gi"
      env {
        name  = "AZURE_STORAGE_ACCOUNT_NAME"
        value = var.azure_storage_account_name
      }
      env {
        name  = "AZURE_STORAGE_BLOB_ENDPOINT"
        value = var.azure_storage_blob_endpoint
      }
      env {
        name        = "AZURE_STORAGE_ACCOUNT_KEY"
        secret_name = "azure-storage-account-key"
      }
    }
    min_replicas = 1
  }
}
