[[ << Back to the main documentation page](../README.md)]

# Using Java with NubesGen

This documentation is for running Java applications with NubesGen, and there are two other options that might interest you:

- You can also use [Spring Boot with NubesGen](spring-boot.md), which is similar to Java, but using the specific Spring Boot application properties.
- As Java applications can be packaged with Docker, you can also run them as [Docker applications with NubesGen](docker.md).

NubesGen supports creating Azure App Service instances and Azure Functions instances, depending on the type of Java application that you which to deploy.

## Which Azure resources will be created

If you deploy your Java application to an Azure App Service instance, NubesGen will generate:

- An [Azure App Service plan](https://docs.microsoft.com/azure/app-service/overview-hosting-plans) to define the type of App Service instance you will use.
- An [Azure App Service instance](https://azure.microsoft.com/services/app-service/), configured to run Java code natively.

If you deploy your Java application to an Azure Function, NubesGen will generate:

- An [Azure App Service plan](https://docs.microsoft.com/azure/app-service/overview-hosting-plans) to define the type of Azure Functions instance you will use.
- An [Azure Functions instance](https://azure.microsoft.com/services/functions/), configured to run Java code natively.
- An [Azure Storage Account](https://azure.microsoft.com/services/storage/), to store your Java application.

## Maven vs Gradle

NubesGen supports both Maven and Gradle, so you can use the build system you prefer.

## Important configuration options

NubesGen will generate some environment variables for your application, depending on the database and add-ons that you have selected.

- `DATABASE_URL`: the JDBC URL to your database
- `DATABASE_USERNAME`: the database user name
- `DATABASE_PASSWORD`: the database password
- `REDIS_HOST`: the Redis host name
- `REDIS_PASSWORD`: the Redis password
- `REDIS_PORT`: the Redis port (by default `6380`)
- `AZURE_STORAGE_ACCOUNT_NAME`: the storage account name
- `AZURE_STORAGE_ACCOUNT_KEY`: the storage account key
- `AZURE_STORAGE_BLOB_ENDPOINT`: the blob storage endpoint
- `MONGODB_DATABASE`: the MongoDB database name
- `MONGODB_URI`: the MongoDB database URL
  
## Tutorial: running a Java application with NubesGen

1. Create a sample Java Web application using [https://start.spring.io/](https://start.spring.io/).
   ```bash
   curl https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=2.4.4.RELEASE&baseDir=java-sample-app&groupId=com.example&artifactId=java-sample-app&name=java-sample-app&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.java-sample-app&packaging=jar&javaVersion=11&dependencies=web | jar xv
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
3. In the cloned project, set up [GitOps with NubesGen by following this tutorial](../gitops-quick-start.md) (you've already done step 1 above).
4. Use [the command-line with NubesGen](../command-line.md) to generate a NubesGen configuration. Modify the name of the file (`<your-unique-name>.tgz`) to have a unique name you can use in your Azure subscription.
   ```bash
   curl "https://nubesgen.com/<your-unique-name>.zip?runtime=java&application=app_service.standard&gitops=true" | jar xv
   ```
5. Create a new branch called `env-dev`, and push your code:
   ```bash
   git checkout -b env-dev
   git add .
   git commit -m 'Configure GitOps with NubesGen'
   git push --set-upstream origin env-dev
   ```
6. Go to your GitHub project, and check that the GitHub Action is running.
7. You can go to the [Azure Portal](https://portal.azure.com) to check the created resources.
8. The application should be deployed on your App Service instance. Its URL should be in the form `https://app-<your-unique-name>-dev-001.azurewebsites.net/`, and you can also find it in the GitHub Action workflow (Job: "manage-infrastructure", step "Apply Terraform"), or in the Azure portal.
As it is an empty application, you should get 404 page called `Whitelabel Error Page`.
9. Once you have finished, you should clean up your resources:
   1. Delete the resource group that was created by NubesGen to host your resources, which is named `rg-<your-unique-name>-001`.
   2. Delete the storage account used to store your Terraform state, in the `rg-terraform-001` resource group, named ``.

[[ << Back to the main documentation page](../README.md)]
