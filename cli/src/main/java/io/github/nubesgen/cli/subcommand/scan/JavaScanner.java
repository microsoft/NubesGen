package io.github.nubesgen.cli.subcommand.scan;

import io.github.nubesgen.cli.util.Output;

import java.util.ArrayList;
import java.util.List;

public class JavaScanner {

    public static String javaDatabaseScanner(String testFile, String getRequest) {
        if (testFile.contains("org.postgresql")) {
            Output.printInfo("Database selected: PostgreSQL");
            getRequest += "&database=POSTGRESQL";
        } else if (testFile.contains("mysql-connector-java")) {
            Output.printInfo("Database selected: MySQL");
            getRequest += "&database=MYSQL";
        } else if (testFile.contains("com.microsoft.sqlserver")) {
            Output.printInfo("Database selected: Azure SQL");
            getRequest += "&database=SQL_SERVER";
        } else {
            Output.printInfo("Database selected: None");
        }
        return getRequest;
    }

    public static String javaAddOnScanner(String testFile, String getRequest) {
        List<String> addOns = new ArrayList<>();
        if (testFile.contains("spring-boot-starter-data-mongodb") ||
            testFile.contains("mongodb-driver-sync")) {

            addOns.add("cosmosdb_mongodb");
            Output.printInfo("Add-on selected: MongoDB");
        }
        if (testFile.contains("spring-boot-starter-data-redis") ||
         (testFile.contains("redis.clients") && testFile.contains("jedis")) ||
         (testFile.contains("io.lettuce") && testFile.contains("lettuce-core")) ||
         (testFile.contains("org.redisson") && testFile.contains("redisson"))) {

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
