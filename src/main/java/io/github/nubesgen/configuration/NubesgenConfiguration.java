package io.github.nubesgen.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NubesgenConfiguration {

    private String location;

    private String applicationName;

    @JsonProperty("database")
    private DatabaseConfiguration databaseConfiguration;

    public NubesgenConfiguration() {
        this.location = "eastus";
        this.applicationName = "sampleNubesApplication";
        this.databaseConfiguration = new DatabaseConfiguration();
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

    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    public void setDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    public boolean isDatabaseTypeNone() {
        return DatabaseType.NONE.equals(this.databaseConfiguration.getDatabaseType());
    }

    public boolean isDatabaseTypeMysql() {
        return DatabaseType.MYSQL.equals(this.databaseConfiguration.getDatabaseType());
    }

    public boolean isDatabaseTypePostgresql() {
        return DatabaseType.POSTGRESQL.equals(this.databaseConfiguration.getDatabaseType());
    }

    @Override
    public String toString() {
        return "NubesgenConfiguration{" +
                "applicationName='" + applicationName + '\'' +
                ", location='" + location + '\'' +
                ", database=" + databaseConfiguration +
                '}';
    }
}
