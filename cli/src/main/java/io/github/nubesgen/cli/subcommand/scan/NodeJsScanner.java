package io.github.nubesgen.cli.subcommand.scan;

import io.github.nubesgen.cli.util.Output;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NodeJsScanner {

    public static String nodejsDatabaseScanner(String testFile, String getRequest) {
        if (testFile.contains("\"pg\"")) {
            Output.printInfo("Database selected: PostgreSQL");
            getRequest += "&database=POSTGRESQL";
        } else if (testFile.contains("\"mysql2\"")) {
            Output.printInfo("Database selected: MySQL");
            getRequest += "&database=MYSQL";
        } else if (testFile.contains("\"mssql\"")) {
            Output.printInfo("Database selected: Azure SQL");
            getRequest += "&database=SQL_SERVER";
        } else {
            Output.printInfo("Database selected: None");
        }
        return getRequest;
    }

    public static String nodejsAddOnScanner(String testFile, String getRequest) {
        List<String> addOns = new ArrayList<>();
        if (testFile.contains("\"mongodb\"") ||
            testFile.contains("\"mongoose\"")) {

            addOns.add("cosmosdb_mongodb");
            Output.printInfo("Add-on selected: MongoDB");
        }
        if (testFile.contains("\"redis\"")) {
            addOns.add("redis");
            Output.printInfo("Add-on selected: Redis");
        }
        if (testFile.contains("\"@azure/storage-blob\"")) {
            addOns.add("storage_blob");
            Output.printInfo("Add-on selected: Azure Blob Storage");
        }
        GenericScanner.genericAddOnScanner(addOns);
        if (addOns.size() > 0) {
            getRequest += "&addons=" + String.join(",", addOns);
        }
        return getRequest;
    }
}
