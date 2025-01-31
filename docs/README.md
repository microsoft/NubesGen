# Nubesgen Documentation

Welcome to the Nubesgen documentation. Here you'll find everything you need to learn and **deploy applications and services on Azure in minutes**.

::: danger
**NubesGen has been archived**.

Thank you to everyone who used NubesGen over the past few years. We hope it was helpful. The project is now in an archived state, so the domain nubesgen.com no longer hosts the tool, and no further bug or security fixes will be provided.

You can still find the source code and documentation on GitHub, and the latest version of the code is also available as a Docker image.

Please note that any references to nubesgen.com in this documentation are no longer valid. Instead, we recommend running NubesGen locally (see below for details).

:::

## Using NubesGen locally

1. Download the docker image `ghcr.io/microsoft/nubesgen/nubesgen:main`
1. Run the image, with exposing port 8080
1. Go to [http://localhost:8080](http://localhost:8080)

```bash
docker pull ghcr.io/microsoft/nubesgen/nubesgen:main
docker run --name nubesgen -d -p 8080:8080 ghcr.io/microsoft/nubesgen/nubesgen:main
```

You'll be able to use Nubesgen, both from the UI or from the command line.

Once you're done, you can tear down the container and delete it with the following command:

```bash
docker stop nubesgen
docker rm nubesgen
## Optional: delete the image from the local cache
docker rmi ghcr.io/microsoft/nubesgen/nubesgen:main

```

## Version compatibility

This documentation only reflects the latest version of Nubesgen.


## Open Source

Nubesgen and its documentation are both completely Open Source. You can **support the project by starring** [our main GitHub repository](https://github.com/microsoft/nubesgen) or contributing to the docs. 

<a class="github-button" href="https://github.com/microsoft/nubesgen" data-icon="octicon-star" data-size="large" data-show-count="true" aria-label="Star microsoft/nubesgen on GitHub">Star</a>
<a class="github-button" href="https://github.com/microsoft/nubesgen/subscription" data-icon="octicon-eye" data-size="large" data-show-count="true" aria-label="Watch microsoft/nubesgen on GitHub">Watch</a>

