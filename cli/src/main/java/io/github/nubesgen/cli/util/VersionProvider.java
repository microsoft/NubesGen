package io.github.nubesgen.cli.util;

import java.util.Properties;

import picocli.CommandLine.IVersionProvider;

public class VersionProvider implements IVersionProvider {

    public String[] getVersion() {
        String version = "undefined";
        Properties properties = new Properties();
        try {
            properties.load(VersionProvider.class.getClassLoader().getResourceAsStream("git.properties"));
            version = properties.getProperty("git.build.version");
        } catch (Exception e) {
            Output.printError("Could not find version number - Error: " + e.getMessage());
        }
        return new String[] { "NubesGen CLI version " + version };
    }
}
