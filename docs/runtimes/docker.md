# Using Docker with NubesGen

Docker is the default option with NubesGen, and allows to run any kind of application supporting Docker.

It comes with two options: "Docker with a Dockerfile" and "Docker with Spring Boot".

## Tutorial: running a Docker image with NubesGen

We're going to deploy [https://github.com/jdubois/golang-sample-app](https://github.com/jdubois/golang-sample-app), which is a sample application written in Go.
We'll use NubesGen's [GitOps support](/gitops/gitops-overview) to automatically build and deploy the application.

__Prerequisites:__

_Tip: You can go to [https://aka.ms/nubesgen-azure-shell](https://aka.ms/nubesgen-azure-shell) to have those prerequisites installed, and run the script from a Web browser._
- [Bash](https://fr.wikipedia.org/wiki/Bourne-Again_shell), which is installed by default on most Linux distributions and on Mac OS X. If you're using Windows, one solution is to use [WSL](https://aka.ms/nubesgen-install-wsl).
- [Azure CLI](https://aka.ms/nubesgen-install-az-cli). To login, use `az login`.
- (optional) [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`.

__Steps:__
1. Fork the project on your GitHub account.
2. Clone the fork on your computer. Change `<your-github-account>` by the name of your GitHub account:
   ``` bash
   git clone https://github.com/<your-github-account>/golang-sample-app.git
   ``` 
3. In the cloned project (`cd golang-sample-app`), set up GitOps with NubesGen by using the NubesGen CLI ([more information here](/gitops/gitops-quick-start/)):
   ``` bash
    ./nubesgen-cli-linux gitops
    ```
4. Use the command-line with NubesGen ([more information here](/reference/rest-api/)) to generate a NubesGen configuration:
   ``` bash
   curl "https://nubesgen.com/demo.tgz?application=app_service.standard&gitops=true" | tar -xzvf -
   ```
5. Create a new branch called `env-dev`, and push your code:
   ``` bash
   git checkout -b env-dev
   git add .
   git commit -m 'Configure GitOps with NubesGen'
   git push --set-upstream origin env-dev
   ```
6. Go to your GitHub project, and check that the GitHub Action is running.
7. You can go to the [Azure Portal](https://aka.ms/nubesgen-portal) to check the created resources.
8. The application should be deployed on your App Service instance. Its URL should be in the form `https://app-demo-XXXX-XXXX-XXXX-XXXX-dev-001.azurewebsites.net/`, 
   and you can also find it in the GitHub Action workflow (Job: "manage-infrastructure", step "Apply Terraform"), or in the Azure portal.
   As it is a simple application, it should print by default `Hello, world`.
9.  Once you have finished, you should clean up your resources:
   1. Delete the resource group that was created by NubesGen to host your resources, which is named `rg-demo-XXXX-XXXX-XXXX-XXXX-001`.
   2. Delete the storage account used to store your Terraform state, in the `rg-terraform-001` resource group.

## Which Azure resources are created

NubesGen will generate:

- An [Azure App Service plan](https://aka.ms/nubesgen-app-service-plans) to define the type of App Service instance you will use.
- An [Azure App Service instance](https://aka.ms/nubesgen-app-service), configured to run your Docker image.
- An [Azure Container Registry instance](https://aka.ms/nubesgen-container-registry) to store your Docker images.

## Configuration options

In the generated `terraform/modules/app-service/main.tf` file, NubesGen will configure some environment variables 
for your application.

### Important options

- `WEBSITES_PORT` is the port that will be exposed by your Docker image. By default it is set to `8080`, but if your Docker image uses port `1337` instead, you will need to reconfigure this accordingly.
- `DOCKER_REGISTRY_SERVER_URL`, `DOCKER_REGISTRY_SERVER_USERNAME` and `DOCKER_REGISTRY_SERVER_PASSWORD` are used to access the Docker container registry. If you want to use another registry, you need to modify those variables accordingly.

### Other options

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

## Using a Dockerfile vs using Spring Boot

Using a Dockerfile is the default way to use Docker, so this will work in most situations. NubesGen will build your project using the standard `docker build` command.

Spring Boot is a Java framework that can use a Dockerfile, but which uses by default a Maven plugin: this is supported by NubesGen if you select that option, in which case the Docker image will be built using the `mvn spring-boot:build-image` command.

If you have selected Spring Boot, NubesGen will also configure the same configuration properties as the ones described in [Spring Boot with NubesGen](spring-boot/).
For example, your database should be automatically configured.
