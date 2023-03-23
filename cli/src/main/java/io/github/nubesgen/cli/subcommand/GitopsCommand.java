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
        Output.printInfo("(1/10) Checking if the project is configured on GitHub...");
        try {
            String remoteRepo = ProcessExecutor.executeAndReturnString("git config --get remote.origin.url");
            int exitCode = ProcessExecutor.execute("gh secret set NUBESGEN_TEST -b\"test\" -R " + remoteRepo);
            if (exitCode != 0) {
                Output.printError("The project isn't configured on GitHub, GitOps configuration is disabled.");
                return -1;
            } else {
                ProcessExecutor.execute("gh secret remove NUBESGEN_TEST -R " + remoteRepo);
            }
        } catch (Exception e) {
            Output.printError("The project isn't configured on GitHub, GitOps configuration is disabled. Error: "
                    + e.getMessage());

            return -1;
        }

        Output.printInfo("(2/10) Checking if the project already has GitOps configured...");
        try {
            String remoteRepo = ProcessExecutor.executeAndReturnString("git config --get remote.origin.url");
            String secretList = ProcessExecutor.executeAndReturnString("gh secret list -R " + remoteRepo);
            if (secretList.contains("AZURE_CREDENTIALS")) {
                Output.printInfo("The project already has a \"AZURE_CREDENTIALS\" secret.");
                if (refresh) {
                    Output.printMessage("As the \"--refresh-secrets\" option was selected, this secret will be refreshed.");
                } else {
                    Output.printMessage("To refresh this secret, use the \"--refresh-secrets\" (or \"-r\" shortcut) option when running this CLI.");
                    return 0;
                }
            }
            if (secretList.contains("TF_STORAGE_ACCOUNT")) {
                Output.printInfo("The project already has a \"TF_STORAGE_ACCOUNT\" secret.");
                if (refresh) {
                    Output.printMessage("As the \"--refresh-secrets\" option was selected, this secret will be refreshed.");
                } else {
                    Output.printMessage("To refresh this secret, use the \"--refresh-secrets\" (or \"-r\" shortcut) option when running this CLI.");
                    return 0;
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
        tfStorageAccount = tfStorageAccount.toLowerCase();
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
            Output.printInfo("(3/10) Create resource group \"" + resourceGroup + "\"");
            String resourceGroupExists = ProcessExecutor
                    .executeAndReturnString("az group exists --name " + resourceGroup);
            if (!resourceGroupExists.equals("true")) {
                ProcessExecutor
                        .execute("az group create --name " + resourceGroup + " --location " + location + " -o none");
            }

            Output.printInfo("(4/10) Create storage account \"" + tfStorageAccount + "\"");
            ProcessExecutor.execute("az storage account create --resource-group " + resourceGroup + " --name "
                    + tfStorageAccount
                    + " --sku Standard_LRS --allow-blob-public-access false --encryption-services blob -o none");

            Output.printInfo("(5/10) Get storage account key");
            String storageAccountKey = ProcessExecutor
                    .executeAndReturnString("az storage account keys list --resource-group " + resourceGroup
                            + " --account-name " + tfStorageAccount + " --query '[0].value' -o tsv");

            Output.printInfo("(6/10) Create blob container");
            ProcessExecutor.execute("az storage container create --name " + containerName + " --account-name "
                    + tfStorageAccount + " --account-key " + storageAccountKey + " -o none");

            Output.printInfo("(7/10) Create a virtual network");
            String vnet = "vnet-" + tfStorageAccount;
            String subnet = "snet-" + tfStorageAccount;
            ProcessExecutor.execute("az network vnet create --resource-group " + resourceGroup
                + " --name " + vnet + " --subnet-name " + subnet + " -o none");
            ProcessExecutor.execute("az network vnet subnet update --resource-group " + resourceGroup
                + " --name " + subnet + " --vnet-name " + vnet + " --service-endpoints \"Microsoft.Storage\" -o none");

            Output.printInfo("(8/10) Secure the storage account in the virtual network");
            ProcessExecutor.execute("az storage account network-rule add --account-name " + tfStorageAccount
                + " --vnet-name " + vnet + " --subnet " + subnet + " -o none");
            ProcessExecutor.execute("az storage account update --name " + tfStorageAccount
                + " --default-action Deny --bypass None -o none");

            Output.printInfo("(9/10) Get current subscription");
            String subscriptionId = ProcessExecutor
                    .executeAndReturnString("az account show --query id --output tsv --only-show-errors");

            Output.printInfo("(10/10) Create secrets in GitHub");
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
