package io.github.nubesgen.cli.subcommand;

import picocli.CommandLine;

import java.util.concurrent.Callable;

import io.github.nubesgen.cli.util.Output;
import io.github.nubesgen.cli.util.ProcessExecutor;

@CommandLine.Command(name = "healthcheck", description = "Checks if the required tools are installed and available")
public class HealthCommand  implements Callable<Integer> {

    @Override public Integer call() {
        return configure();
    }

    public static Integer configure() {
        Output.printTitle("Checking required tools installation status...");
        Output.printInfo("Checking Azure CLI status...");
        if (ProcessExecutor.execute("az > /dev/null") != 0) {
            Output.printError("Azure CLI is not installed. Please install it first, go to https://docs.microsoft.com/cli/azure/install-azure-cli for more information.");
            return -1;
        } else {
            Output.printInfo("Azure CLI is installed.");
        }
        if (ProcessExecutor.execute("az account show > /dev/null") != 0) {
            Output.printError("You are not authenticated with Azure CLI. Please run 'az login' first.");
            return -1;
        } else {
            Output.printInfo("You are authenticated with Azure CLI.");
        }
        Output.printInfo("GitHub CLI status...");
        if (ProcessExecutor.execute("gh version > /dev/null") != 0) {
            Output.printError("GitHub CLI is not installed. Please install it first, go to https://github.com/cli/cli#installation for more information.");
            return -1;
        } else {
            Output.printInfo("GitHub CLI is installed.");
        }
        if (ProcessExecutor.execute("gh auth status") != 0) {
            Output.printError("You are not authenticated with GitHub CLI. Please run 'gh auth login' first.");
            return -1;
        } else {
            Output.printInfo("You are authenticated with GitHub CLI.");
        }
        return 0;
    }
}
