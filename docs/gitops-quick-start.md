# GitOps quick start

[Do you want to understand first GitOps with NubesGen? Here is the overview](gitops-overview.md)

## Configure GitOps in 5 steps

1. Create a GitHub repository to work in (or select one that you already created), and clone it on your local computer.
1. Go to [https://nubesgen.azurewebsites.net/](https://nubesgen.azurewebsites.net/) to create your Terraform configuration, and select the `GitOps` option. Download the generated file and unzip it inside the Git repository you have just cloned.
1. Go to [https://shell.azure.com/](https://shell.azure.com/) and login with the Azure subscription you want to use. In this shell, run the following script:
    <details>
    <summary>For first-time users</summary>

    ```bash
    RESOURCE_GROUP_NAME=rg-terraform-001
    LOCATION=westeurope
    TF_STORAGE_ACCOUNT=st$RANDOM$RANDOM$RANDOM$RANDOM
    CONTAINER_NAME=tfstate
    # Create resource group
    az group create --name $RESOURCE_GROUP_NAME --location $LOCATION
    # Create storage account
    az storage account create --resource-group $RESOURCE_GROUP_NAME --name $TF_STORAGE_ACCOUNT --sku Standard_LRS --encryption-services blob
    # Get storage account key
    ACCOUNT_KEY=$(az storage account keys list --resource-group $RESOURCE_GROUP_NAME --account-name $TF_STORAGE_ACCOUNT --query [0].value -o tsv)
    # Create blob container
    az storage container create --name $CONTAINER_NAME --account-name $TF_STORAGE_ACCOUNT --account-key $ACCOUNT_KEY
    # Create service principal
    SUBSCRIPTION_ID=$(az account show --query id --output tsv)
    SERVICE_PRINCIPAL=$(az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/$SUBSCRIPTION_ID" --sdk-auth)
    echo "AZURE_CREDENTIALS: $SERVICE_PRINCIPAL"
    echo "TF_STORAGE_ACCOUNT: $TF_STORAGE_ACCOUNT"
    ```
    </details>
   <details>
    <summary>For existing users, who are already using GitOps with NubesGen on their subscription</summary>

    ```bash
    RESOURCE_GROUP_NAME=rg-terraform-001
    LOCATION=westeurope
    TF_STORAGE_ACCOUNT=st$RANDOM$RANDOM$RANDOM$RANDOM
    CONTAINER_NAME=tfstate
    # Create storage account
    az storage account create --resource-group $RESOURCE_GROUP_NAME --name $TF_STORAGE_ACCOUNT --sku Standard_LRS --encryption-services blob
    # Get storage account key
    ACCOUNT_KEY=$(az storage account keys list --resource-group $RESOURCE_GROUP_NAME --account-name $TF_STORAGE_ACCOUNT --query [0].value -o tsv)
    # Create blob container
    az storage container create --name $CONTAINER_NAME --account-name $TF_STORAGE_ACCOUNT --account-key $ACCOUNT_KEY
    # Create service principal
    SUBSCRIPTION_ID=$(az account show --query id --output tsv)
    SERVICE_PRINCIPAL=$(az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/$SUBSCRIPTION_ID" --sdk-auth)
    echo "AZURE_CREDENTIALS: $SERVICE_PRINCIPAL"
    echo "TF_STORAGE_ACCOUNT: $TF_STORAGE_ACCOUNT"
    ```
    </details>
1. The script above generates two variables, `AZURE_CREDENTIALS` and `TF_STORAGE_ACCOUNT`. Go to your GitHub repository's settings, and create two secrets using those names and values.
1. You can now push the NubesGen code to your repository, for example by typing `git add . && git commit -m 'Configure GitOps with NubesGen' && git push`.

__Congratulations, you have setup GitOps with NubesGen on your project!__
