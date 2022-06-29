# Using Micronaut with NubesGen

This documentation is for running Micronaut applications with NubesGen. There are two other options that might interest you:

- You can also use [Java with NubesGen](java/), which is similar to Micronaut, but does not use the specific Micronaut application properties.
- As Micronaut applications can be packaged with Docker, you can also run them as [Docker applications with NubesGen](docker/).

NubesGen supports creating Azure App Service instances and Azure Functions instances, depending on the type of Micronaut application that you wish to deploy.

## Tutorial: running a Micronaut Launch project with NubesGen

This tutorial is similar to the [Java on NubesGen](java/) tutorial, as this simple Micronaut application is also a Java application.

__Prerequisites:__

_Tip: You can go to [https://aka.ms/nubesgen-azure-shell](https://aka.ms/nubesgen-azure-shell) to have those prerequisites installed, and run the script from a Web browser._
- [Bash](https://fr.wikipedia.org/wiki/Bourne-Again_shell), which is installed by default on most Linux distributions and on Mac OS X. If you're using Windows, one solution is to use [WSL](https://aka.ms/nubesgen-install-wsl).
- [Azure CLI](https://aka.ms/nubesgen-install-az-cli). To login, use `az login`.
- (optional) [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`.

__Steps:__
1. Create a sample Java Web application using [https://launch.micronaut.io/](https://launch.micronaut.io/).
   ```bash
   curl --location --request GET 'https://launch.micronaut.io/create/default/com.example.micronaut-sample-app?lang=JAVA&build=GRADLE&test=JUNIT&javaVersion=JDK_11' | tar -xzvf -
   ```
2. Create a project on GitHub called `micronaut-sample-app`, and push the generated project to that repository. Change `<your-github-account>` by the name of your GitHub account:
   ```bash
   cd micronaut-sample-app
   git init
   git add .
   git commit -m "first commit"
   git remote add origin https://github.com/<your-github-account>/micronaut-sample-app.git
   git branch -M main
   git push -u origin main
   ```
3. In the cloned project (`cd micronaut-sample-app`), set up GitOps with NubesGen by running the NubesGen CLI ([more information here](/gitops/gitops-quick-start/)):
   ```bash
    ./nubesgen-cli-linux gitops
    ```
4. Use the command-line with NubesGen ([more information here](/reference/rest-api/)) to generate a NubesGen configuration:
   ```bash
   curl "https://nubesgen.com/demo.tgz?runtime=micronaut_gradle&application=app_service.standard&gitops=true" | tar -xzvf -
   ```
5. Push the generated terraform and GitHub action to the `main` branch of the repository:
   ```bash
   cd micronaut-sample-app
   git add .
   git commit -m "Configure GitOps with NubesGen"
   git push -u origin main
   ```
6. Create a new branch called `env-dev`, and push it to trigger a build:
   ```bash
   git checkout -b env-dev
   git push --set-upstream origin env-dev
   ```
7. Go to your GitHub project, and check that the GitHub Action is running.
8. You can go to the [Azure Portal](https://aka.ms/nubesgen-portal) to check the created resources.
9. The application should be deployed on your App Service instance. Its URL should be in the form `https://app-demo-XXXX-XXXX-XXXX-XXXX-dev-001.azurewebsites.net/`, and you can also find it in the GitHub Action workflow (Job: "display-information", step "Display Azure infrastructure information"), or in the Azure portal.
As it is an empty application, you should get a 404 page with JSON describing the request.
10. Once you have finished, you should clean up your resources:
    1. Delete the resource group that was created by NubesGen to host your resources, which is named `rg-demo-XXXX-XXXX-XXXX-XXXX-001`.
    2. Delete the storage account used to store your Terraform state, in the `rg-terraform-001` resource group.

## Which Azure resources will be created

If you deploy your Micronaut application to an Azure App Service instance, NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of App Service instance you will use.
- An [Azure App Service instance](https://aka.ms/nubesgen-app-service), configured to run Java code natively.

If you deploy your Micronaut application to an Azure Function, NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of Azure Functions instance you will use.
- An [Azure Functions instance](https://aka.ms/nubesgen-functions), configured to run Java code natively.
- An [Azure Storage Account](https://aka.ms/nubesgen-storage), to store your Java application.

## Maven vs Gradle

NubesGen supports both Maven and Gradle, so you can use the build system you prefer.
For Maven, change `runtime=micronaut_gradle` to `runtime=micronaut` in Step 5 above.

## Configuration options

In the generated `terraform/modules/app-service/main.tf` file, NubesGen will configure some environment variables for your application.
Those are standard Micronaut properties, so your Micronaut application should be automatically configured (for example: your database connection should be working out-of-the-box).

- `DATASOURCES_DEFAULT_URL`: the JDBC URL to your database
- `DATASOURCES_DEFAULT_USERNAME`: the database user name
- `DATASOURCES_DEFAULT_PASSWORD`: the database password
- `REDIS_HOST`: the Redis host name
- `REDIS_PASSWORD`: the Redis password
- `REDIS_PORT`: the Redis port (by default `6380`)
- `REDIS_SSL`: if Redis uses SSL (by default `true`)
- `MONGODB_URI`: the MongoDB database URL
