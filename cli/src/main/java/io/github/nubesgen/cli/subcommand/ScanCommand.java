package io.github.nubesgen.cli.subcommand;

import io.github.nubesgen.cli.subcommand.scan.*;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import io.github.nubesgen.cli.util.FilesUtil;
import io.github.nubesgen.cli.util.Output;

@CommandLine.Command(name = "scan", description = "Scan the current project to find the technologies it uses")
public class ScanCommand implements Callable<Integer> {

    @Option(names = {"-d", "--directory"}, description = "Directory in which the CLI will be executed")
    public static String directory;

    @Option(names = {"--application-insights"}, description = "Add support for Application Performance Management (APM)")
    public static boolean applicationInsights;

    @Option(names = {"--azure-key-vault"}, description = "Add support for Azure Key Vault to store the secrets")
    public static boolean azureKeyVault;

    @Option(names = {"--vnet"}, description = "Add support for VNet to secure network connections")
    public static boolean vnet;

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
        File nodejsFile = new File(workingDirectory + FileSystems.getDefault().getSeparator() + "package.json");
        File pythonFile = new File(workingDirectory + FileSystems.getDefault().getSeparator() + "requirements.txt");
        Optional<Path> dotnetFile = dotNetFinder(workingDirectory);
        String testFile;
        try {
            if (mavenFile.exists()) {
                Output.printInfo("Maven detected");
                testFile = FilesUtil.readString(mavenFile.toPath());
                if (testFile.contains("spring-cloud-starter-function") || testFile.contains("azure-functions-java-library")) {
                    Output.printInfo("Project type detected: Azure Functions");
                    getRequest = "?application=FUNCTION.consumption";
                }
                if (testFile.contains("org.springframework.boot")) {
                    Output.printInfo("Runtime selected: Spring Boot + Maven");
                    getRequest += "&runtime=SPRING";
                } else if (testFile.contains("io.quarkus")) {
                    Output.printInfo("Runtime selected: Quarkus + Maven");
                    getRequest += "&runtime=QUARKUS";
                } else if (testFile.contains("micronaut")) {
                    Output.printInfo("Runtime selected: Micronaut + Maven");
                    getRequest += "&runtime=MICRONAUT";
                } else {
                    Output.printInfo("Runtime selected: Java + Maven");
                    getRequest += "&runtime=JAVA";
                }
                getRequest = JavaScanner.javaDatabaseScanner(testFile, getRequest);
                getRequest = JavaScanner.javaAddOnScanner(testFile, getRequest);
            } else if (gradleFile.exists()) {
                Output.printInfo("Gradle project detected");
                testFile = FilesUtil.readString(gradleFile.toPath());
                if (testFile.contains("spring-cloud-starter-function") || testFile.contains("azure-functions-java-library")) {
                    Output.printInfo("Project type detected: Azure Functions");
                    getRequest = "?application=FUNCTION.consumption";
                }
                if (testFile.contains("org.springframework.boot")) {
                    Output.printInfo("Runtime selected: Spring Boot + Gradle");
                    getRequest += "&runtime=SPRING_GRADLE";
                } else if (testFile.contains("micronaut")) {
                    Output.printInfo("Runtime selected: Micronaut + Gradle");
                    getRequest += "&runtime=MICRONAUT_GRADLE";
                } else {
                    Output.printInfo("Runtime selected: Java + Gradle");
                    getRequest += "&runtime=JAVA_GRADLE";
                }
                getRequest = JavaScanner.javaDatabaseScanner(testFile, getRequest);
                getRequest = JavaScanner.javaAddOnScanner(testFile, getRequest);
            } else if (nodejsFile.exists()) {
                Output.printInfo("NodeJS project detected");
                testFile = FilesUtil.readString(nodejsFile.toPath());
                getRequest += "&runtime=NODEJS";
                getRequest = NodeJsScanner.nodejsDatabaseScanner(testFile, getRequest);
                getRequest = NodeJsScanner.nodejsAddOnScanner(testFile, getRequest);
            } else if (dotnetFile.isPresent()) {
                Output.printInfo(".NET project detected");
                testFile = FilesUtil.readString(dotnetFile.get());
                getRequest += "&runtime=DOTNET";
                getRequest = DotNetScanner.dotnetDatabaseScanner(testFile, getRequest);
                getRequest = DotNetScanner.dotnetAddOnScanner(testFile, getRequest);
            } else if (pythonFile.exists()) {
                Output.printInfo("Python project detected");
                testFile = FilesUtil.readString(pythonFile.toPath());
                getRequest += "&runtime=PYTHON";
                getRequest = PythonScanner.pythonDatabaseScanner(testFile, getRequest);
                getRequest = PythonScanner.pythonAddOnScanner(testFile, getRequest);
            } else {
                Output.printInfo("Runtime couldn't be detected, failing back to Docker");
                List<String> addOns = new ArrayList<>();
                GenericScanner.genericAddOnScanner(addOns);
                if (addOns.size() > 0) {
                    getRequest += "&addons=" + String.join(",", addOns);
                }
            }
        } catch (IOException e) {
            Output.printError("Error while reading files: " + e.getMessage());
            Output.printInfo("Project technology couldn't be detected, failing back to Docker");
        }
        if (vnet) {
            Output.printInfo("Network configuration selected: VNet");
            getRequest += "&network=VIRTUAL_NETWORK";
        }
        return getRequest;
    }

    private static Optional<Path> dotNetFinder(String workingDirectory) {
        try (Stream<Path> files = Files.walk(Paths.get(workingDirectory))) {
            return files
                    .filter(f -> f.getFileName().toString().endsWith(".csproj"))
                    .findFirst();
        } catch (IOException e) {
            Output.printError("Error while scanning directory: " + e.getMessage());
        }
        return Optional.empty();
    }
}
