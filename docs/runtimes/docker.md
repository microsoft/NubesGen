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

## Example: running a Go application with NubesGen

We're going to deploy [https://github.com/codefresh-contrib/golang-sample-app](https://github.com/codefresh-contrib/golang-sample-app), which is a sample application written in Go.

1. Fork the project
2. Clone your fork in your local computer, and go to the project's root directory.
3. Use [the command-line with NubesGen](../command-line.md) to generate a NubesGen configuration. Modify the name of the file (`golang-sample-app` by default) to have a unique name you can use in your Azure subscription.
  ```bash
  curl "https://nubesgen.azurewebsites.net/golang-sample-app.tgz?gitops=true" | tar -xzvf -
  ```
4. Follow the [GitOps quick start](../gitops-quick-start.md) to set up GitOps for your project.
5. Create a new branch called `env-prod` and push it: `git checkout -b env-prod && git push`
6. Go to your fork's GitHub Actions tab to see the Azure infrastructure being set up.
7. Go to the [Azure Portal](https://portal.azure.com) to check the created resources.
8. Go to the Azure App Service instance to check if the application is running.
9. You can modify the application or its infrastructure, and do `git push` to have everything updated automatically.
10. You can create a second branch called `env-dev` to check how you can work with a second environment.
