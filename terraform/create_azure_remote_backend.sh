#!/bin/sh
RESOURCE_GROUP_NAME=rg-terraform-001
LOCATION=westeurope
STORAGE_ACCOUNT_NAME=stterraform$RANDOM$RANDOM
CONTAINER_NAME=tfstate
# Create resource group
az group create --name $RESOURCE_GROUP_NAME --location $LOCATION
# Create storage account
az storage account create --resource-group $RESOURCE_GROUP_NAME --name $STORAGE_ACCOUNT_NAME --sku Standard_LRS --encryption-services blob
# Get storage account key
ACCOUNT_KEY=$(az storage account keys list --resource-group $RESOURCE_GROUP_NAME --account-name $STORAGE_ACCOUNT_NAME --query [0].value -o tsv)
# Create blob container
az storage container create --name $CONTAINER_NAME --account-name $STORAGE_ACCOUNT_NAME --account-key $ACCOUNT_KEY
# Create service principal
SUBSCRIPTION_ID=$(az account show --query id --output tsv)
SERVICE_PRINCIPAL=$(az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/$SUBSCRIPTION_ID" --name="http://sp-$RESOURCE_GROUP_NAME")
echo "resource_group_name: $RESOURCE_GROUP_NAME"
echo "storage_account_name: $STORAGE_ACCOUNT_NAME"
echo "container_name: $CONTAINER_NAME"
echo "service_principal: $SERVICE_PRINCIPAL"
