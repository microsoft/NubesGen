package io.github.nubesgen.service;

public class CodeGeneratorProperties {

    private String resourceGroup;

    private String location;

    private String applicationName;

    public CodeGeneratorProperties() {
        this.resourceGroup = "nubesgen";
        this.location = "eastus";
        this.applicationName = "sampleNubesApplication";
    }

    public String getResourceGroup() {
        return resourceGroup;
    }

    public void setResourceGroup(String resourceGroup) {
        this.resourceGroup = resourceGroup;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String toString() {
        return "CodeGeneratorProperties{" +
                "resourceGroup='" + resourceGroup + '\'' +
                ", location='" + location + '\'' +
                ", applicationName='" + applicationName + '\'' +
                '}';
    }
}
