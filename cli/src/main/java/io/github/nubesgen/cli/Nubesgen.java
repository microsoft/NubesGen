package io.github.nubesgen.cli;

import io.github.nubesgen.cli.subcommand.HealthCommand;
import io.github.nubesgen.cli.subcommand.ProjectnameCommand;
import io.github.nubesgen.cli.subcommand.DownloadCommand;
import io.github.nubesgen.cli.subcommand.GitopsCommand;
import io.github.nubesgen.cli.subcommand.ScanCommand;
import io.github.nubesgen.cli.util.Output;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(name = "nubesgen", mixinStandardHelpOptions = true,
        versionProvider = io.github.nubesgen.cli.util.VersionProvider.class,
        description = "CLI for NubesGen.com")
public class Nubesgen implements Callable<Integer> {

    @Option(names = {"-v", "--verbose"}, description = "Print verbose output")
    public static boolean verbose;

    @Option(names = {"-d", "--directory"}, description = "Directory in which the CLI will be executed")
    public static String directory;

    @Option(names = {"-x", "--development"}, description = "Development mode, this requires a local REST server running on http://127.0.0.1:8080")
    public static boolean development;

    @Override
    public Integer call() throws Exception {
        int exitCode = HealthCommand.configure();
        if (exitCode == 0) {
            String workingDirectory = Paths.get(".").toAbsolutePath().normalize().toString();
            if (directory != null) {
                workingDirectory = Paths.get(directory).toAbsolutePath().normalize().toString();
            }
            Output.printMessage("Working directory: " + workingDirectory);
            String projectName = ProjectnameCommand.projectName(workingDirectory);
            String getRequest = ScanCommand.scan(workingDirectory);
            int gitopsExitStatus = GitopsCommand.gitops(projectName);
            if (gitopsExitStatus == 0) {
                getRequest += "&gitops=true";
            }
            int downloadExitStatus = DownloadCommand.download(workingDirectory, projectName, getRequest);
            if (downloadExitStatus != 0) {
                Output.printError("NubesGen configuration failed! The configuration couldn't be downloaded from the server.");
                return downloadExitStatus;
            }
            Output.printTitle("NugesGen configuration finished");
            if (gitopsExitStatus == 0) {
                Output.printInfo("You can now save this configuration in Git:");
                Output.printMessage("git add . && git commit -m \"Configure GitOps\" && git push");
                Output.printInfo("To create a new environment, create a branch starting with \"env-\" and push it:");
                Output.printMessage("git checkout -b \"env-dev\" && git push --set-upstream origin \"env-dev\"");
            }
        }
        return exitCode;
    }

    public static void main(String... args) {
        AnsiConsole.systemInstall();
        Output.printTitle("NugesGen configuration starting");
        int exitCode = new CommandLine(new Nubesgen())
                .addSubcommand(new HealthCommand())
                .addSubcommand(new ProjectnameCommand())
                .addSubcommand(new ScanCommand())
                .addSubcommand(new GitopsCommand())
                .addSubcommand(new DownloadCommand())
                .execute(args);

        AnsiConsole.systemUninstall();
        System.exit(exitCode);
    }
}

