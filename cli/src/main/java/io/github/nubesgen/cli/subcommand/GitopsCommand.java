package io.github.nubesgen.cli.subcommand;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.text.DecimalFormat;
import java.util.concurrent.Callable;

import io.github.nubesgen.cli.util.Output;
import io.github.nubesgen.cli.util.ProcessExecutor;

@CommandLine.Command(name = "gitops", description = "Setup GitOps on the current project")
public class GitopsCommand implements Callable<Integer> {

    @Option(names = {"-r", "--refresh-secrets"}, description = "Refresh the GitOps configuration if it already exists")
    public static boolean refreshSecrets;

    @Override
    public Integer call() {
        DecimalFormat formater = new DecimalFormat("0000");
        String random1 = formater.format(Math.random() * (10000));
        String random2 = formater.format(Math.random() * (10000));
        return gitops("st" + random1 + random2, refreshSecrets);
    }

    public static Integer gitops(String tfStorageAccount, boolean refresh) {
        Output.printTitle("Setting up GitOps...");
        Output.printInfo("(1/8) Checking if the project is configured on GitHb...");
        try {
            int exitCode = ProcessExecutor.execute("gh secret set NUBESGEN_TEST -b\"test\"");
            if (exitCode != 0) {
                Output.printError("The project isn't configured on GitHub, GitOps configuration is disabled.");
                return -1;
            } else {
                ProcessExecutor.execute("gh secret remove NUBESGEN_TEST");
            }
        } catch (Exception e) {
            Output.printError("The project isn't configured on GitHub, GitOps configuration is disabled. Error: "
                    + e.getMessage());

            return -1;
        }

        Output.printInfo("(2/8) Checking if the project already has GitOps configured...");
        try {
            String secretList = ProcessExecutor.executeAndReturnString("gh secret list");
            if (secretList.contains("AZURE_CREDENTIALS")) {
                Output.printInfo("The project already has a \"AZURE_CREDENTIALS\" secret.");
                if (refresh) {
                    Output.printMessage("As the \"--refresh-secrets\" option was selected, this secret will be refreshed.");
                } else {
                    Output.printMessage("To refresh this secret, use the \"--refresh-secrets\" (or \"-r\" shortcut) option when running this CLI.");
                    return -1;
                }
            }
            if (secretList.contains("TF_STORAGE_ACCOUNT")) {
                Output.printInfo("The project already has a \"TF_STORAGE_ACCOUNT\" secret.");
                if (refresh) {
                    Output.printMessage("As the \"--refresh-secrets\" option was selected, this secret will be refreshed.");
                } else {
                    Output.printMessage("To refresh this secret, use the \"--refresh-secrets\" (or \"-r\" shortcut) option when running this CLI.");
                    return -1;
                }
            }
        } catch (Exception e) {
            Output.printError("The project isn't configured on GitHub, GitOps configuration is disabled. Error: "
                    + e.getMessage());

            return -1;
        }

        // The resource group used by Terraform to store its remote state.
        String resourceGroup = "rg-terraform-001";
        // The location of the resource group.
        String location = "eastus";
        // The storage account (inside the resource group) used by Terraform to store
        // its remote state.
        tfStorageAccount = tfStorageAccount.replaceAll("-", "");
        if (tfStorageAccount.length() > 16) {
            tfStorageAccount = tfStorageAccount.substring(0, 16);
        }
        DecimalFormat formater = new DecimalFormat("0000");
        String random1 = formater.format(Math.random() * (10000));
        String random2 = formater.format(Math.random() * (10000));
        tfStorageAccount += random1 + random2;
        // The container name (inside the storage account) used by Terraform to store
        // its remote state.
        String containerName = "tfstate";

        // Run commands
        try {
            Output.printInfo("(3/8) Create resource group \"" + resourceGroup + "\"");
            String resourceGroupExists = ProcessExecutor
                    .executeAndReturnString("az group exists --name " + resourceGroup);
            if (!resourceGroupExists.equals("true")) {
                ProcessExecutor
                        .execute("az group create --name " + resourceGroup + " --location " + location + " -o none");
            }

            Output.printInfo("(4/8) Create storage account \"" + tfStorageAccount + "\"");
            ProcessExecutor.execute("az storage account create --resource-group " + resourceGroup + " --name "
                    + tfStorageAccount
                    + " --sku Standard_LRS --allow-blob-public-access false --encryption-services blob -o none");

            Output.printInfo("(5/8) Get storage account key");
            String storageAccountKey = ProcessExecutor
                    .executeAndReturnString("az storage account keys list --resource-group " + resourceGroup
                            + " --account-name " + tfStorageAccount + " --query '[0].value' -o tsv");

            Output.printInfo("(6/8) Create blob container");
            ProcessExecutor.execute("az storage container create --name " + containerName + " --account-name "
                    + tfStorageAccount + " --account-key " + storageAccountKey + " -o none");

            Output.printInfo("(7/8) Get current subscription");
            String subscriptionId = ProcessExecutor
                    .executeAndReturnString("az account show --query id --output tsv --only-show-errors");

            Output.printInfo("(8/8) Create secrets in GitHub");
            Output.printInfo("Using the GitHub CLI to set secrets.");
            String remoteRepo = ProcessExecutor.executeAndReturnString("git config --get remote.origin.url");
            ProcessExecutor.execute("SERVICE_PRINCIPAL=$(az ad sp create-for-rbac --role=\"Contributor\" --scopes=\"/subscriptions/"
                + subscriptionId + "\" --sdk-auth --only-show-errors) &&" +
                " gh secret set AZURE_CREDENTIALS -b\"$SERVICE_PRINCIPAL\" -R " + 
                remoteRepo);
            ProcessExecutor.execute("gh secret set TF_STORAGE_ACCOUNT -b\"" + tfStorageAccount + "\" -R " + remoteRepo);
            Output.printTitle("Congratulations! You have successfully configured GitOps with NubesGen.");
        } catch (Exception e) {
            Output.printError("Error while executing GitOps configuration: " + e.getMessage());
            return -1;
        }
        return 0;
    }
}
