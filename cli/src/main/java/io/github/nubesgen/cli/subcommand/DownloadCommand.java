package io.github.nubesgen.cli.subcommand;

import io.github.nubesgen.cli.Nubesgen;
import io.github.nubesgen.cli.util.Output;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@CommandLine.Command(name = "download", description = "Download the NubesGen configuration")
public class DownloadCommand implements Callable<Integer> {

    @Option(names = {"-d", "--directory"}, description = "Directory in which the CLI will be executed")
    public static String directory;

    @Override
    public Integer call() {
        String workingDirectory = Paths.get(".").toAbsolutePath().normalize().toString();
        if (directory != null) {
            workingDirectory = Paths.get(directory).toAbsolutePath().normalize().toString();
        }
        return download(workingDirectory, "demo", "?application=APP_SERVICE.basic&runtime=DOCKER");
    }

    public static Integer download(String workingDirectory, String projectName, String getRequest) {
        Output.printTitle("Downloading the NubesGen configuration...");
        
        try {
            String server = "https://nubesgen.com";
            if (Nubesgen.development) {
                server = "http://localhost:8080";
            }
            URL url = (new URI(server + "/" + projectName + ".zip" + getRequest)).toURL();
            Output.printMessage("Downloading from: " + url);
            Files.copy(
                    url.openStream(),
                    Paths.get(workingDirectory + FileSystems.getDefault().getSeparator() + projectName + ".zip"),
                    StandardCopyOption.REPLACE_EXISTING);

            Output.printInfo("NubesGen configuration downloaded");
            Path source = Paths.get(workingDirectory + FileSystems.getDefault().getSeparator() + projectName + ".zip");
            Path target = Paths.get(workingDirectory);
            unzipFolder(source, target);
            Files.delete(source);
        } catch (IOException | URISyntaxException e) {
            Output.printError("Error: " + e.getMessage());
            return 1;
        }
        return 0;
    }

    public static void unzipFolder(Path source, Path target) throws IOException {
        Output.printMessage("Unzipping the NubesGen configuration file");
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                boolean isDirectory = zipEntry.getName().endsWith(File.separator);
                Path newPath = zipSlipProtect(zipEntry, target);
                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    Output.printMessage(newPath.toString());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (Exception e) {
            Output.printError("Error: " + e.getMessage());
        }
    }

    // protect zip slip attack
    public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
        throws IOException {

        // test zip slip vulnerability
        // Path targetDirResolved = targetDir.resolve("../../" + zipEntry.getName());
        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }
        return normalizePath;
    }
}
