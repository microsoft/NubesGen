# NubesGen

_A cloud infrastructure generator for Terraform and Azure_

## 1-minute introduction to NubesGen

With NubesGen, going to production to Azure is painless, secured and on budget!

It is a graphical Web tool that generates a cloud infrastructure using Terraform: you select easy-to-understand options ("a MySQL database", "a medium-sized application server"), and NubesGen will generate a state-of-the-art configuration that you can import and tweak in your project.

## 5-minutes introduction to NubesGen

Terraform and Azure look complicated to you?

With Nubesgen, we have simplified the way to production: you answer some easy-to-understand options ("a MySQL database", "a medium-sized application server"), so you don't need to spend hours looking at the technical documentation.

NubesGen generates a Terraform configuration that is hand-made by Azure experts: it follows the current best practices, with a specific focus on security and scalability. We also provide budget estimates and dashboards, so you can be assured your finances will be under control.

For advanced users, this Terraform configuration can be modified and tweaked as usual: NubesGen is only here to get you started quickly!

## What is being generated?

NubesGen generates a `nubesgen.zip` file, that you can download from [https://nubesgen.com](https://nubesgen.com).

Unzipping that file provides the following directories:

```
- terraform
  |- azure
    |- dev
    |- prod
```

The `dev` directory contains a Terraform configuration for a _development_ setup: it focuses on having a quick and cheap environment for developing and testing.

The `prod` directory contains a Terraform configuration for a _production_ setup: it focuses on following security and scalability best practices for running code in production.

## Why only Azure?

There is nothing specific to Azure in NubesGen, but as our goal is to have high-quality templates and as this is being done by Azure experts, we are only focusing on Azure for the moment.

If you want to join the team to add support to another cloud, feel free to contact us!

## Is this working with other generators?

You should be able to use other tools like [https://start.spring.io/](https://start.spring.io/) or [https://www.jhipster.tech/](https://www.jhipster.tech/) with NubesGen.

## Running NugesGen

To generate a default application:

```
curl http://localhost:8080/nubesgen.zip -o nubesgen.zip
```

```
curl http://localhost:8080/nubesgen.zip | jar xv
```

If you want to pass some parameters:

```
curl http://localhost:8080/nubesgen.zip -d location=westeurope -d applicationName=myapplication -o nubesgen.zip
```

```
curl http://localhost:8080/nubesgen.zip -d location=westeurope -d applicationName=myapplication | jar xv
```
