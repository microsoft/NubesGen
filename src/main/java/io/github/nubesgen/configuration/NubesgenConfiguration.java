package io.github.nubesgen.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class NubesgenConfiguration {

    public static final String DEFAULT_APPLICATION_NAME = "demo";

    private final Date date = new Date();

    private String region;

    private String applicationName;

    @JsonProperty("runtime")
    private RuntimeType runtimeType;

    @JsonProperty("application")
    private ApplicationConfiguration applicationConfiguration;

    @JsonProperty("database")
    private DatabaseConfiguration databaseConfiguration;

    @JsonProperty("gitops")
    private boolean gitops;

    @JsonProperty("addons")
    private List<AddonConfiguration> addons = new ArrayList<>();

    public NubesgenConfiguration() {
        this.region = "eastus";
        this.applicationName = DEFAULT_APPLICATION_NAME;
        this.runtimeType = RuntimeType.DOCKER;
        this.applicationConfiguration = new ApplicationConfiguration();
        this.databaseConfiguration = new DatabaseConfiguration();
        this.gitops = false;
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

    @JsonIgnore
    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        Random rand = new Random();

        if (applicationName.equals(DEFAULT_APPLICATION_NAME)) {
            DecimalFormat formater = new DecimalFormat("0000");
            String random1 = formater.format(Math.random() * (10000));
            String random2 = formater.format(Math.random() * (10000));
            String random3 = formater.format(Math.random() * (10000));
            String random4 = formater.format(Math.random() * (10000));
            this.applicationName =
                    DEFAULT_APPLICATION_NAME + "-" + random1 + "-" + random2 + "-" + random3 + "-" + random4;

        } else {
            this.applicationName = applicationName;
        }
    }

    @JsonIgnore
    public String getContainerRegistry() {
        // This has the same algorithm as the Terraform template which manages the Azure Container Registry
        String containerRegistryWithNoHyphens = applicationName.replace("-", "");
        // the maxLength doesn't include the environment, as we can't know it from here
        // so this will probably won't be correct for longer names, when using an environment
        int maxLength = (containerRegistryWithNoHyphens.length() < 46) ? containerRegistryWithNoHyphens.length() : 46;
        return containerRegistryWithNoHyphens.replace("-", "").substring(0, maxLength);
    }

    public RuntimeType getRuntimeType() {
        return runtimeType;
    }

    public void setRuntimeType(RuntimeType runtimeType) {
        this.runtimeType = runtimeType;
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

    public boolean isGitops() {
        return gitops;
    }

    public void setGitops(boolean gitops) {
        this.gitops = gitops;
    }

    public List<AddonConfiguration> getAddons() {
        return addons;
    }

    public void setAddons(List<AddonConfiguration> addons) {
        this.addons = addons;
    }

    @JsonIgnore
    public boolean isRuntimeDocker() {
        return RuntimeType.DOCKER.equals(this.getRuntimeType()) ||
                RuntimeType.DOCKER_SPRING.equals(this.getRuntimeType());
    }

    @JsonIgnore
    public boolean isRuntimeJava() {
        return RuntimeType.SPRING.equals(this.getRuntimeType()) ||
                RuntimeType.SPRING_GRADLE.equals(this.getRuntimeType()) ||
                RuntimeType.JAVA.equals(this.getRuntimeType()) ||
                RuntimeType.JAVA_GRADLE.equals(this.getRuntimeType());
    }

    @JsonIgnore
    public boolean isRuntimeSpring() {
        return RuntimeType.SPRING.equals(this.getRuntimeType()) ||
                RuntimeType.SPRING_GRADLE.equals(this.getRuntimeType()) ||
                RuntimeType.DOCKER_SPRING.equals(this.getRuntimeType());
    }

    @JsonIgnore
    public boolean isRuntimeMaven() {
        return RuntimeType.SPRING.equals(this.getRuntimeType()) ||
                RuntimeType.JAVA.equals(this.getRuntimeType());
    }

    @JsonIgnore
    public boolean isRuntimeGradle() {
        return RuntimeType.SPRING_GRADLE.equals(this.getRuntimeType()) ||
                RuntimeType.JAVA_GRADLE.equals(this.getRuntimeType());
    }

    @JsonIgnore
    public boolean isRuntimeDotnet() {
        return RuntimeType.DOTNET.equals(this.getRuntimeType());
    }

    @JsonIgnore
    public boolean isRuntimeNodejs() {
        return RuntimeType.NODEJS.equals(this.getRuntimeType());
    }

    @JsonIgnore
    public boolean isApplicationTypeAppService() {
        return ApplicationType.APP_SERVICE.equals(this.getApplicationConfiguration().getApplicationType());
    }

    @JsonIgnore
    public boolean isApplicationTierFree() {
        return Tier.FREE.equals(this.getApplicationConfiguration().getTier());
    }

    @JsonIgnore
    public boolean isApplicationTierBasic() {
        return Tier.BASIC.equals(this.getApplicationConfiguration().getTier());
    }

    @JsonIgnore
    public boolean isApplicationTierConsumption() {
        return Tier.CONSUMPTION.equals(this.getApplicationConfiguration().getTier());
    }

    @JsonIgnore
    public boolean isApplicationTierStandard() {
        return Tier.STANDARD.equals(this.getApplicationConfiguration().getTier());
    }

    @JsonIgnore
    public boolean isApplicationTierPremium() {
        return Tier.PREMIUM.equals(this.getApplicationConfiguration().getTier());
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
    public boolean isDatabaseTierBasic() {
        return Tier.BASIC.equals(this.getDatabaseConfiguration().getTier());
    }

    @JsonIgnore
    public boolean isDatabaseTierServerless() {
        return Tier.SERVERLESS.equals(this.getDatabaseConfiguration().getTier());
    }

    @JsonIgnore
    public boolean isDatabaseTierGeneralPurpose() {
        return Tier.GENERAL_PURPOSE.equals(this.getDatabaseConfiguration().getTier());
    }

    @JsonIgnore
    public boolean isAddonRedis() {
        return this.getAddons().stream()
                .anyMatch(addon -> AddonType.REDIS.equals(addon.getAddonType()));
    }

    @JsonIgnore
    public boolean isAddonApplicationInsights() {
        return this.getAddons().stream()
                .anyMatch(addon -> AddonType.APPLICATION_INSIGHTS.equals(addon.getAddonType()));
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
                "region='" + region + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", runtimeType=" + runtimeType +
                ", applicationConfiguration=" + applicationConfiguration +
                ", databaseConfiguration=" + databaseConfiguration +
                ", addons=" + addons +
                '}';
    }
}
