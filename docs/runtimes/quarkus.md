# Using Quarkus with NubesGen

This documentation is for running Quarkus applications with NubesGen. There are two other options that might interest you:

- You can also use [Java with NubesGen](java.md), which is similar to Quarkus, but does not use the specific Quarkus application properties.
- As Quarkus applications can be packaged with Docker, you can also run them as [Docker applications with NubesGen](docker.md).

NubesGen supports deploying Quarkus applications both using the JVM and using the native image compilation (GraalVM).

[![Video tutorial](https://img.youtube.com/vi/5jBR75CGsNs/0.jpg)](https://www.youtube.com/watch?v=5jBR75CGsNs)

_YouTube video showing how to deploy Quarkus with NubesGen, both in JVM and native mode_

## Tutorial 1: running a Quarkus application on the JVM

__Prerequisites:__

_Tip: You can go to [https://aka.ms/nubesgen-azure-shell](https://aka.ms/nubesgen-azure-shell) to have those prerequisites installed, and run the script from a Web browser._
- [Bash](https://fr.wikipedia.org/wiki/Bourne-Again_shell), which is installed by default on most Linux distributions and on Mac OS X. If you're using Windows, one solution is to use [WSL](https://aka.ms/nubesgen-install-wsl).
- [Azure CLI](https://aka.ms/nubesgen-install-az-cli). To login, use `az login`.
- (optional) [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`.

__Steps:__
1. Create a sample Quarkus Web application using [https://code.quarkus.io/](https://code.quarkus.io/).
   ```bash
   curl https://code.quarkus.io/d?e=resteasy-jackson&e=resteasy&cn=code.quarkus.io | tar -xzvf -
   ```
2. Create a project on GitHub called `code-with-quarkus`, and push the generated project to that repository. Change `<your-github-account>` by the name of your GitHub account:
   ```bash
   cd code-with-quarkus
   git init
   git add .
   git commit -m "first commit"
   git remote add origin https://github.com/<your-github-account>/code-with-quarkus.git
   git branch -M main
   git push -u origin main
   ```
3. In the cloned project (`cd code-with-quarkus`), set up GitOps with NubesGen by running the NubesGen CLI ([more information here](/gitops/gitops-quick-start.md)):
   ```bash
    ./nubesgen-cli-linux gitops
    ```
4. Use the command-line with NubesGen ([more information here](/getting-started/cli.md)) to generate a NubesGen configuration:
   ```bash
   curl "https://nubesgen.com/demo.tgz?runtime=quarkus&application=app_service.standard&gitops=true" | tar -xzvf -
   ```
5. Create a new branch called `env-dev`, and push your code:
   ```bash
   git checkout -b env-dev
   git add .
   git commit -m 'Configure GitOps with NubesGen'
   git push --set-upstream origin env-dev
   ```
6. Go to your GitHub project, and check that the GitHub Action is running.
7. You can go to the [Azure Portal](https://aka.ms/nubesgen-portal) to check the created resources.
8. The application should be deployed on your App Service instance. Its URL should be in the form `https://app-demo-XXXX-XXXX-XXXX-XXXX-dev-001.azurewebsites.net/`, and you can also find it in the GitHub Action workflow (Job: "manage-infrastructure", step "Apply Terraform"), or in the Azure portal.
As it is an empty application, you should get the standard Quarkus welcome page.
9. Once you have finished, you should clean up your resources:
   1. Delete the resource group that was created by NubesGen to host your resources, which is named `rg-demo-XXXX-XXXX-XXXX-XXXX-001`.
   2. Delete the storage account used to store your Terraform state, in the `rg-terraform-001` resource group.

## Tutorial 2: running a native Quarkus application (using GraalVM)

The only difference with the previous tutorial is that we use a different NubesGen parameter, `quarkus_native`, instead of `quarkus`.

__Prerequisites:__

_Tip: You can go to [https://aka.ms/nubesgen-azure-shell](https://aka.ms/nubesgen-azure-shell) to have those prerequisites installed, and run the script from a Web browser._
- [Bash](https://fr.wikipedia.org/wiki/Bourne-Again_shell), which is installed by default on most Linux distributions and on Mac OS X. If you're using Windows, one solution is to use [WSL](https://aka.ms/nubesgen-install-wsl).
- [Azure CLI](https://aka.ms/nubesgen-install-az-cli). To login, use `az login`.
- (optional) [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`.

__Steps:__
1. Create a sample Quarkus Web application using [https://code.quarkus.io/](https://code.quarkus.io/).
   ```bash
   curl https://code.quarkus.io/d?e=resteasy-jackson&e=resteasy&cn=code.quarkus.io | tar -xzvf -
   ```
2. Create a project on GitHub called `code-with-quarkus`, and push the generated project to that repository. Change `<your-github-account>` by the name of your GitHub account:
   ```bash
   cd code-with-quarkus
   git init
   git add .
   git commit -m "first commit"
   git remote add origin https://github.com/<your-github-account>/code-with-quarkus.git
   git branch -M main
   git push -u origin main
   ```
3. In the cloned project (`cd code-with-quarkus`), set up GitOps with NubesGen by running the NubesGen CLI ([more information here](../gitops-quick-start.md)):
   ```bash
    ./nubesgen-cli-linux gitops
    ```
4. Use the command-line with NubesGen ([more information here](../command-line.md)) to generate a NubesGen configuration. If you followed the first tutorial, please note the `quarkus_native` parameter.
   ```bash
   curl "https://nubesgen.com/demo.tgz?runtime=quarkus_native&application=app_service.standard&gitops=true" | tar -xzvf -
   ```
5. Create a new branch called `env-dev`, and push your code:
   ```bash
   git checkout -b env-dev
   git add .
   git commit -m 'Configure GitOps with NubesGen'
   git push --set-upstream origin env-dev
   ```
6. Go to your GitHub project, and check that the GitHub Action is running.
7. You can go to the [Azure Portal](https://aka.ms/nubesgen-portal) to check the created resources.
8. The application should be deployed on your App Service instance. Its URL should be in the form `https://app-demo-XXXX-XXXX-XXXX-XXXX-dev-001.azurewebsites.net/`, and you can also find it in the GitHub Action workflow (Job: "manage-infrastructure", step "Apply Terraform"), or in the Azure portal.
As it is an empty application, you should get the standard Quarkus welcome page.
9. Once you have finished, you should clean up your resources:
   1. Delete the resource group that was created by NubesGen to host your resources, which is named `rg-demo-XXXX-XXXX-XXXX-XXXX-001`.
   2. Delete the storage account used to store your Terraform state, in the `rg-terraform-001` resource group.

## Which Azure resources will be created

If you deploy your Quarkus application to an Azure App Service instance, NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of App Service instance you will use.
- An [Azure App Service instance](https://aka.ms/nubesgen-app-service), configured to run Java code natively.

If you use Quarkus on the JVM, the Azure App Service instance will be configured with Java. And if you use Quarkus in native mode, the Azure App Service instance will
be configured with Docker.

## Configuration options

In the generated `terraform/modules/app-service/main.tf` file, NubesGen will configure some environment variables
for your application. Those are standard Spring Boot
properties, so your Spring Boot application should be automatically configured 
(for example: your database connection should be working out-of-the-box).

- `QUARKUS_DATASOURCE_JDBC_URL`: the JDBC URL to your database
- `QUARKUS_DATASOURCE_USERNAME`: the database user name
- `QUARKUS_DATASOURCE_PASSWORD`: the database password
- `QUARKUS_REDIS_HOSTS`: the Redis host configuration, including the password (in the form: "redis://$PASSWORD@$HOST:6380")
- `QUARKUS_MONGODB_DATABASE`: the MongoDB database name
- `QUARKUS_MONGODB_HOSTS`: the MongoDB URI
- `AZURE_STORAGE_ACCOUNT_NAME`: the storage account name
- `AZURE_STORAGE_ACCOUNT_KEY`: the storage account key
- `AZURE_STORAGE_BLOB_ENDPOINT`: the blob storage endpoint
