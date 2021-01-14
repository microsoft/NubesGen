# NubesGen

_A cloud infrastructure generator for Terraform and Azure_

[https://nubesgen.azurewebsites.net/](https://nubesgen.azurewebsites.net/)

## 1-minute introduction to NubesGen

With NubesGen, going to production to Azure is painless, secured and on budget!

It is a graphical Web tool that generates a cloud infrastructure using Terraform: you select easy-to-understand options ("a MySQL database", "a medium-sized application server"), and NubesGen will generate a state-of-the-art configuration that you can import and tweak in your project.

## 5-minutes introduction to NubesGen

Terraform and Azure look complicated to you?

With Nubesgen, we have simplified the way to production: you answer some easy-to-understand options ("a MySQL database", "a medium-sized application server"), so you don't need to spend hours looking at the technical documentation.

NubesGen generates a Terraform configuration that is hand-made by Azure experts: it follows the current best practices, with a specific focus on security and scalability. We also provide budget estimates and dashboards, so you can be assured your finances will be under control.

For advanced users, this Terraform configuration can be modified and tweaked as usual: NubesGen is only here to get you started quickly!

## What is being generated?

NubesGen generates a `nubesgen.tgz` file, that you can download from [https://nubesgen.com](https://nubesgen.com).

Unzipping that file provides a similar structure:

```
- terraform
  |- modules
    |- app-service
      |- main.tf
      |- outputs.tf
      |- README.md
      |- variables.tf
    |- mysql
      |- main.tf
      |- outputs.tf
      |- README.md
      |- variables.tf
  |- main.tf
  |- outputs.tf
  |- README.md
  |- variables.tf
```

In this example, we have a Terraform configuration that uses two modules (App Service and MySQL), that are ready for 
deployment.

To deploy your infrastructure, all you need to do is initialize Terraform and apply its configuration:

```bash
terraform init && terraform apply -auto-approve
```

## Why only Azure?

There is nothing specific to Azure in NubesGen, but as our goal is to have high-quality templates and as this is being done by Azure experts, we are only focusing on Azure for the moment.

If you want to join the team to add support to another cloud, feel free to contact us!

## Is this working with other generators?

You should be able to use other tools like [https://start.spring.io/](https://start.spring.io/) or [https://www.jhipster.tech/](https://www.jhipster.tech/) with NubesGen.

## Running NugesGen from the command line with cURL

To automate your workflow, you don't need to use a Web interface! Use cURL to directly download and use your NubesGen configuration.

To generate a default application:

```
curl http://localhost:8080/nubesgen.tgz | tar -xzvf -
```

or (as a .zip file):

```
curl http://localhost:8080/nubesgen.zip | jar xv
```

If you want to pass some parameters:

```
curl http://localhost:8080/nubesgen.tgz -d '{ "applicationName": "myapplication", "location": "westeurope", "database": { "type": "MYSQL", "size": "S"}}' -H "Content-Type: application/json"  | tar -xzvf -
```
