package io.github.nubesgen.cli.subcommand.scan;

import io.github.nubesgen.cli.util.Output;

import java.util.ArrayList;
import java.util.List;

public class DotNetScanner {

    public static String dotnetDatabaseScanner(String testFile, String getRequest) {
        if (testFile.contains("\"npgsql\"")) {
            Output.printInfo("Database selected: PostgreSQL");
            getRequest += "&database=POSTGRESQL";
        } else if (testFile.contains("\"MySql.data\"") || testFile.contains("\"MySqlConnector\"")) {
            Output.printInfo("Database selected: MySQL");
            getRequest += "&database=MYSQL";
        } else if (testFile.contains("\"Microsoft.Data.SqlClient\"") || testFile.contains("\"System.Data.SqlClient\"")) {
            Output.printInfo("Database selected: Azure SQL");
            getRequest += "&database=SQL_SERVER";
        } else {
            Output.printInfo("Database selected: None");
        }
        return getRequest;
    }

    public static String dotnetAddOnScanner(String testFile, String getRequest) {
        List<String> addOns = new ArrayList<>();
        if (testFile.contains("\"MongoDB.Driver\"")) {
            addOns.add("cosmosdb_mongodb");
            Output.printInfo("Add-on selected: MongoDB");
        }
        if (testFile.contains("\"StackExchange.Redis\"") || testFile.contains("\"Microsoft.Extensions.Caching.Redis\"")) {
            addOns.add("redis");
            Output.printInfo("Add-on selected: Redis");
        }
        if (testFile.contains("\"Azure.Storage.Blobs\"")) {
            addOns.add("storage_blob");
            Output.printInfo("Add-on selected: Azure Blob Storage");
        }
        if (testFile.contains("\"Azure.Security.KeyVault")) {
            addOns.add("key_vault");
            Output.printInfo("Add-on selected: Azure Key Vault");
        }
        if (testFile.contains("\"Microsoft.ApplicationInsights\"")) {
            addOns.add("application_insights");
            Output.printInfo("Add-on selected: Azure Application Insights");
        }
        
        GenericScanner.genericAddOnScanner(addOns);
        if (addOns.size() > 0) {
            getRequest += "&addons=" + String.join(",", addOns);
        }
        return getRequest;
    }
}
