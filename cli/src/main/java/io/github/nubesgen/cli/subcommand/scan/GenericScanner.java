package io.github.nubesgen.cli.subcommand.scan;

import io.github.nubesgen.cli.subcommand.ScanCommand;
import io.github.nubesgen.cli.util.Output;

import java.util.List;

public class GenericScanner {
    public static void genericAddOnScanner(List<String> addOns) {
        if (ScanCommand.applicationInsights) {
            addOns.add("application_insights");
            Output.printInfo("Add-on selected: Application Insights");
        }
        if (ScanCommand.azureKeyVault) {
            addOns.add("key_vault");
            Output.printInfo("Add-on selected: Azure Key Vault");
        }
    }
}
