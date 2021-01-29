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

    @JsonProperty("application")
    private ApplicationConfiguration applicationConfiguration;

    @JsonProperty("database")
    private DatabaseConfiguration databaseConfiguration;

    @JsonProperty("addons")
    private List<AddonConfiguration> addons = new ArrayList<>();

    public NubesgenConfiguration() {
        this.region = "eastus";
        this.applicationName = "sample-nubes-application";
        this.applicationConfiguration = new ApplicationConfiguration();
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

    public ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    public void setDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    public List<AddonConfiguration> getAddons() {
        return addons;
    }

    public void setAddons(List<AddonConfiguration> addons) {
        this.addons = addons;
    }

    @JsonIgnore
    public boolean isApplicationTypeAppService() {
        return ApplicationType.APP_SERVICE.equals(this.getApplicationConfiguration().getApplicationType());
    }

    @JsonIgnore
    public boolean isApplicationTierBasic() {
        return Tier.BASIC.equals(this.getApplicationConfiguration().getTier());
    }

    @JsonIgnore
    public boolean isApplicationTierStandard() {
        return Tier.STANDARD.equals(this.getApplicationConfiguration().getTier());
    }

    @JsonIgnore
    public boolean isApplicationTypeFunction() {
        return ApplicationType.FUNCTION.equals(this.getApplicationConfiguration().getApplicationType());
    }

    @JsonIgnore
    public boolean isDatabaseTypeNone() {
        return DatabaseType.NONE.equals(this.databaseConfiguration.getDatabaseType());
    }

    @JsonIgnore
    public boolean isDatabaseTypeSqlServer() {
        return DatabaseType.SQL_SERVER.equals(this.databaseConfiguration.getDatabaseType());
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
    public boolean isAddonRedis() {
        return this.getAddons().stream()
                .anyMatch(addon -> AddonType.REDIS.equals(addon.getAddonType()));
    }

    @JsonIgnore
    public boolean isAddonStorageBlob() {
        return this.getAddons().stream()
                .anyMatch(addon -> AddonType.STORAGE_BLOB.equals(addon.getAddonType()));
    }

    @JsonIgnore
    public boolean isAddonCosmosdbMongodb() {
        return this.getAddons().stream()
                .anyMatch(addon -> AddonType.COSMOSDB_MONGODB.equals(addon.getAddonType()));
    }

    @Override
    public String toString() {
        return "NubesgenConfiguration{" +
                "date=" + date +
                ", region='" + region + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", applicationConfiguration=" + applicationConfiguration +
                ", databaseConfiguration=" + databaseConfiguration +
                ", addons=" + addons +
                '}';
    }
}
