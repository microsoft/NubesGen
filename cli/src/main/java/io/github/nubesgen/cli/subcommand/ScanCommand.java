package io.github.nubesgen.cli.subcommand;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import io.github.nubesgen.cli.util.Output;

@CommandLine.Command(name = "scan", description = "Scan the current project to find the technologies it uses")
public class ScanCommand implements Callable<Integer> {

    @Option(names = {"-d", "--directory"}, description = "Directory in which the CLI will be executed")
    public static String directory;

    @Override
    public Integer call() {
        String workingDirectory = Paths.get(".").toAbsolutePath().normalize().toString();
        if (directory != null) {
            workingDirectory = Paths.get(directory).toAbsolutePath().normalize().toString();
        }
        String request = scan(workingDirectory);
        Output.printTitle("GET request: " + request);
        Output.printInfo("Test this request on the NubesGen REST server:");
        Output.printMessage("curl \"https://nubesgen.com/demo.tgz" + request + "\" | tar -xzvf -");
        return 0;
    }

    public static String scan(String workingDirectory) {
        Output.printTitle("Scanning the current project...");
        Output.printMessage("Current directory: " + workingDirectory);
        String getRequest = "?application=APP_SERVICE.basic";
        File mavenFile = new File(workingDirectory + FileSystems.getDefault().getSeparator() + "pom.xml");
        File gradleFile = new File(workingDirectory + FileSystems.getDefault().getSeparator() + "build.gradle");
        String testFile = "";
        try {
            if (mavenFile.exists()) {
                Output.printInfo("Maven detected");
                testFile = Files.readString(mavenFile.toPath());
                if (testFile.contains("org.springframework.boot")) {
                    Output.printInfo("Runtime selected: Spring Boot + Maven");
                    getRequest += "&runtime=SPRING";
                } else if (testFile.contains("io.quarkus")) {
                    Output.printInfo("Runtime selected: Quarkus + Maven");
                    getRequest += "&runtime=QUARKUS";
                } else {
                    Output.printInfo("Runtime selected: Java + Maven");
                    getRequest += "&runtime=JAVA";
                }
                getRequest = javaDatabaseScanner(testFile, getRequest);
            } else if (gradleFile.exists()) {
                Output.printInfo("Gradle project detected");
                testFile = Files.readString(gradleFile.toPath());
                if (testFile.contains("org.springframework.boot")) {
                    Output.printInfo("Runtime selected: Spring Boot + Gradle");
                    getRequest += "&runtime=SPRING_GRADLE";
                } else {
                    Output.printInfo("Runtime selected: Java + Gradle");
                    getRequest += "&runtime=JAVA_GRADLE";
                }
                getRequest = javaDatabaseScanner(testFile, getRequest);
            } else {
                Output.printInfo("Runtime couldn't be detected, failing back to Docker");
            }
        } catch (IOException e) {
            Output.printError("Error while reading files: " + e.getMessage());
            Output.printInfo("Project technology couldn't be detected, failing back to Docker");
        }
        return getRequest;
    }

    private static String javaDatabaseScanner(String testFile, String getRequest) {
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
}
