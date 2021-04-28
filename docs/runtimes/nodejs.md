[[ << Using .NET with NubesGen ](dot-net.md) | [ Main documentation page ](../README.md) |[ Frequently asked questions >> ](../frequently-asked-questions.md)]

# Using Node.js with NubesGen

This documentation is for running Node.js applications with NubesGen, and there is another options that might interest you:

- As Node.js applications can be packaged with Docker, you can also run them as [Docker applications with NubesGen](docker.md).

NubesGen supports creating Azure App Service instances and Azure Functions instances, depending on the type of Node.js application that you which to deploy.

## Tutorial: running a Node.js application with NubesGen

__Prerequisites:__

_Tip: You can go to [https://shell.azure.com/](https://shell.azure.com/) to have those prerequisites installed, and run the script from a Web browser._
- [Bash](https://fr.wikipedia.org/wiki/Bourne-Again_shell), which is installed by default on most Linux distributions and on Mac OS X. If you're using Windows, one solution is to use [WSL](https://aka.ms/nubesgen-install-wsl).
- [Azure CLI](https://aka.ms/nubesgen-install-az-cli). To login, use `az login`.
- (optional) [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`.

__Steps:__
1. Create a sample Node.js Web application using [NestJs](https://nestjs.com/).
   We'll follow the beginning of the [NestJs "first steps" guide](https://docs.nestjs.com/first-steps):
   ```bash
   npm i -g @nestjs/cli
   nest new nodejs-sample-app
   cd nodejs-sample-app
   ```
   Select the default package manager (`npm`).
   In the project, open the `src/main.ts` file, and use the `$PORT` environment variable to bind your application
   to the correct port: 
   ```javascript
   await app.listen(process.env.PORT || 3000);
   ```
   Open `package.json` file, and add the `files` entry to tell NPM how to package your app:
   ```json
   "files": [
      "dist"
   ]
   ```
2. Create a project on GitHub called `nodejs-sample-app`, and push the generated project to that repository. Change `<your-github-account>` by the name of your GitHub account:
   ```bash
   git init
   git add .
   git commit -m "first commit"
   git remote add origin https://github.com/<your-github-account>/nodejs-sample-app.git
   git branch -M main
   git push -u origin main
   ```
3. In the cloned project (`cd nodejs-sample-app`), set up GitOps with NubesGen by running the following script ([more information here](../gitops-quick-start.md)):
   ```bash
    bash -c "$(curl -fsSL https://nubesgen.com/gitops/setup.sh)"
    ```
4. Use the command-line with NubesGen([more information here](../command-line.md)) to generate a NubesGen configuration:
   ```bash
   curl "https://nubesgen.com/demo.tgz?runtime=nodejs&application=app_service.standard&gitops=true" | tar -xzvf -
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
8. The application should be deployed on your App Service instance. Its URL should be in the form `https://app-demo-XXXX-XXXX-XXXX-XXXX-dev-001.azurewebsites.net/`,
   and you can also find it in the GitHub Action workflow (Job: "manage-infrastructure", step "Apply Terraform"), or in the Azure portal.
   As it is a simple application, it should print by default `Hello, world`.
9. Once you have finished, you should clean up your resources:
  1. Delete the resource group that was created by NubesGen to host your resources, which is named `rg-demo-XXXX-XXXX-XXXX-XXXX-001`.
  2. Delete the storage account used to store your Terraform state, in the `rg-terraform-001` resource group.

## Which Azure resources are created

If you deploy your Node.js application to an Azure App Service instance, NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of App Service instance you will use.
- An [Azure App Service instance](https://aka.ms/nubesgen-app-service), configured to run Node.js code natively.

If you deploy your Node.js application to an Azure Function, NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of Azure Functions instance you will use.
- An [Azure Functions instance](https://azure.microsoft.com/services/functions/), configured to run Node.js code natively.
- An [Azure Storage Account](https://azure.microsoft.com/services/storage/), to store your Node.js application.

## Configuration options

In the generated `terraform/modules/app-service/main.tf` file, NubesGen will configure some variables
for your application.

### The `app_command_line` parameter

In the `site_config` block, NubesGen generates the following configuration:

```
app_command_line = "npm run start:prod"
```

This command is the one used to run the Node.js application. By default, this command
is `npm run start:prod`, which is the default with [NestJS](https://nestjs.com/), but this
should be specifically configured depending on the framework used.

### The `PORT` environment variable

Azure App Service automatically assigns the `PORT` variable, so your Node.js application
can listen to the correct port.

You need to configure it in your application:

```javascript
app.listen(process.env.PORT || 3000);
```

### Other options

NubesGen will configure some environment variables for your application.

- `DATABASE_URL`: the URL to your database
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

[[ << Using .NET with NubesGen ](dot-net.md) | [ Main documentation page ](../README.md) |[ Frequently asked questions >> ](../frequently-asked-questions.md)]
