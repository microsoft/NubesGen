# Azure Spring Apps is not yet supported in azurecaf_name
locals {
  spring_apps_service_name = "asa-${var.application_name}-${var.environment}"
  spring_apps_app_name     = "app-${var.application_name}"
}

# This creates the Azure Spring Apps that the service use
resource "azurerm_spring_cloud_service" "application" {
  name                = local.spring_apps_service_name
  resource_group_name = var.resource_group
  location            = var.location
  sku_name            = "B0"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

# This creates the application definition
resource "azurerm_spring_cloud_app" "application" {
  name                = local.spring_apps_app_name
  resource_group_name = var.resource_group
  service_name        = azurerm_spring_cloud_service.application.name
  identity {
    type = "SystemAssigned"
  }
}

# This creates the application deployment. Terraform provider doesn't support dotnet yet
resource "azurerm_spring_cloud_java_deployment" "application_deployment" {
  name                = "default"
  spring_cloud_app_id = azurerm_spring_cloud_app.application.id
  instance_count      = 1
  runtime_version     = "Java_17"

  quota {
    cpu    = "1"
    memory = "1Gi"
  }

  environment_variables = {
    "SPRING_PROFILES_ACTIVE" = "prod,azure"
  }
}
