#!/usr/bin/env bash
################################################
# This script sets up GitOps with NubesGen.
#
# Read the documentation at https://github.com/microsoft/NubesGen/blob/main/docs/gitops-quick-start.md
################################################

################################################
# Set environment variables - those are the main variables you might want to configure.
#
# The resource group used by Terraform to store its remote state.
RESOURCE_GROUP_NAME=rg-terraform-001
# The location of the resource group. For example `eastus`. Leave blank to use your default location.
LOCATION=
# The storage account (inside the resource group) used by Terraform to store its remote state.
TF_STORAGE_ACCOUNT=st$RANDOM$RANDOM$RANDOM$RANDOM
# The container name (inside the storage account) used by Terraform to store its remote state.
CONTAINER_NAME=tfstate
# End set environment variables
################################################

set -Eeuo pipefail
trap cleanup SIGINT SIGTERM ERR EXIT

cleanup() {
  trap - SIGINT SIGTERM ERR EXIT
  # script cleanup here
}

setup_colors() {
  if [[ -t 2 ]] && [[ -z "${NO_COLOR-}" ]] && [[ "${TERM-}" != "dumb" ]]; then
    NOFORMAT='\033[0m' RED='\033[0;31m' GREEN='\033[0;32m' ORANGE='\033[0;33m' BLUE='\033[0;34m' PURPLE='\033[0;35m' CYAN='\033[0;36m' YELLOW='\033[1;33m'
  else
    NOFORMAT='' RED='' GREEN='' ORANGE='' BLUE='' PURPLE='' CYAN='' YELLOW=''
  fi
}

msg() {
  echo >&2 -e "${1-}"
}

setup_colors

msg "${GREEN}Setting up GitOps with NubesGen..."

# get default location if not set at the beginning of this file
if [ "$LOCATION" == '' ] ; then
    {
      az config get defaults.location --only-show-errors > /dev/null 2>&1
      LOCATION_DEFAULTS_SETUP=$?
    } || {
      LOCATION_DEFAULTS_SETUP=0
    }
    # if no default location is set, fallback to "eastus"
    if [ "$LOCATION_DEFAULTS_SETUP" -eq 1 ]; then
      LOCATION=eastus
    else
      LOCATION=$(az config get defaults.location --only-show-errors | jq -r .value)
    fi
fi

# Check AZ CLI status
msg "${GREEN}(1/8) Checking Azure CLI status...${NOFORMAT}"
{
  az > /dev/null
} || {
  msg "${RED}Azure CLI is not installed."
  msg "${GREEN}Go to https://aka.ms/nubesgen-install-az-cli to install Azure CLI."
  exit 1;
}
{
  az account show > /dev/null
} || {
  msg "${RED}You are not authenticated with Azure CLI."
  msg "${GREEN}Run \"az login\" to authenticate."
  exit 1;
}

msg "${YELLOW}Azure CLI is installed and configured!"

# Check GitHub CLI status
msg "${GREEN}(2/8) Checking GitHub CLI status...${NOFORMAT}"
USE_GITHUB_CLI=false
{
  gh auth status && USE_GITHUB_CLI=true && msg "${YELLOW}GitHub CLI is installed and configured!"
} || {
  msg "${YELLOW}Cannot use the GitHub CLI. ${GREEN}No worries! ${YELLOW}We'll set up the GitHub secrets manually."
  USE_GITHUB_CLI=false
}

# Execute commands
msg "${GREEN}(3/8) Create resource group \"$RESOURCE_GROUP_NAME\""

if [ $(az group exists --name $RESOURCE_GROUP_NAME) = false ]; then
  az group create --name $RESOURCE_GROUP_NAME --location $LOCATION -o none
fi
msg "${GREEN}(4/8) Create storage account \"$TF_STORAGE_ACCOUNT\""
az storage account create --resource-group $RESOURCE_GROUP_NAME --name $TF_STORAGE_ACCOUNT --sku Standard_LRS --allow-blob-public-access false --encryption-services blob -o none
msg "${GREEN}(5/8) Get storage account key"
ACCOUNT_KEY=$(az storage account keys list --resource-group $RESOURCE_GROUP_NAME --account-name $TF_STORAGE_ACCOUNT --query '[0].value' -o tsv)
msg "${GREEN}(6/8) Create blob container"
az storage container create --name $CONTAINER_NAME --account-name $TF_STORAGE_ACCOUNT --account-key $ACCOUNT_KEY -o none
msg "${GREEN}(7/8) Create service principal"
SUBSCRIPTION_ID=$(az account show --query id --output tsv --only-show-errors)
SERVICE_PRINCIPAL=$(az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/$SUBSCRIPTION_ID" --sdk-auth --only-show-errors)
msg "${GREEN}(8/8) Create secrets in GitHub"
if $USE_GITHUB_CLI; then
  {
    msg "${GREEN}Using the GitHub CLI to set secrets.${NOFORMAT}"
    gh secret set AZURE_CREDENTIALS -b"$SERVICE_PRINCIPAL" && gh secret set TF_STORAGE_ACCOUNT -b"$TF_STORAGE_ACCOUNT"
  } || {
    USE_GITHUB_CLI=false
  }
fi
if [ $USE_GITHUB_CLI == false ]; then
  msg "${NOFORMAT}======================MANUAL SETUP======================================"
  msg "${GREEN}Using your Web browser to set up secrets..."
  msg "${NOFORMAT}Go to the GitHub repository you want to configure."
  msg "${NOFORMAT}In the \"settings\", go to the \"secrets\" tab and add two new secrets:"
  msg "(in ${YELLOW}yellow the secret name${NOFORMAT} and in ${GREEN}green the secret value${NOFORMAT})"
  msg "${YELLOW}\"AZURE_CREDENTIALS\""
  msg "${GREEN}$SERVICE_PRINCIPAL"
  msg "${YELLOW}\"TF_STORAGE_ACCOUNT\""
  msg "${GREEN}$TF_STORAGE_ACCOUNT"
  msg "${NOFORMAT}========================================================================"
fi
msg "${GREEN}Congratulations! You have successfully configured GitOps with NubesGen."
