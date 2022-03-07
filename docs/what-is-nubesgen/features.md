# Features

NubesGen is an Infrastructure as Code *generator*. Here are some of our _unique_ features.

[[toc]]

## Only supports Azure

The creators of NubesGen have deep expertise with Azure, and work at Microsoft. As we want to adhere to the [Project Philosophy](/what-is-nubesgen/philosophy), we are only supporting Azure cloud for now. 

## Terraform code generation

NubesGen generates an entire set of Terraform code and modules. Whenether you're new to Terraform or already experienced, you will be able to deploy your infrastructure with Terraform in minutes.

## Terraform remote state

If the "GitOps" option is selected, NubesGen will modify the generated Terraform code to use a remote state stored in an Azure Storage account.

## Bicep code generation

NubesGen generates an entire set of Bicep code and modules. Whenether you're new to Bicep or already experienced, you will be able to deploy your infrastructure with Bicep in minutes.

## GitHub Actions code generation

If the "GitOps" option is selected, NubesGen will generate one GitHub Actions workflow that will: 
- Deploy your infrastructure either with Terraform or Bicep,
- Build your code
- Deploy your code on the deployed infrastructure.

## GitOps workflow

By default, NubesGen only generate "[GitOps](/gitops/gitops-overview/)" workflows. You can easily create as many environments as you want with the `env-{environmentName}` branch naming convention. You can learn more about NubesGen and GitOps [here](/gitops/gitops-quick-start/)

## REST API

At its core, NubesGen is _just_ a REST API. You can use NubesGen [from another application or from the command line with cURL](/reference/rest-api/)?
