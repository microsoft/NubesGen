# Terraform module for Azure App Service with Docker configuration

This module configures a Azure App Service with Docker instance with Terraform.

To create the Docker image:

```bash
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=nubesgen/nubesgen
```

To push the Docker image:

```bash
docker image push acrnubesgendev001.azurecr.io/nubesgen/nubesgen:latest
```
