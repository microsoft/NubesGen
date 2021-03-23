[[ << Back to the main documentation page](README.md)]

# Running NubesGen from the command line

__We're using `http://localhost:8080` in the following examples, but you can replace it with our online instance at [https://nubesgen.azurewebsites.net/](https://nubesgen.azurewebsites.net/) if needed__

## Generating a project using cURL

To automate your workflow, you don't need to use a Web interface! Use cURL to directly download and use your NubesGen configuration.

To generate a default application:

```bash
curl "http://localhost:8080/nubesgen.tgz" | tar -xzvf -
```

or (as a .zip file):

```bash
curl "http://localhost:8080/nubesgen.zip" | jar xv
```

If you want to pass some parameters in POST:

```bash
curl "http://localhost:8080/myapplication.tgz" -d '{ "region": "westeurope", "database": { "type": "MYSQL", "tier": "BASIC"}}' -H "Content-Type: application/json"  | tar -xzvf -
```

If you want to pass some parameters in GET:

```bash
curl "http://localhost:8080/myapplication.tgz?region=westeurope&database=mysql"  | tar -xzvf -
```

## Complete parameters list

### Main parameters

| Name  | Description  | Values  | POST example | GET example  |
|---|---|---|---|---|
| runtime |  The language and framework used to run the application | DOCKER (default), DOCKER_SPRING, JAVA, JAVA_GRADLE, SPRING, SPRING_GRADLE, DOTNET | `http://localhost:8080/myapplication.tgz -d '{ "runtime": "JAVA"' -H "Content-Type: application/json"` | `http://localhost:8080/myapplication.tgz?runtime=java`  |
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
| APP_SERVICE | FREE, BASIC, STANDARD | `http://localhost:8080/myapplication.tgz?application=app_service.standard` |
| FUNCTION | CONSUMPTION, PREMIUM | `http://localhost:8080/myapplication.tgz?application=function.premium` |
| SQL_SERVER | SERVERLESS, GENERAL_PURPOSE | `http://localhost:8080/myapplication.tgz?database=sql_server.general_purpose` |
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

```bash
curl "http://localhost:8080/myapplication.tgz" -d '{ "region": "westeurope", "runtime": "spring", "database": { "type": "MYSQL", "tier": "BASIC"}, "addons": [{ "type": "STORAGE_BLOB", "tier": "BASIC"}, { "type": "REDIS", "tier": "BASIC"}]}' -H "Content-Type: application/json"  | tar -xzvf -
```

```bash
curl "http://localhost:8080/myapplication.tgz?region=westeurope&runtime=spring&database=MYSQL&addons=STORAGE_BLOB,REDIS"  | tar -xzvf -
```

[[ << Back to the main documentation page](README.md)]
