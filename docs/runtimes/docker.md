[[ << Back to the main documentation page](../README.md)]

# Using Docker with NubesGen

Docker is the default option with NubesGen, and allows to run any kind of application supporting Docker.

It comes with two options: "Docker with a Dockerfile" and "Docker with Spring Boot".

## Which Azure resources will be created

NubesGen will generate:

- An [Azure App Service plan](https://docs.microsoft.com/azure/app-service/overview-hosting-plans) to define the type of App Service instance you will use.
- An [Azure App Service instance](https://azure.microsoft.com/services/app-service/), configured to run your Docker image.
- An [Azure Container Registry instance](https://azure.microsoft.com/services/container-registry/) to store your Docker images.

## Important configuration options

In the generated `terraform/modules/app-service/main.tf` file, the following options are worth noticing:

- `WEBSITES_PORT` is the port that will be exposed by your Docker image. By default it is set to `8080`, but if your Docker image uses port `1337` instead, you will need to reconfigure this accordingly.
- `DOCKER_REGISTRY_SERVER_URL`, `DOCKER_REGISTRY_SERVER_USERNAME` and `DOCKER_REGISTRY_SERVER_PASSWORD` are used to access the Docker container registry. If you want to use another registry, you need to modify those variables accordingly.

## Using a Dockerfile vs using Spring Boot

Using a Dockerfile is the default way to use Docker, so this will work in most situations. NubesGen will build your project using the standard `docker build` command.

Spring Boot is a Java framework that can use a Dockerfile, but which uses by default a Maven plugin: this is supported by NubesGen if you select that option, in which case the Docker image will be built using the `mvn spring-boot:build-image` command.

## Tutorial: running a Go application with NubesGen

We're going to deploy [https://github.com/jdubois/golang-sample-app](https://github.com/jdubois/golang-sample-app), which is a sample application written in Go.
We'll use NubesGen's [GitOps support](../gitops-overview.md) to automatically build and deploy the application.

1. Fork the project on your GitHub account.
2. Clone the fork on your computer. Change `<your-github-account>` by the name of your GitHub account:
   ```bash
   git clone https://github.com/<your-github-account>/golang-sample-app.git
   ``` 
3. In the cloned project (`cd golang-sample-app`), set up [GitOps with NubesGen by following this tutorial](../gitops-quick-start.md) (you've already done steps 1 & 2 above).
4. Use [the command-line with NubesGen](../command-line.md) to generate a NubesGen configuration. Modify the name of the file (`<your-unique-name>.tgz`) to have a unique name you can use in your Azure subscription.
   ```bash
   curl "https://nubesgen.azurewebsites.net/<your-unique-name>.tgz?application=app_service.standard&gitops=true" | tar -xzvf -
   ```
5. Create a new branch called `env-dev`, and push your code:
   ```bash
   git checkout -b env-dev && git add . && git commit -m 'Configure GitOps with NubesGen' && git push --set-upstream origin env-dev
   ```
6. Go to your GitHub project, and check that the GitHub Action is running.
7. You can go to the [Azure Portal](https://portal.azure.com) to check the created resources.
8. The application should be deployed on your App Service instance. Its URL should be in the form `https://app-<your-unique-name>-dev-001.azurewebsites.net/`, and you can also find it in the GitHub Action workflow (Job: "manage-infrastructure", step "Apply Terraform"), or in the Azure portal.
As it is a simple application, it should print by default `Hello, world`.
9. Once you have finished, you should clean up your resources:
   1. Delete the resource group that was created by NubesGen to host your resources, which is named `rg-<your-unique-name>-001`.
   2. Delete the storage account used to store your Terraform state, in the `rg-terraform-001` resource group, named ``.

[[ << Back to the main documentation page](../README.md)]
