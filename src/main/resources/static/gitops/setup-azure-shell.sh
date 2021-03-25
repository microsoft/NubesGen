#!/bin/sh
#
# This script sets up GitOps with NubesGen, running inside the Azure Shell.
#
# Read the documentation at https://github.com/microsoft/NubesGen/blob/main/docs/gitops-quick-start.md
#

# Color output
green() {
    printf "\033[32m$@${NC}\n"
}

# Set environment variables
RESOURCE_GROUP_NAME=rg-terraform-001
LOCATION=eastus
TF_STORAGE_ACCOUNT=st$RANDOM$RANDOM$RANDOM$RANDOM
CONTAINER_NAME=tfstate

# Execute commands
echo $(green "(1/5) Create resource group \"$RESOURCE_GROUP_NAME\"")
if [ $(az group exists --name $RESOURCE_GROUP_NAME) = false ]; then
  az group create --name $RESOURCE_GROUP_NAME --location $LOCATION -o none
fi
echo $(green "(2/5) Create storage account \"$TF_STORAGE_ACCOUNT\"")
az storage account create --resource-group $RESOURCE_GROUP_NAME --name $TF_STORAGE_ACCOUNT --sku Standard_LRS --encryption-services blob -o none
echo $(green "(3/5) Get storage account key")
ACCOUNT_KEY=$(az storage account keys list --resource-group $RESOURCE_GROUP_NAME --account-name $TF_STORAGE_ACCOUNT --query '[0].value' -o tsv)
echo $(green "(4/5) Create blob container")
az storage container create --name $CONTAINER_NAME --account-name $TF_STORAGE_ACCOUNT --account-key $ACCOUNT_KEY -o none
echo $(green "(5/5) Create service principal")
SUBSCRIPTION_ID=$(az account show --query id --output tsv --only-show-errors)
SERVICE_PRINCIPAL=$(az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/$SUBSCRIPTION_ID" --sdk-auth --only-show-errors)
echo $(green "-- GitHub secrets to configure --")
echo "AZURE_CREDENTIALS: $SERVICE_PRINCIPAL"
echo "TF_STORAGE_ACCOUNT: $TF_STORAGE_ACCOUNT"
echo $(green "---------------------------------")
