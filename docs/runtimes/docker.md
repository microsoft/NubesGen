# Using Docker with NubesGen

Docker is the default option with NubesGen, and allows to run any kind of application supporting Docker.

It comes with two options: "Docker with a Dockerfile" and "Docker with Spring Boot".

## What Azure resources will be created

NubesGen will generate:

- An [Azure App Service plan and instance](https://azure.microsoft.com/services/app-service/) to run your Docker image.
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

To follow this tutorial, you need to have [Terraform](https://www.terraform.io/) and the [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli) installed. As we will create some Azure resources, you need to be authenticated using the Azure CLI.

1. Clone the project on your computer, and go into the project's directory:
   ```bash
   git clone https://github.com/jdubois/golang-sample-app.git
   cd golang-sample-app
   ``` 
2. Use [the command-line with NubesGen](../command-line.md) to generate a NubesGen configuration. Modify the name of the file (`golang-sample-app.tgz` by default) to have a unique name you can use in your Azure subscription.
  ```bash
  curl "https://nubesgen.azurewebsites.net/golang-sample-app.tgz" | tar -xzvf -
  ```
3. Go to the newly-created `terraform` directory, and apply the Terraform configuration.
   ```bash
   cd terraform && terraform init && terraform apply -auto-approve
   ```
4. You can check the newly-created resources in the [Azure Portal](https://portal.azure.com). Go to the Azure Container Registry that was created, 
   and use the credentials in to login the Docker registry:
5. You can now build the Docker image, push it the Azure Container Registry, and it will be deployed to the Azure App Service instance that was created.

If you want to automate steps 3 to 5, have a look at the [GitOps option](../gitops-overview.md) offered by NubesGen.
