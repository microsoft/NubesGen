[[ << GitOps overview ](gitops-overview.md) |[ Main documentation page ](README.md) |[ What is being generated >> ](what-is-being-generated.md)]

# GitOps with NubesGen quick start

[Do you want to understand first GitOps with NubesGen? Here is the overview](gitops-overview.md)

## Introduction

When using GitOps, Terraform will use [an Azure backend](https://www.terraform.io/docs/language/settings/backends/azurerm.html) to lock its state while it is being updated, as several GitHub Actions runs can occur in parallel.

This makes this setup far more robust than running Terraform manually, but adds a bit more complexity, as a specific resource group and a specific storage account need to created.

The following steps will guide you through creating those resources, and authorizing GitHub Actions to perform Azure resource management on your behalf.

## Configure GitOps in 5 steps

_Prerequisites_

__Tip:__ You can go to [https://aka.ms/nubesgen-azure-shell](https://aka.ms/nubesgen-azure-shell) and login with the Azure subscription you want to use. This will provide you with the 
mandatory prerequisites below (Bash and Azure CLI).

For this installation method to work, you need to have installed and configured the following tools:

- [Bash](https://fr.wikipedia.org/wiki/Bourne-Again_shell), which is installed by default on most Linux distributions and on Mac OS X. If you're using Windows, one solution is to use [WSL](https://aka.ms/nubesgen-install-wsl).
- [Azure CLI](https://aka.ms/nubesgen-install-az-cli). To login, use `az login`.
- (optional) [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`. This will automate creating the GitHub secrets for you, otherwise you will need to do it
manually.

_Automatic Installation (with a shell script)_

1. Create a GitHub repository to work in (or select one that you already created), and clone it on your local computer.
1. Open up a terminal in the repository you just cloned, and run the following script (or [download it](https://nubesgen.com/gitops/setup.sh) to read it and fine-tune it):
    ```bash
    bash -c "$(curl -fsSL https://nubesgen.com/gitops/setup.sh)"
    ```
1. Go to [https://nubesgen.com/](https://nubesgen.com/) to create your Terraform configuration, and select the `GitOps` option. Download the generated file and unzip it inside the Git repository you have just cloned.
1. You can now push the NubesGen code to your repository, for example by typing `git add . && git commit -m 'Configure GitOps with NubesGen' && git push`.
1. To use the new GitOps features, follow [GitOps overview](gitops-overview.md) and create a specific branch, for example
   `git checkout -b env-test && git push --set-upstream origin env-test`

_Manual Installation (without a shell script)_

<details>
<summary>This setup only replaces step 2 of the automatic installation, described above</summary>

Instead of running a shell script, you will manually create one Azure Storage account, and two GitHub secrets.

1. Setup some environment variables:
    ```bash
    # The resource group used by Terraform to store its remote state.
    RESOURCE_GROUP_NAME=rg-terraform-001
    # The location of the resource group. For example `eastus`. Leave blank to use your default location.
    LOCATION=
    # The storage account (inside the resource group) used by Terraform to store its remote state.
    TF_STORAGE_ACCOUNT=st$RANDOM$RANDOM$RANDOM$RANDOM
    # The container name (inside the storage account) used by Terraform to store its remote state.
    CONTAINER_NAME=tfstate
    ```
1. Create a new Azure Resource Group : `az group create --name $RESOURCE_GROUP_NAME --location $LOCATION`
1. Create the storage account : `az storage account create --resource-group $RESOURCE_GROUP_NAME --name $TF_STORAGE_ACCOUNT --sku Standard_LRS --allow-blob-public-access false --encryption-services blob`
1. Get the storage account key: `ACCOUNT_KEY=$(az storage account keys list --resource-group $RESOURCE_GROUP_NAME --account-name $TF_STORAGE_ACCOUNT --query '[0].value' -o tsv)`
1. Create a blob container: `az storage container create --name $CONTAINER_NAME --account-name $TF_STORAGE_ACCOUNT --account-key $ACCOUNT_KEY`
1. Get the subscription ID: `SUBSCRIPTION_ID=$(az account show --query id --output tsv --only-show-errors)`
1. Create a service principal: `SERVICE_PRINCIPAL=$(az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/$SUBSCRIPTION_ID" --sdk-auth --only-show-errors)`
1. Get the current GitHub remote repository: `REMOTE_REPO=$(git config --get remote.origin.url)`
1. Set the two GitHub secrets: `gh secret set AZURE_CREDENTIALS -b"$SERVICE_PRINCIPAL" -R $REMOTE_REPO && gh secret set TF_STORAGE_ACCOUNT -b"$TF_STORAGE_ACCOUNT" -R $REMOTE_REPO`

</details>

__Congratulations, you have set up GitOps with NubesGen on your project!__

## Using the GitOps workflow

As described in the [GitOps overview](gitops-overview.md), each time you create an `env-*` branch in Git, a new environment will be created for you.

That environment is an Azure resource group, containing all the resources configured with Terraform. When that environment is created, and each time you `git push` to that branch, two things will happen:

- The GitHub Action will apply the current Terraform configuration, so that your Azure resource group is synchronized with the configuration store in Git.
- The GitHub Action will then package and deploy the code stored in the Git branch, so that code runs on the infrastructure that was configured in the previous step.

[[ << GitOps overview ](gitops-overview.md) |[ Main documentation page ](README.md) |[ What is being generated >> ](what-is-being-generated.md)]
