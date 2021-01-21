package io.github.nubesgen.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NubesgenConfiguration {

    private Date date = new Date();

    private String region;

    private String applicationName;

    @JsonProperty("database")
    private DatabaseConfiguration databaseConfiguration;

    @JsonProperty("addOns")
    private List<AddOnConfiguration> addOns = new ArrayList<>();

    public NubesgenConfiguration() {
        this.region = "eastus";
        this.applicationName = "sample-nubes-application";
        this.databaseConfiguration = new DatabaseConfiguration();
    }

    public Date getDate() {
        return date;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public List<AddOnConfiguration> getAddOns() {
        return addOns;
    }

    public void setAddOns(List<AddOnConfiguration> addOns) {
        this.addOns = addOns;
    }

    @JsonIgnore
    public boolean isDatabaseTypeNone() {
        return DatabaseType.NONE.equals(this.databaseConfiguration.getDatabaseType());
    }

    @JsonIgnore
    public boolean isDatabaseTypeMysql() {
        return DatabaseType.MYSQL.equals(this.databaseConfiguration.getDatabaseType());
    }

    @JsonIgnore
    public boolean isDatabaseTypePostgresql() {
        return DatabaseType.POSTGRESQL.equals(this.databaseConfiguration.getDatabaseType());
    }

    @JsonIgnore
    public boolean isAddOnStorageBlob() {
        return this.getAddOns().stream()
                .anyMatch(addOn -> AddOnType.STORAGE_BLOB.equals(addOn.getAddOnType()));
    }

    @JsonIgnore
    public boolean isAddOnRedis() {
        return this.getAddOns().stream()
                .anyMatch(addOn -> AddOnType.REDIS.equals(addOn.getAddOnType()));
    }

    @Override
    public String toString() {
        return "NubesgenConfiguration{" +
                "date=" + date +
                ", region='" + region + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", databaseConfiguration=" + databaseConfiguration +
                ", addOns=" + addOns +
                '}';
    }
}
