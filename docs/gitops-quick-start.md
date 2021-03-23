# GitOps with NubesGen quick start

[Do you want to understand first GitOps with NubesGen? Here is the overview](gitops-overview.md)

## Introduction

When using GitOps, Terraform will use [an Azure backend](https://www.terraform.io/docs/language/settings/backends/azurerm.html) to lock its state while it is being updated, as several GitHub Actions runs can occur in parallel.

This makes this setup far more robust than running Terraform manually, but adds a bit more complexity, as a specific resource group and a specific storage account need to created.

The following steps will guide you through creating those resources, and authorizing GitHub Actions to perform Azure resource management on your behalf.

_There are two ways to configure GitOps with NubesGen: a 5-step installation using the command line, or a 6-step 
installation using only a Web browser_

## Configure GitOps in 5 steps, using the command line

_Prerequisites_

For this installation method to work, you need to have installed and configured the following tools:

- [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli). To login, use `az login`.
- [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`.

_Installation_

1. Create a GitHub repository to work in (or select one that you already created), and clone it on your local computer.
1. Go to [https://nubesgen.azurewebsites.net/](https://nubesgen.azurewebsites.net/) to create your Terraform configuration, and select the `GitOps` option. Download the generated file and unzip it inside the Git repository you have just cloned.
1. Open up a terminal in the repository you just cloned, and run the following script:
    <details>
    <summary>Run this script</summary>

    ```bash
    RESOURCE_GROUP_NAME=rg-terraform-001
    LOCATION=westeurope
    TF_STORAGE_ACCOUNT=st$RANDOM$RANDOM$RANDOM$RANDOM
    CONTAINER_NAME=tfstate
    # Create resource group
    if [ $(az group exists --name $RESOURCE_GROUP_NAME) = false ]; then
      az group create --name $RESOURCE_GROUP_NAME --location $LOCATION -o none
    fi
    # Create storage account
    az storage account create --resource-group $RESOURCE_GROUP_NAME --name $TF_STORAGE_ACCOUNT --sku Standard_LRS --encryption-services blob -o none
    # Get storage account key
    ACCOUNT_KEY=$(az storage account keys list --resource-group $RESOURCE_GROUP_NAME --account-name $TF_STORAGE_ACCOUNT --query '[0].value' -o tsv)
    # Create blob container
    az storage container create --name $CONTAINER_NAME --account-name $TF_STORAGE_ACCOUNT --account-key $ACCOUNT_KEY -o none
    # Create service principal
    SUBSCRIPTION_ID=$(az account show --query id --output tsv --only-show-errors)
    SERVICE_PRINCIPAL=$(az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/$SUBSCRIPTION_ID" --sdk-auth --only-show-errors)
    # Create secrets in GitHub
    gh secret set AZURE_CREDENTIALS -b"$SERVICE_PRINCIPAL"
    gh secret set TF_STORAGE_ACCOUNT -b"$TF_STORAGE_ACCOUNT"

    ```
    </details>
1. You can now push the NubesGen code to your repository, for example by typing `git add . && git commit -m 'Configure GitOps with NubesGen' && git push`.
1. To use the new GitOps features, follow [GitOps overview](gitops-overview.md) and create a specific branch, for example
   `git checkout -b env-test && git push --set-upstream origin env-test`

__Congratulations, you have setup GitOps with NubesGen on your project!__

## Configure GitOps in 6 steps, using your Web browser

1. Create a GitHub repository to work in (or select one that you already created), and clone it on your local computer.
1. Go to [https://nubesgen.azurewebsites.net/](https://nubesgen.azurewebsites.net/) to create your Terraform configuration, and select the `GitOps` option. Download the generated file and unzip it inside the Git repository you have just cloned.
1. Go to [https://shell.azure.com/](https://shell.azure.com/) and login with the Azure subscription you want to use. In this shell, run the following script:
    <details>
    <summary>Run this script</summary>

    ```bash
    RESOURCE_GROUP_NAME=rg-terraform-001
    LOCATION=westeurope
    TF_STORAGE_ACCOUNT=st$RANDOM$RANDOM$RANDOM$RANDOM
    CONTAINER_NAME=tfstate
    # Create resource group
    if [ $(az group exists --name $RESOURCE_GROUP_NAME) = false ]; then
      az group create --name $RESOURCE_GROUP_NAME --location $LOCATION -o none
    fi
    # Create storage account
    az storage account create --resource-group $RESOURCE_GROUP_NAME --name $TF_STORAGE_ACCOUNT --sku Standard_LRS --encryption-services blob -o none
    # Get storage account key
    ACCOUNT_KEY=$(az storage account keys list --resource-group $RESOURCE_GROUP_NAME --account-name $TF_STORAGE_ACCOUNT --query '[0].value' -o tsv)
    # Create blob container
    az storage container create --name $CONTAINER_NAME --account-name $TF_STORAGE_ACCOUNT --account-key $ACCOUNT_KEY -o none
    # Create service principal
    SUBSCRIPTION_ID=$(az account show --query id --output tsv --only-show-errors)
    SERVICE_PRINCIPAL=$(az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/$SUBSCRIPTION_ID" --sdk-auth --only-show-errors)
    echo "AZURE_CREDENTIALS: $SERVICE_PRINCIPAL"
    echo "TF_STORAGE_ACCOUNT: $TF_STORAGE_ACCOUNT"

    ```
    </details>
1. The script above generates two variables, `AZURE_CREDENTIALS` and `TF_STORAGE_ACCOUNT`. Go to your GitHub repository's settings, and create two secrets using those names and values.
1. You can now push the NubesGen code to your repository, for example by typing `git add . && git commit -m 'Configure GitOps with NubesGen' && git push`.
1. To use the new GitOps features, follow [GitOps overview](gitops-overview.md) and create a specific branch, for example 
   `git checkout -b env-test && git push --set-upstream origin env-test`

__Congratulations, you have setup GitOps with NubesGen on your project!__

## Using the GitOps workflow

As described in the [GitOps overview](gitops-overview.md), each time you create an `env-*` branch in Git, a new environment will be created for you.

That environment is an Azure resource group, containing all the resources configured with Terraform. When that environment is created, and each time you `git push` to that branch, two things will happen:

- The GitHub Action will apply the current Terraform configuration, so that your Azure resource group is synchronized with the configuration store in Git.
- The GitHub Action will then package and deploy the code stored in the Git branch, so that code runs on the infrastructure that was configured in the previous step.
