# NubesGen REST API reference

## Generating a project using cURL

To automate your workflow, you don't need to use a Web interface! Use cURL to directly download and use your NubesGen configuration.

To generate a default application:

```bash
curl "https://nubesgen.com/demo.tgz" | tar -xzvf -
```

or (as a .zip file):

```bash
curl "https://nubesgen.com/demo.zip" | jar xv
```

If you want to pass some parameters in POST:

```bash
curl "https://nubesgen.com/demo.tgz" -d '{ "region": "westeurope", "database": { "type": "MYSQL", "tier": "BASIC"}}' -H "Content-Type: application/json"  | tar -xzvf -
```

If you want to pass some parameters in GET:

```bash
curl "https://nubesgen.com/demo.tgz?region=westeurope&database=mysql" | tar -xzvf -
```

## Application name

The name of the application is in the name of the file that you download:

```bash
curl "https://nubesgen.com/my-wonderful-application.tgz" | tar -xzvf -
```

- The application name would be `my-wonderful-application` in this example.
- As a result, all Azure resources will use that name, following the [Recommended abbreviations for Azure resource types](https://aka.ms/nubesgen-recommended-abbreviations).
  For example, those resources will be created in a resource group named `rg-my-wonderful-application-01`.
- If you use the `.tgz` suffix, your application will be downloaded as a gzipped tar file, that you can uncompress with the `tar -xzvf my-wonderful-application.tgz` command.
- If you use the `.zip` suffix, your application will be downloaded as a zip file, that you can uncompress with the `unzip my-wonderful-application.zip` command.

If you use the default *demo* name for your application, in order to avoid name clashes inside Azure, NubesGen will generate a random string and append it
to your project name.

If you download:

```bash
curl "https://nubesgen.com/demo.tgz" | tar -xzvf -
```

Then your application name will be something similar to `demo-1234-5678-9012`.

## Complete parameters list

### Main parameters

| Name  | Description  | Values                                                                                    | POST example | GET example  |
|---|---|-------------------------------------------------------------------------------------------|---|---|
| runtime |  The language and framework used to run the application | DOCKER (default), DOCKER_SPRING, JAVA, JAVA_GRADLE, SPRING, SPRING_GRADLE, DOTNET, NODEJS | `https://nubesgen.com/demo.tgz -d '{ "runtime": "JAVA"' -H "Content-Type: application/json"` | `https://nubesgen.com/demo.tgz?runtime=java`  |
| application  | Type of application: Web app or serverless  | APP_SERVICE (default), FUNCTION, CONTAINER_APPS, SPRING_APPS                              | `https://nubesgen.com/demo.tgz -d '{ "application": { "type": "FUNCTION", "tier": "CONSUMPTION"}}' -H "Content-Type: application/json"` | `https://nubesgen.com/demo.tgz?application=function`  |
| region  |  Azure Region where the resource will be located | Run `az account list-locations`                                                           | `https://nubesgen.com/demo.tgz -d '{ "region": "westeurope"}' -H "Content-Type: application/json"` | `https://nubesgen.com/demo.tgz?region=westeurope`  |
| database  |  The database | NONE (default), SQL_SERVER, MYSQL, POSTGRESQL                                             | `https://nubesgen.com/demo.tgz -d '{ "database": { "type": "MYSQL", "tier": "BASIC"}}' -H "Content-Type: application/json"` | `https://nubesgen.com/demo.tgz?database=mysql`  |
| network  |  The network security | PUBLIC (default), VIRTUAL_NETWORK                                                         | `https://nubesgen.com/demo.tgz -d '{ "gitops": "true"}' -H "Content-Type: application/json"` | `https://nubesgen.com/demo.tgz?network=VIRTUAL_NETWORK`  |
| gitops  |  If [GitOps](/gitops/gitops-quick-start/) is enabled | FALSE (default), TRUE                                                                     | `https://nubesgen.com/demo.tgz -d '{ "gitops": "true"}' -H "Content-Type: application/json"` | `https://nubesgen.com/demo.tgz?gitops=true`  |

_In a GET request, parameters can be in uppercase or lowercase, for example `database=MYSQL`or `database=mysql`_

### Tiers

Application types and databases have different tiers.

- Each resource type has its own specific tiers.
- NubesGen provides the main available tiers for running development and production workloads.
- For each of those tiers, NubesGen configures the least expensive option possible. If you want to upgrade your resource, you'll need to modify the Terraform configuration (our recommended solution) or modify the resource using the Azure portal.
- If no tier is provided, NubesGen will use the least expensive one.

We provide the following tiers per resource type:

| Resource type  | Available tiers             | Example                                                             |
|----------------|-----------------------------|---------------------------------------------------------------------|
| APP_SERVICE    | FREE, BASIC, STANDARD       | `https://nubesgen.com/demo.tgz?application=app_service.standard`    |
| FUNCTION       | CONSUMPTION, PREMIUM        | `https://nubesgen.com/demo.tgz?application=function.premium`        |
| CONTAINER_APPS | CONSUMPTION                 | `https://nubesgen.com/demo.tgz?application=container_apps`          |
| SPRING_APPS    | BASIC, STANDARD             | `https://nubesgen.com/demo.tgz?application=spring_apps.standard`    |
| SQL_SERVER     | SERVERLESS, GENERAL_PURPOSE | `https://nubesgen.com/demo.tgz?database=sql_server.general_purpose` |
| MYSQL          | BASIC, GENERAL_PURPOSE      | `https://nubesgen.com/demo.tgz?database=mysql.general_purpose`      |
| POSTGRESQL     | BASIC, GENERAL_PURPOSE      | `https://nubesgen.com/demo.tgz?database=postgresql.general_purpose` |

### Add-ons

You can add "addons", which are specific technologies added to your stack:

| Name  | Description  | POST example | GET example  |
|---|---|---|---|
| APPLICATION_INSIGHTS  | Add support for Azure Application Insights  | `https://nubesgen.com/demo.tgz -d '{ "region": "westeurope", "addons": [{ "type": "APPLICATION_INSIGHTS", "tier": "BASIC"}]}' -H "Content-Type: application/json"` | `https://nubesgen.com/demo.tgz?addons=application_insights`  |
| KEY_VAULT  | Add support for Azure Key Vault  | `https://nubesgen.com/demo.tgz -d '{ "region": "westeurope", "addons": [{ "type": "KEY_VAULT", "tier": "STANDARD"}]}' -H "Content-Type: application/json"` | `https://nubesgen.com/demo.tgz?addons=key_vault`  |
| STORAGE_BLOB  | Add support for Azure Blob Storage  | `https://nubesgen.com/demo.tgz -d '{ "region": "westeurope", "addons": [{ "type": "STORAGE_BLOB", "tier": "BASIC"}]}' -H "Content-Type: application/json"` | `https://nubesgen.com/demo.tgz?addons=storage_blob`  |
| REDIS  | Add support for Azure Cache for Redis  | `https://nubesgen.com/demo.tgz -d '{ "region": "westeurope", "addons": [{ "type": "REDIS", "tier": "BASIC"}]}' -H "Content-Type: application/json"` | `https://nubesgen.com/demo.tgz?addons=redis`  |
| COSMOSDB_MONGODB  | Add support for Cosmos DB with the MongoDB API  | `https://nubesgen.com/demo.tgz -d '{ "region": "westeurope", "addons": [{ "type": "COSMOSDB_MONGODB", "tier": "BASIC"}]}' -H "Content-Type: application/json"` | `https://nubesgen.com/demo.tgz?addons=cosmosdb_mongodb`  |

_In a GET request, you can configure several addons by separating them with a comma, for example `addons=storage_blob,redis`_

Here is a complete example:

```bash
curl "https://nubesgen.com/demo.tgz" -d '{ "region": "westeurope", "runtime": "spring", "database": { "type": "MYSQL", "tier": "BASIC"}, "addons": [{ "type": "STORAGE_BLOB", "tier": "BASIC"}, { "type": "REDIS", "tier": "BASIC"}]}' -H "Content-Type: application/json"  | tar -xzvf -
```

```bash
curl "https://nubesgen.com/demo.tgz?region=westeurope&runtime=spring&database=MYSQL&addons=STORAGE_BLOB,REDIS"  | tar -xzvf -
```
