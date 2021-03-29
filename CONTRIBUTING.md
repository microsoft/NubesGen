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

The easiest way to work on the project is to use [Visual Studio Code](https://code.visualstudio.com/) with [Docker](https://docs.docker.com/get-docker/) and the [Remote Development Extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.vscode-remote-extensionpack).

Once you have the project cloned on your machine, open the VS Code command palette and select **Reopen Folder in Container**. It will take a few minutes the first time while the container image is building, after that you're ready to code.

| Command                          | Action                                          |
|----------------------------------|-------------------------------------------------|
| `./mvnw package`                 | Generates .jar package in `/target` folder      |
| `./mvnw test`                    | Runs tests                                      |
| `java -jar target/nubesgen*.jar` | Starts NubesGen server on http://localhost:8080 |
