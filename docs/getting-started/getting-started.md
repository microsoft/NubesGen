# Getting started with NubesGen

NubesGen can be used through a [Web interface](https://nubesgen.com/) or the [command line](command-line.md), to generate a set of configuration files for you.

Those files ([described here](what-is-being-generated.md)) are of two different kinds:

- [Terraform](https://www.terraform.io/) configuration files describe the infrastructure that you want to manage.
- [GitOps](gitops-overview.md) configuration files are optional, and can fully automate the usage of Terraform.

## Using Terraform directly

This is the default option with NubesGen: you will need to apply the Terraform configuration manually. If you want a more automated setup, see the next section about GitOps.

1. Go to an existing GitHub repository (or create a new one), and clone it on your machine.
2. Either using the [Web interface](https://nubesgen.com/) or the [command line](command-line.md), generate a NubesGen package (either a `.tgz` or `.zip` file). The configuration selected in NubesGen should match what is needed for your project to run (for example, if your project needs a MySQL database, select a MySQL database in NubesGen).
3. Uncompress that file in your project's directory. You should have a new `terraform` directory inside your project's root folder.
4. Log into Azure using the Azure CLI, for example by typing `az login`.
5. Go into the new `terraform` folder, initialize Terraform and apply its configuration:
```bash
cd terraform
terraform init
terraform apply
```

This should create a new Azure resource group, in the form `rg-<your-project-name>`, in which several resources have been created. For example, if you selected Azure App Service and Azure database for MySQL, you should have an App Service plan, App Service instance and MySQL database created in your resource group.

You can go to the [Azure Portal](https://aka.ms/nubesgen-portal) to check those resources.

![Resource group created by NubesGen](assets/azure-resource-group-docker.png "Resource group created by NubesGen")

## Using Terraform with GitOps

Using the [GitOps option](gitops-overview.md), a specific GitHub Action file will be created, and this will automate:

- Running Terraform to manage the Azure infrastructure
- Deploying your application code to that infrastructure
