# Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Working on the project

The project is divided into 2 sub-projects:

- The `rest-server` folder contains the REST Server for NubesGen, which is the main project.
- The `cli` folder contains the command line interface for NubesGen.

Both projects are built using Maven, and use Java.

The easiest way to work on those projects is to use [Visual Studio Code](https://code.visualstudio.com/) with [Docker](https://docs.docker.com/get-docker/) and the [Remote Development Extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.vscode-remote-extensionpack).

Once you have the project cloned on your machine, open the VS Code command palette and select **Reopen Folder in Container**. It will take a few minutes the first time while the container image is building, after that you're ready to code.

| Command                          | Action                                          |
|----------------------------------|-------------------------------------------------|
| `./mvnw package`                 | Generates .jar package in `/target` folder      |
| `./mvnw test`                    | Runs tests                                      |
| `java -jar target/nubesgen*.jar` | Starts NubesGen REST server on http://localhost:8080 |
| `./mvnw spring-boot:run`  | Compiles and starts NubesGen REST server on https://localhost:8080 |

## Doing a release

Releases are managed using the [Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/), which
will automatically increase the version number in the `pom.xml` and tag the repository.

To do a release:
- Update the version number and tag the repository (in the `rest-server` sub-project): `./mvnw release:clean release:prepare`
- Use the GitHub CLI to create the release: `gh release create <TAG_NAME>` (replace `<TAG_NAME>` by the name of the tag created 
  by the Maven Release Plugin)
  
Creating the GitHub release will trigger the [Release GitHub Action](https://github.com/microsoft/NubesGen/blob/main/.github/workflows/release.yml), 
which will create two artifacts:
- An executable Jar file
- A Docker image

Those artifacts will be automatically added to the GitHub release, once the GitHub Action completes.

## Deploying to production

You should only deploy to production a release that was tagged in the previous section.

To deploy to production, push the code to the `prod` branch. This will trigger the [Continuous Deployment GitHub Action](https://github.com/microsoft/NubesGen/blob/main/.github/workflows/continuous-deployment.yml), which will execute the deployment process.
