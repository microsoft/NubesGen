# Getting started with Bicep

:::tip NOTE
This documentation page is under construction. Improvements welcome!
:::

Bicep is a domain-specific language (DSL) for deploying Azure resources declaratively. It aims to drastically simplify the authoring experience with a cleaner syntax, improved type safety, and better support for modularity and code re-use.

After downloading the zip file from NubesGen, extract the contents to a folder on your computer. The folder contains `main.bicep` which uses Bicep modules inside subfolders.

## Get started with the Azure CLI

The Azure CLI is a command-line tool that provides a convenient way to create and manage Azure resources. You can use the Azure CLI to deploy Bicep files.

1. [Install the Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli)
2. Login to your Azure account by running `az login`

## Deploy the resources

Run the following command to provision the resources in your Azure account:

```
az deployment sub create --template-file main.bicep --location eastus
```

If you just want to provision a single resource in an existing resource group, use a command of this form:

```
az deployment group create --resource-group resource-group-name-here --template-file modules/postgresql/postgresql.bicep
```