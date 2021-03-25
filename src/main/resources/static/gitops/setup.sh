#!/bin/sh
#
# This script sets up GitOps with NubesGen.
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
echo $(green "Create resource group \"$RESOURCE_GROUP_NAME\"")
if [ $(az group exists --name $RESOURCE_GROUP_NAME) = false ]; then
  az group create --name $RESOURCE_GROUP_NAME --location $LOCATION -o none
fi
echo $(green "Create storage account \"TF_STORAGE_ACCOUNT\"")
az storage account create --resource-group $RESOURCE_GROUP_NAME --name $TF_STORAGE_ACCOUNT --sku Standard_LRS --encryption-services blob -o none
echo $(green "Get storage account key")
ACCOUNT_KEY=$(az storage account keys list --resource-group $RESOURCE_GROUP_NAME --account-name $TF_STORAGE_ACCOUNT --query '[0].value' -o tsv)
echo $(green "Create blob container")
az storage container create --name $CONTAINER_NAME --account-name $TF_STORAGE_ACCOUNT --account-key $ACCOUNT_KEY -o none
echo $(green "Create service principal")
SUBSCRIPTION_ID=$(az account show --query id --output tsv --only-show-errors)
SERVICE_PRINCIPAL=$(az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/$SUBSCRIPTION_ID" --sdk-auth --only-show-errors)
echo $(green "Create secrets in GitHub")
gh secret set AZURE_CREDENTIALS -b"$SERVICE_PRINCIPAL"
gh secret set TF_STORAGE_ACCOUNT -b"$TF_STORAGE_ACCOUNT"
echo $(green "Congratulations! You have successfully configured NugesGen")
