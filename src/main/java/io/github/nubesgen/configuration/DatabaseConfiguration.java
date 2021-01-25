package io.github.nubesgen.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseConfiguration {

    @JsonProperty("type")
    private DatabaseType databaseType;

    @JsonProperty("size")
    private ConfigurationSize configurationSize;

    public DatabaseConfiguration() {
        this.databaseType = DatabaseType.NONE;
        this.configurationSize = ConfigurationSize.FREE;
    }

    public DatabaseConfiguration(DatabaseType databaseType, ConfigurationSize configurationSize) {
        this.databaseType = databaseType;
        this.configurationSize = configurationSize;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public ConfigurationSize getConfigurationSize() {
        return configurationSize;
    }

    public void setConfigurationSize(ConfigurationSize configurationSize) {
        this.configurationSize = configurationSize;
    }

    @Override
    public String toString() {
        return "DatabaseConfiguration{" +
                "databaseType=" + databaseType +
                ", configurationSize=" + configurationSize +
                '}';
    }
}
