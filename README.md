# NubesGen
![Continuous Integration](https://github.com/microsoft/NubesGen/workflows/Continuous%20Integration/badge.svg)

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
| runtime |  The language and framework used to run the application | JAVA (default), SPRING, DOTNET | `http://localhost:8080/myapplication.tgz -d '{ "runtime": "JAVA"' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?runtime=java`  |
| application  | Type of application: Web app or serverless  | APP_SERVICE (default), FUNCTION | `http://localhost:8080/myapplication.tgz -d '{ "application": { "type": "FUNCTION", "tier": "CONSUMPTION"}}' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?application=function`  |
| region  |  Azure Region where the resource will be located | Run `az account list-locations` | `http://localhost:8080/myapplication.tgz -d '{ "region": "westeurope"}' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?region=westeurope`  |
| database  |  The database | NONE (default), SQL_SERVER, MYSQL, POSTGRESQL  | `http://localhost:8080/myapplication.tgz -d '{ "database": { "type": "MYSQL", "tier": "BASIC"}}' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?database=mysql`  |
| gitops  |  If [GitOps](docs/gitops-overview.md) is enabled | FALSE (default), TRUE  | `http://localhost:8080/myapplication.tgz -d '{ "gitops": "true"}' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?gitops=true`  |

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
| COSMOSDB_MONGODB  | Add support for Cosmos DB with the MongoDB API  | `http://localhost:8080/myapplication.tgz -d '{ "region": "westeurope", "addons": [{ "type": "COSMOSDB_MONGODB", "tier": "BASIC"}]}' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?addons=cosmosdb_mongodb`  |

_In a GET request, you can configure several addons by separating them with a comma, for example `addons=storage_blob,redis`_

Here is a complete example:

```
curl "http://localhost:8080/myapplication.tgz" -d '{ "region": "westeurope", "runtime": "spring", "database": { "type": "MYSQL", "tier": "BASIC"}, "addons": [{ "type": "STORAGE_BLOB", "tier": "BASIC"}, { "type": "REDIS", "tier": "BASIC"}]}' -H "Content-Type: application/json"  | tar -xzvf -
```
```
curl "http://localhost:8080/myapplication.tgz?region=westeurope&runtime=spring&database=MYSQL&addons=STORAGE_BLOB,REDIS"  | tar -xzvf -
```

## Working on the project

The easiest way to work on the project is to use [Visual Studio Code](https://code.visualstudio.com/) with [Docker](https://docs.docker.com/get-docker/) and the [Remote Development Extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.vscode-remote-extensionpack).

Once you have the project cloned on your machine, open the VS Code command palette and select **Reopen Folder in Container**. It will take a few minutes the first time while the container image is building, after that you're ready to code.

| Command                          | Action                                          |
|----------------------------------|-------------------------------------------------|
| `./mvnw package`                 | Generates .jar package in `/target` folder      |
| `./mvnw test`                    | Runs tests                                      |
| `java -jar target/nubesgen*.jar` | Starts NubesGen server on http://localhost:8080 |

## Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Trademarks

This project may contain trademarks or logos for projects, products, or services. Authorized use of Microsoft
trademarks or logos is subject to and must follow
[Microsoft's Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general).
Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion or imply Microsoft sponsorship.
Any use of third-party trademarks or logos are subject to those third-party's policies.
