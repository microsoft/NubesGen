# Terraform module for Azure App Service with Docker configuration

This module configures a Azure App Service with Docker instance with Terraform.

To create the Docker image:

```bash
./mvnw package
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build -t acrnubesgen001.azurecr.io/nubesgen/nubesgen . 
```

To push the Docker image:

```bash
docker login acrnubesgen001.azurecr.io
docker image push acrnubesgen001.azurecr.io/nubesgen/nubesgen:latest
```
