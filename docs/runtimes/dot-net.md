# Using .NET with NubesGen

This documentation is for running .NET applications with NubesGen, and there is another other options that might interest you:

- As .NET applications can be packaged with Docker, you can also run them as [Docker applications with NubesGen](docker/).

NubesGen supports creating Azure App Service instances and Azure Functions instances, depending on the type of .NET application that you which to deploy.

## Tutorial: running a .NET application with NubesGen

__Prerequisites:__

_Tip: You can go to [https://aka.ms/nubesgen-azure-shell](https://aka.ms/nubesgen-azure-shell) to have those prerequisites installed, and run the script from a Web browser._
- [Bash](https://fr.wikipedia.org/wiki/Bourne-Again_shell), which is installed by default on most Linux distributions and on Mac OS X. If you're using Windows, one solution is to use [WSL](https://aka.ms/nubesgen-install-wsl).
- [Azure CLI](https://aka.ms/nubesgen-install-az-cli). To login, use `az login`.
- (optional) [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`.

__Steps:__
1. Create a sample .NET Web application using [the .NET CLI](https://aka.ms/nubesgen-install-dotnet-cli).
   We'll follow the beginning of the [official "Get started with ASP.NET Core" tutorial](https://aka.ms/nubesgen-dotnet-getting-started):
   ```bash
   dotnet new webapp -o dotnet-sample-app -f net6.0
   ```
2. Create a project on GitHub called `dotnet-sample-app`, and push the generated project to that repository. Change `<your-github-account>` by the name of your GitHub account:
   ```bash
   cd dotnet-sample-app
   git init
   git add .
   git commit -m "first commit"
   git remote add origin https://github.com/<your-github-account>/dotnet-sample-app.git
   git branch -M main
   git push -u origin main
   ```
3. In the cloned project (`cd dotnet-sample-app`), set up GitOps with NubesGen by running the NubesGen CLI ([more information here](/gitops/gitops-quick-start/)):
   ```bash
    ./nubesgen-cli-linux gitops
    ```
4. Use the command-line with NubesGen ([more information here](/reference/rest-api/)) to generate a NubesGen configuration:
   ```bash
   curl "https://nubesgen.com/demo.tgz?runtime=dotnet&application=app_service.standard&gitops=true" | tar -xzvf -
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
As it is a simple application, it should print by default `Hello, world`.
9. Once you have finished, you should clean up your resources:
   1. Delete the resource group that was created by NubesGen to host your resources, which is named `rg-demo-XXXX-XXXX-XXXX-XXXX-001`.
   2. Delete the storage account used to store your Terraform state, in the `rg-terraform-001` resource group.

## Which Azure resources are created

If you deploy your .NET application to an Azure App Service instance, NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of App Service instance you will use.
- An [Azure App Service instance](https://aka.ms/nubesgen-app-service), configured to run .NET code natively.

If you deploy your .NET application to an Azure Function, NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of Azure Functions instance you will use.
- An [Azure Functions instance](https://aka.ms/nubesgen-functions), configured to run .NET code natively.
- An [Azure Storage Account](https://aka.ms/nubesgen-storage), to store your .NET application.

## .NET version support

NubesGen supports .NET 6.0 by default, as it's the current long term support version. If you want to use .NET 5, you will 
need to configure it in two places:

- In the generated `terraform/modules/app-service/main.tf`, you need to modify `dotnet_version = "6.0"` to be
  `dotnet_version = "5.0""`
- If you selected the [GitOps option](/gitops/gitops-overview), at the beginning of the generated `.github/workflows/gitops.yml` file,
  there is a specific `DOTNET_VERSION: '6.0'` environment variable that should be modified to `DOTNET_VERSION: '5.0'`

## Configuration options

In the generated `terraform/modules/app-service/main.tf` file, NubesGen will configure some environment variables
for your application.

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
