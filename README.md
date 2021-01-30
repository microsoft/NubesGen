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

## Why the name "NubesGen"?

NubesGen is a code generator for Terraform. In Latin, Terra means "earth" and Nubes means "cloud". 

## Why only Azure?

There is nothing specific to Azure in NubesGen, but as our goal is to have high-quality templates and as this is being done by Azure experts, we are only focusing on Azure for the moment.

If you want to join the team to add support to another cloud, feel free to contact us!

## Is this working with other generators?

You should be able to use other tools like [https://start.spring.io/](https://start.spring.io/) or [https://www.jhipster.tech/](https://www.jhipster.tech/) with NubesGen.

## Running NugesGen from the command line with cURL

To automate your workflow, you don't need to use a Web interface! Use cURL to directly download and use your NubesGen configuration.

To generate a default application:

```
curl "http://localhost:8080/nubesgen.tgz" | tar -xzvf -
```

or (as a .zip file):

```
curl "http://localhost:8080/nubesgen.zip" | jar xv
```

If you want to pass some parameters in POST:

```
curl "http://localhost:8080/myapplication.tgz" -d '{ "region": "westeurope", "database": { "type": "MYSQL", "tier": "BASIC"}}' -H "Content-Type: application/json"  | tar -xzvf -
```

If you want to pass some parameters in GET:

```
curl "http://localhost:8080/myapplication.tgz?region=westeurope&database=mysql"  | tar -xzvf -
```

## Complete parameters list

### Main parameters

| Name  | Description  | Values  | POST example | GET example  |
|---|---|---|---|---|
| application  | Type of application: Web app or serverless  | APP_SERVICE, FUNCTION  | `http://localhost:8080/myapplication.tgz -d '{ "application": { "type": "FUNCTION", "tier": "CONSUMPTION"}}' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?application=function`  |
| region  |  Azure Region where the resource will be located | Run `az account list-locations`  | `http://localhost:8080/myapplication.tgz -d '{ "region": "westeurope"}' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?region=westeurope`  |
| database  |  The database | NONE, SQL_SERVER, MYSQL, POSTGRESQL  | `http://localhost:8080/myapplication.tgz -d '{ "database": { "type": "MYSQL", "tier": "BASIC"}}' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?database=mysql`  |

_In a GET request, parameters can be in uppercase or lowercase, for example `database=MYSQL`or `database=mysql`_

### Tiers

Application types and databases have different tiers.

- Each resource type have its own specific tiers.
- NubesGen provides the main available tiers for running development and production workloads.
- For each of those tiers, NubesGen configures the less expensive options possible. If you want to upgrade your resource, you'll need to modify the Terraform configuration (our recommended solution) or modify the resource using the Azure portal.
- If no tier is provided, NubesGen will use the less expensive one.

We provide the following tiers per resource type:

| Resource type  | Available tiers  | Example |
|---|---|---|
| APP_SERVICE | FREE, BASIC, STANDARD | `http://localhost:8080/myapplication.tgz?application=app_service.premium` |
| FUNCTION | CONSUMPTION, PREMIUM | `http://localhost:8080/myapplication.tgz?application=function.premium` |
| SQL_SERVER | SERVERLESS, GENERAL_PURPOSE | `http://localhost:8080/myapplication.tgz?database=sql_server.basic` |
| MYSQL | BASIC, GENERAL_PURPOSE | `http://localhost:8080/myapplication.tgz?database=mysql.general_purpose` |
| POSTGRESQL |BASIC, GENERAL_PURPOSE | `http://localhost:8080/myapplication.tgz?database=postgresql.general_purpose` |

### Add-ons

You can add "addons", which are specific technologies added to your stack:

| Name  | Description  | POST example | GET example  |
|---|---|---|---|
| STORAGE_BLOB  | Add support for Azure Blob Storage  | `http://localhost:8080/myapplication.tgz -d '{ "region": "westeurope", "addons": [{ "type": "STORAGE_BLOB", "tier": "BASIC"}]}' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?addons=storage_blob`  |
| REDIS  | Add support for Azure Cache for Redis  | `http://localhost:8080/myapplication.tgz -d '{ "region": "westeurope", "addons": [{ "type": "REDIS", "tier": "BASIC"}]}' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?addons=redis`  |

_In a GET request, you can configure several addons by separating them with a comma, for example `addons=storage_blob,redis`_

Here is a complete example:

```
curl "http://localhost:8080/myapplication.tgz" -d '{ "region": "westeurope", "database": { "type": "MYSQL", "tier": "BASIC"}, "addons": [{ "type": "STORAGE_BLOB", "tier": "BASIC"}, { "type": "REDIS", "tier": "BASIC"}]}' -H "Content-Type: application/json"  | tar -xzvf -
```
```
curl "http://localhost:8080/myapplication.tgz?region=westeurope&database=MYSQL&addons=STORAGE_BLOB,REDIS"  | tar -xzvf -
```