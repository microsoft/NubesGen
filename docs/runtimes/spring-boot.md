# Using Spring Boot with NubesGen

This documentation is for running Spring Boot applications with NubesGen. There are two other options that might interest you:

- You can also use [Java with NubesGen](java/), which is similar to Spring Boot, but does not use the specific Spring Boot application properties.
- As Spring Boot applications can be packaged with Docker, you can also run them as [Docker applications with NubesGen](docker/).

NubesGen supports creating Azure App Service instances and Azure Functions instances, depending on the type of Spring Boot application that you wish to deploy.

## Tutorial 1: running a full stack Spring Boot application generated with JHipster

We're going to deploy [https://github.com/jhipster/jhipster-sample-app](https://github.com/jhipster/jhipster-sample-app), which is the default [JHipster](https://www.jhipster.tech/) application.

It uses a PostgreSQL database, so creating the database and connecting to it will also be managed by NubesGen.

We'll use NubesGen's [GitOps support](/gitops/gitops-overview) to automatically build and deploy the application.

__Prerequisites:__

_Tip: You can go to [https://aka.ms/nubesgen-azure-shell](https://aka.ms/nubesgen-azure-shell) to have those prerequisites installed, and run the script from a Web browser._
- [Bash](https://fr.wikipedia.org/wiki/Bourne-Again_shell), which is installed by default on most Linux distributions and on Mac OS X. If you're using Windows, one solution is to use [WSL](https://aka.ms/nubesgen-install-wsl).
- [Azure CLI](https://aka.ms/nubesgen-install-az-cli). To login, use `az login`.
- (optional) [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`.

__Steps:__
1. Fork the project on your GitHub account.
2. Clone the fork on your computer. Change `<your-github-account>` to the name of your GitHub account:
   ```bash
   git clone https://github.com/<your-github-account>/jhipster-sample-app.git
   ``` 
3. In the cloned project (`cd java-sample-app`), set up GitOps with NubesGen by running the NubesGen CLI ([more information here](/gitops/gitops-quick-start/)):
   ```bash
    ./nubesgen-cli-linux gitops
    ```
4. Use the command-line with NubesGen ([more information here](/reference/rest-api/)) to generate a NubesGen configuration:
   ```bash
   curl "https://nubesgen.com/demo.tgz?runtime=spring&application=app_service.standard&database=postgresql&gitops=true" | tar -xzvf -
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
8. The application should be deployed on your App Service instance. Its URL should be in the form `https://app-demo-XXXX-XXXX-XXXX-XXXX-dev-001.azurewebsites.net/`, and you can also find it in the GitHub Action workflow (Job: "display-information", step "Display Azure infrastructure information"), or in the Azure portal.
9. Once you have finished, you should clean up your resources:
   1. Delete the resource group that was created by NubesGen to host your resources, which is named `rg-XXXX-XXXX-XXXX-XXXX-001`.
   2. Delete the storage account used to store your Terraform state, in the `rg-terraform-001` resource group.

## Tutorial 2: running a Spring Intializr project with NubesGen

This tutorial is similar to the [Java on NubesGen](java/) tutorial, as this simple Spring Boot application is also a Java application.

__Prerequisites:__

_Tip: You can go to [https://aka.ms/nubesgen-azure-shell](https://aka.ms/nubesgen-azure-shell) to have those prerequisites installed, and run the script from a Web browser._
- [Bash](https://fr.wikipedia.org/wiki/Bourne-Again_shell), which is installed by default on most Linux distributions and on Mac OS X. If you're using Windows, one solution is to use [WSL](https://aka.ms/nubesgen-install-wsl).
- [Azure CLI](https://aka.ms/nubesgen-install-az-cli). To login, use `az login`.
- (optional) [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`.

__Steps:__
1. Create a sample Java Web application using [https://start.spring.io/](https://start.spring.io/).
   ```bash
   curl https://start.spring.io/starter.tgz?type=maven-project&language=java&bootVersion=3.1.3&baseDir=java-sample-app&groupId=com.example&artifactId=java-sample-app&name=java-sample-app&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.java-sample-app&packaging=jar&javaVersion=17&dependencies=web | tar -xzvf -
   ```
2. Create a project on GitHub called `java-sample-app`, and push the generated project to that repository. Change `<your-github-account>` by the name of your GitHub account:
   ```bash
   cd java-sample-app
   git init
   git add .
   git commit -m "first commit"
   git remote add origin https://github.com/<your-github-account>/java-sample-app.git
   git branch -M main
   git push -u origin main
   ```
3. In the cloned project (`cd java-sample-app`), set up GitOps with NubesGen by running the NubesGen CLI ([more information here](/gitops/gitops-quick-start/)):
   ```bash
    ./nubesgen-cli-linux gitops
    ```
4. Use the command-line with NubesGen ([more information here](/reference/rest-api/)) to generate a NubesGen configuration:
   ```bash
   curl "https://nubesgen.com/demo.tgz?runtime=spring&application=app_service.standard&gitops=true" | tar -xzvf -
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
8. The application should be deployed on your App Service instance. Its URL should be in the form `https://app-demo-XXXX-XXXX-XXXX-XXXX-dev-001.azurewebsites.net/`, and you can also find it in the GitHub Action workflow (Job: "display-information", step "Display Azure infrastructure information"), or in the Azure portal.
As it is an empty application, you should get 404 page called `Whitelabel Error Page`.
9. Once you have finished, you should clean up your resources:
   1. Delete the resource group that was created by NubesGen to host your resources, which is named `rg-demo-XXXX-XXXX-XXXX-XXXX-001`.
   2. Delete the storage account used to store your Terraform state, in the `rg-terraform-001` resource group.

## Which Azure resources will be created

If you deploy your Spring Boot application to an Azure App Service instance, NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of App Service instance you will use.
- An [Azure App Service instance](https://aka.ms/nubesgen-app-service), configured to run Java code natively.

If you deploy your Spring Boot application to an Azure Function, NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of Azure Functions instance you will use.
- An [Azure Functions instance](https://aka.ms/nubesgen-functions), configured to run Java code natively.
- An [Azure Storage Account](https://aka.ms/nubesgen-storage), to store your Java application.

## Maven vs Gradle

NubesGen supports both Maven and Gradle, so you can use the build system you prefer.

## Configuration options

In the generated `terraform/modules/app-service/main.tf` file, NubesGen will configure some environment variables
for your application. Those are standard Spring Boot
properties, so your Spring Boot application should be automatically configured 
(for example: your database connection should be working out-of-the-box).

- `SPRING_DATASOURCE_URL`: the JDBC URL to your database
- `SPRING_DATASOURCE_USERNAME`: the database user name
- `SPRING_DATASOURCE_PASSWORD`: the database password
- `SPRING_REDIS_HOST`: the Redis host name
- `SPRING_REDIS_PASSWORD`: the Redis password
- `SPRING_REDIS_PORT`: the Redis port (by default `6380`)
- `SPRING_REDIS_SSL`: if Redis uses SSL (by default `true`)
- `AZURE_STORAGE_ACCOUNT_NAME`: the storage account name
- `AZURE_STORAGE_ACCOUNT_KEY`: the storage account key
- `AZURE_STORAGE_BLOB_ENDPOINT`: the blob storage endpoint
- `SPRING_DATA_MONGODB_DATABASE`: the MongoDB database name
- `SPRING_DATA_MONGODB_URI`: the MongoDB database URL
