package io.github.nubesgen.cli.subcommand.scan;

import io.github.nubesgen.cli.util.Output;

import java.util.ArrayList;
import java.util.List;

public class PythonScanner {

    public static String pythonDatabaseScanner(String testFile, String getRequest) {
        if (testFile.contains("psycopg2")) {
            Output.printInfo("Database selected: PostgreSQL");
            getRequest += "&database=POSTGRESQL";
        } else if (testFile.contains("mysql-connector-python")) {
            Output.printInfo("Database selected: MySQL");
            getRequest += "&database=MYSQL";
        } else if (testFile.contains("pyodbc")) {
            Output.printInfo("Database selected: Azure SQL");
            getRequest += "&database=SQL_SERVER";
        } else {
            Output.printInfo("Database selected: None");
        }
        return getRequest;
    }

    public static String pythonAddOnScanner(String testFile, String getRequest) {
        List<String> addOns = new ArrayList<>();
        if (testFile.contains("pymongo") ||
            testFile.contains("motor")) {

            addOns.add("cosmosdb_mongodb");
            Output.printInfo("Add-on selected: MongoDB");
        }
        if (testFile.contains("redis")) {
            addOns.add("redis");
            Output.printInfo("Add-on selected: Redis");
        }
        if (testFile.contains("azure-storage-blob")) {
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
