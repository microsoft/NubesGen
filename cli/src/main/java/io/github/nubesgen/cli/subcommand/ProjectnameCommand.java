package io.github.nubesgen.cli.subcommand;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.File;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.concurrent.Callable;

import io.github.nubesgen.cli.util.Output;

@CommandLine.Command(name = "projectname", description = "Generate a name for the current project")
public class ProjectnameCommand implements Callable<Integer> {

    @Option(names = {"-d", "--directory"}, description = "Directory in which the CLI will be executed")
    public static String directory;

    @Override
    public Integer call() {
        String workingDirectory = Paths.get(".").toAbsolutePath().normalize().toString();
        if (directory != null) {
            workingDirectory = Paths.get(directory).toAbsolutePath().normalize().toString();
        }
        Output.printTitle("Project name: " + projectName(workingDirectory));
        return 0;
    }

    public static String projectName(String workingDirectory) {
        Output.printTitle("Creating a name for the current project...");
        Output.printMessage("Current directory: " + workingDirectory);
        String projectName = workingDirectory.substring(workingDirectory.lastIndexOf(File.separator) + 1);
        projectName = projectName.replaceAll(" ", "-").replaceAll("_", "-");
        if (projectName.length() > 8) {
            projectName = projectName.substring(0, 8);
        }
        if (projectName == null || projectName.isEmpty()) {
            projectName = "demo";
        }
        DecimalFormat formater = new DecimalFormat("0000");
        String random1 = formater.format(Math.random() * (10000));
        String random2 = formater.format(Math.random() * (10000));
        projectName += "-" + random1 + "-" + random2;
        Output.printInfo("Project name: " + projectName);
        return projectName;
    }
}
