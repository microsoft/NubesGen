# CLI for NubesGen

This CLI is part of the [NubesGen project](https://nubesgen.com).

It automates a project configuration: on simple projects,
running this command line should be enough
to have your project configured with GitOps, and automate its deployment to Azure.

More specifically, this tool:
- scans the project to find its NubesGen configuration
- sets up the GitOps workflow
- creates the Infrastructure as Code (IaC) configuration

## Installation

### Prerequisites

__Tip:__ You can go to [https://shell.azure.com](https://shell.azure.com) and login with the Azure subscription you want to use. This will provide you with the 
mandatory prerequisites below (Bash, Azure CLI, and GitHub CLI).

For the CLI to work, you need to have installed and configured the following tools:

- [Bash](https://fr.wikipedia.org/wiki/Bourne-Again_shell), which is installed by default on most Linux distributions and on Mac OS X. If you're using Windows, one solution is to use [WSL](https://aka.ms/nubesgen-install-wsl).
- [Azure CLI](https://aka.ms/nubesgen-install-az-cli). To login, use `az login`.
- (optional) [GitHub CLI](https://cli.github.com/). To login, use `gh auth login`. This will automate creating the GitHub secrets for you, otherwise you will need to do it manually.

### Downloading and installing the CLI

This CLI is available as a native image on major platforms (Linux, Mac OS, Windows), and as a Java archive. The Java archive will run everywhere, but
requires a Java Virtual Machine.

Releases are available on GitHub at [https://github.com/microsoft/nubesgen/releases](https://github.com/microsoft/nubesgen/releases) and in the following examples we will use the GitHub CLI to download them.

<details>
<summary>Downloading and installing the CLI with Java</summary>

To run the Java archive, you need to have a Java Virtual Machine (version 11 or higher) installed.

- Download the latest release: `gh release download --repo microsoft/nubesgen --pattern='nubesgen-cli-*.jar'`
- Run the binary: `java -jar nubesgen-*.jar -h`
</details>

<details>
<summary>Downloading and installing the CLI on Linux</summary>

To run the binary on Linux, you need to:

- Download the latest release: `gh release download --repo microsoft/nubesgen --pattern='nubesgen-cli-linux'`
- Make the binary executable: `chmod +x nubesgen-cli-linux`
- Run the binary: `./nubesgen-cli-linux -h`

</details>
<details>
<summary>Downloading and installing the CLI on a Mac OS</summary>

To run the binary on a Mac OS, you need to:

- Download the latest release: `gh release download --repo microsoft/nubesgen --pattern='nubesgen-cli-macos'`
- If on Apple Silicon, install Rosetta if it's not already installed: `/usr/sbin/softwareupdate --install-rosetta --agree-to-license`
- Make the binary executable: `chmod +x nubesgen-cli-macos`
- Allow Mac OS X to execute it: `xattr -d com.apple.quarantine nubesgen-cli-macos`
- Run the binary: `./nubesgen-cli-macos -h`

</details>
<details>
<summary>Downloading and installing the CLI on Windows</summary>

To run the binary on Windows, you need to:

- Download the latest release: `gh release download --repo microsoft/nubesgen --pattern='nubesgen-cli-windows.exe'`
- Run the binary: `nubesgen-cli-windows -h`

</details>

## Using the CLI

_Use the `-h` option with the CLI to have a complete and up-to-date help page_

- Running the CLI without any arguments (e.g. `./nubesgen-cli-linux`) will run the most common sub-commands in a logical order. This will setup GitOps, scan your project to find the technologies used, and download the Infrastructure as Code configuration from the NubesGen REST server.
- The `projectname` sub-command (e.g. `./nubesgen-cli-linux projectname`) will create a unique project name to be used inside Azure. It will take the first 8 letters of the name of your current directory, and add two random numbers.
- The `scan` sub-command (e.g. `./nubesgen-cli-linux scan`) will scan your project to find the technologies used. It will use those technologies to generate a REST request String that can be sent to the NubesGen REST server.
- The `gitops` sub-command (e.g. `./nubesgen-cli-linux gitops`) will setup GitOps for the current project.
- The `download` sub-command (e.g. `./nubesgen-cli-linux download`) will download a default Infrastructure as Code configuration from the NubesGen REST server.
