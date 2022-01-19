# Azure Spring Cloud is not yet supported in azurecaf_name
locals {
  spring_cloud_service_name = "asc-${var.application_name}-${var.environment}"
  spring_cloud_app_name     = "app-${var.application_name}"
  cosmosdb_association_name = "${var.application_name}-cosmos"
  redis_association_name    = "${var.application_name}-redis"

  # Azure Spring Cloud Resource Provider object id. It is a constant and it is required to manage the VNET.
  azure_spring_cloud_provisioner_object_id = "d2531223-68f9-459e-b225-5592f90d145e"
}

# Assign Owner role to Azure Spring Cloud Resource Provider on the Virtual Network used by the deployed service
# Make sure the SPID used to provision terraform has privileges to do role assignments.
resource "azurerm_role_assignment" "provider_owner" {
  scope                = var.virtual_network_id
  role_definition_name = "Owner"
  principal_id         = local.azure_spring_cloud_provisioner_object_id
}

# This creates the Azure Spring Cloud that the service use
resource "azurerm_spring_cloud_service" "application" {
  name                = local.spring_cloud_service_name
  resource_group_name = var.resource_group
  location            = var.location
  sku_name            = "S0"

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
  trace {
    connection_string = var.azure_application_insights_connection_string
  }

  network {
    app_subnet_id             = var.app_subnet_id
    service_runtime_subnet_id = var.service_subnet_id
    cidr_ranges               = var.cidr_ranges
  }

  depends_on = [
    azurerm_role_assignment.provider_owner
  ]
}

# Gets the Azure Spring Cloud internal load balancer IP address once it is deployed
data "azurerm_lb" "asc_internal_lb" {
  resource_group_name = "ap-svc-rt_${azurerm_spring_cloud_service.application.name}_${azurerm_spring_cloud_service.application.location}"
  name                = "kubernetes-internal"
  depends_on = [
    azurerm_spring_cloud_service.application
  ]
}

# Create DNS zone
resource "azurerm_private_dns_zone" "private_dns_zone" {
  name                = "private.azuremicroservices.io"
  resource_group_name = var.resource_group
}

# Link DNS to Azure Spring Cloud virtual network
resource "azurerm_private_dns_zone_virtual_network_link" "private_dns_zone_link_asc" {
  name                  = "asc-dns-link"
  resource_group_name   = var.resource_group
  private_dns_zone_name = azurerm_private_dns_zone.private_dns_zone.name
  virtual_network_id    = var.virtual_network_id
}

# Creates an A record that points to Azure Spring Cloud internal balancer IP
resource "azurerm_private_dns_a_record" "internal_lb_record" {
  name                = "*"
  zone_name           = azurerm_private_dns_zone.private_dns_zone.name
  resource_group_name = var.resource_group
  ttl                 = 300
  records             = [data.azurerm_lb.asc_internal_lb.private_ip_address]
}

# This creates the application definition
resource "azurerm_spring_cloud_app" "application" {
  name                = local.spring_cloud_app_name
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
  runtime_version     = "Java_11"

  quota {
    cpu    = "1"
    memory = "1Gi"
  }

  environment_variables = {
    "SPRING_PROFILES_ACTIVE" = "prod,azure"

    # Required for configuring the azure-spring-boot-starter-keyvault-secrets library
    "AZURE_KEYVAULT_ENABLED" = "true"
    "AZURE_KEYVAULT_URI"     = var.vault_uri

    "SPRING_DATASOURCE_URL"      = "jdbc:postgresql://${var.database_url}"
    # Credentials should be retrieved from Azure Key Vault
    "SPRING_DATASOURCE_USERNAME" = "stored-in-azure-key-vault"
    "SPRING_DATASOURCE_PASSWORD" = "stored-in-azure-key-vault"

    "SPRING_REDIS_HOST"     = var.azure_redis_host
    # Credentials should be retrieved from Azure Key Vault
    "SPRING_REDIS_PASSWORD" = "stored-in-azure-key-vault"
    "SPRING_REDIS_PORT"     = "6380"
    "SPRING_REDIS_SSL"      = "true"

    "AZURE_STORAGE_ACCOUNT_NAME"  = var.azure_storage_account_name
    # Credentials should be retrieved from Azure Key Vault
    "AZURE_STORAGE_ACCOUNT_KEY"   = "stored-in-azure-key-vault"
    "AZURE_STORAGE_BLOB_ENDPOINT" = var.azure_storage_blob_endpoint

    "SPRING_DATA_MONGODB_DATABASE" = var.azure_cosmosdb_mongodb_database
    # Credentials should be retrieved from Azure Key Vault
    "SPRING_DATA_MONGODB_URI"      = "stored-in-azure-key-vault"
  }
}

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault_access_policy" "application" {
  key_vault_id = var.vault_id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = azurerm_spring_cloud_app.application.identity[0].principal_id

  secret_permissions = [
    "Get",
    "List"
  ]
}
