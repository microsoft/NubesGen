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
