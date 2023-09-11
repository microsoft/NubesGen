# Using Python with NubesGen

This documentation is for running Python applications with NubesGen, and there is another option that might interest you:

- As Python applications can be packaged with Docker, you can also run them as [Docker applications with NubesGen](docker/).

NubesGen supports creating Azure App Service instances and Azure Functions instances, depending on the type of Python application that you wish to deploy.

## Tutorial: running a Python application with NubesGen

__Prerequisites:__

_Tip: You can go to [https://aka.ms/nubesgen-azure-shell](https://aka.ms/nubesgen-azure-shell) to have those prerequisites installed, and run the script from a Web browser._
- [Bash](https://fr.wikipedia.org/wiki/Bourne-Again_shell), which is installed by default on most Linux distributions and on Mac OS X. If you're using Windows, one solution is to use [WSL](https://aka.ms/nubesgen-install-wsl).
- [Azure CLI](https://aka.ms/nubesgen-install-az-cli). To login, use `az login`.
- (optional) [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`.

__Steps:__
1. Create a sample Python Web application using [Flask](https://flask.palletsprojects.com/).
   We'll follow the beginning of the [Flask "a Minimal Application" guide](https://flask.palletsprojects.com/quickstart/#a-minimal-application):
   Create a new file called `app.py` in the root of your project.
   ```python
   from flask import Flask

   app = Flask(__name__)

   @app.route("/")
   def hello_world():
    return "<p>Hello, World!</p>"
   ```
   To manage the project's dependencies, create a file called `requirements.txt` in the root of your project.
   ```text
   Flask==2.3.3
   ```
2. Create a project on GitHub called `python-sample-app`, and push the generated project to that repository. Change `<your-github-account>` by the name of your GitHub account:
   ```bash
   git init
   git add .
   git commit -m "first commit"
   git remote add origin https://github.com/<your-github-account>/python-sample-app.git
   git branch -M main
   git push -u origin main
   ```
3. In the cloned project (`cd python-sample-app`), set up GitOps with NubesGen by running the NubesGen CLI ([more information here](/gitops/gitops-quick-start/)):
   ```bash
    ./nubesgen-cli-linux gitops
    ```
4. Use the command-line with NubesGen ([more information here](/reference/rest-api/)) to generate a NubesGen configuration:
   ```bash
   curl "https://nubesgen.com/demo.tgz?runtime=python&application=app_service.standard&gitops=true" | tar -xzvf -
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
8. The application should be deployed on your App Service instance. Its URL should be in the form `https://app-demo-XXXX-XXXX-XXXX-XXXX-dev-001.azurewebsites.net/`,
   and you can also find it in the GitHub Action workflow (Job: "display-information", step "Display Azure infrastructure information"), or in the Azure portal.
   As it is a simple application, it should print by default `Hello, World!`.
9. Once you have finished, you should clean up your resources:
  1. Delete the resource group that was created by NubesGen to host your resources, which is named `rg-demo-XXXX-XXXX-XXXX-XXXX-001`.
  2. Delete the storage account used to store your Terraform state, in the `rg-terraform-001` resource group.

## Which Azure resources are created

If you deploy your Python application to an Azure App Service instance, NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of App Service instance you will use.
- An [Azure App Service instance](https://aka.ms/nubesgen-app-service), configured to run Python code natively.

If you deploy your Python application to an Azure Function, NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of Azure Functions instance you will use.
- An [Azure Functions instance](https://aka.ms/nubesgen-functions), configured to run Python code natively.
- An [Azure Storage Account](https://aka.ms/nubesgen-storage), to store your Python application.

## Configuration options

In the generated `terraform/modules/app-service/main.tf` file, NubesGen will configure some variables
for your application.

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
