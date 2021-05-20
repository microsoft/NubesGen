terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.56"
    }
  }
}

provider "azurerm" {
  features {}
}

locals {
  // If an environment is set up (dev, test, prod...), it is used in the application name
  environment      = var.environment == "" ? "dev" : var.environment
  application_name = var.environment == "" ? var.application_name : "${var.application_name}-${local.environment}"
  resource_group   = "rg-${local.application_name}-001"
}

resource "azurerm_resource_group" "main" {
  name     = local.resource_group
  location = var.location

  tags = {
    "terraform"   = "true"
    "environment" = local.environment
  }
}

module "application" {
  source            = "./modules/app-service"
  resource_group    = azurerm_resource_group.main.name
  application_name  = local.application_name
  environment       = local.environment
  location          = var.location

  azure_application_insights_instrumentation_key = module.application-insights.azure_application_insights_instrumentation_key
}

module "application-insights" {
  source            = "./modules/application-insights"
  resource_group    = azurerm_resource_group.main.name
  application_name  = local.application_name
  environment       = local.environment
  location          = var.location
}
