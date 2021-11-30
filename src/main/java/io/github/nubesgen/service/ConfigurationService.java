package io.github.nubesgen.service;

import io.github.nubesgen.configuration.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Validate the user configuration, depending on the constraints from Azure.
 */
@Service
public class ConfigurationService {

    private final Logger log = LoggerFactory.getLogger(ConfigurationService.class);

    public NubesgenConfiguration generateNubesgenConfiguration(
        String iactool,
        String runtime,
        String application,
        String region,
        String database,
        boolean gitops,
        String addons,
        String network
    ) {
        iactool = iactool.toUpperCase();
        runtime = runtime.toUpperCase();
        application = application.toUpperCase();
        database = database.toUpperCase();
        addons = addons.toUpperCase();
        network = network.toUpperCase();
        NubesgenConfiguration properties = new NubesgenConfiguration();
        configureRegion(properties, region);
        configureIac(properties, iactool);
        configureRuntime(properties, runtime, application);
        configureNetwork(properties, network);
        configureDatabase(properties, database);
        configureGitOps(properties, gitops);
        configureAddOns(properties, addons);
        return properties;
    }

    private void configureRegion(NubesgenConfiguration properties, String region) {
        if ("".equals(region)) {
            region = "eastus";
        }
        properties.setRegion(region);
    }

    void configureIac(NubesgenConfiguration properties, String iactool) {
        if (iactool.equals(IaCTool.BICEP.name())) {
            properties.setIaCTool(IaCTool.BICEP);
        } else if (iactool.equals(IaCTool.PULUMI.name())) {
            properties.setIaCTool(IaCTool.PULUMI);
        } else {
            properties.setIaCTool(IaCTool.TERRAFORM);
        }
    }

    void configureRuntime(NubesgenConfiguration properties, String runtime, String application) {
        if (runtime.equals(RuntimeType.DOTNET.name())) {
            properties.setRuntimeType(RuntimeType.DOTNET);
        } else if (runtime.equals(RuntimeType.JAVA.name())) {
            properties.setRuntimeType(RuntimeType.JAVA);
        } else if (runtime.equals(RuntimeType.JAVA_GRADLE.name())) {
            properties.setRuntimeType(RuntimeType.JAVA_GRADLE);
        } else if (runtime.equals(RuntimeType.SPRING.name())) {
            properties.setRuntimeType(RuntimeType.SPRING);
        } else if (runtime.equals(RuntimeType.SPRING_GRADLE.name())) {
            properties.setRuntimeType(RuntimeType.SPRING_GRADLE);
        } else if (runtime.equals(RuntimeType.QUARKUS.name())) {
            properties.setRuntimeType(RuntimeType.QUARKUS);
        } else if (runtime.equals(RuntimeType.QUARKUS_NATIVE.name())) {
            properties.setRuntimeType(RuntimeType.QUARKUS_NATIVE);
        } else if (runtime.equals(RuntimeType.NODEJS.name())) {
            properties.setRuntimeType(RuntimeType.NODEJS);
        } else if (runtime.equals(RuntimeType.DOCKER_SPRING.name())) {
            properties.setRuntimeType(RuntimeType.DOCKER_SPRING);
        } else {
            properties.setRuntimeType(RuntimeType.DOCKER);
        }
        if (application.startsWith(ApplicationType.FUNCTION.name())) {
            ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
            applicationConfiguration.setApplicationType(ApplicationType.FUNCTION);
            if (runtime.equals(RuntimeType.DOCKER.name()) || runtime.equals(RuntimeType.DOCKER_SPRING.name())) {
                log.debug("Docker is not supported for Functions, switching to Spring by default");
                properties.setRuntimeType(RuntimeType.SPRING);
            }
            if (application.endsWith(Tier.PREMIUM.name())) {
                applicationConfiguration.setTier(Tier.PREMIUM);
            } else {
                applicationConfiguration.setTier(Tier.CONSUMPTION);
            }
            properties.setApplicationConfiguration(applicationConfiguration);
        } else if (application.startsWith(ApplicationType.SPRING_CLOUD.name())) {
            ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
            applicationConfiguration.setApplicationType(ApplicationType.SPRING_CLOUD);
            if (!runtime.equals(RuntimeType.SPRING.name()) && !runtime.equals(RuntimeType.SPRING_GRADLE.name())) {
                log.debug("Azure Spring Cloud only supports the Spring runtime, switching to Spring by default");
                properties.setRuntimeType(RuntimeType.SPRING);
            }
            if (application.endsWith(Tier.BASIC.name())) {
                applicationConfiguration.setTier(Tier.BASIC);
            } else {
                applicationConfiguration.setTier(Tier.STANDARD);
            }
            properties.setApplicationConfiguration(applicationConfiguration);
        } else {
            ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
            applicationConfiguration.setApplicationType(ApplicationType.APP_SERVICE);
            if (application.endsWith(Tier.BASIC.name())) {
                applicationConfiguration.setTier(Tier.BASIC);
            } else if (application.endsWith(Tier.STANDARD.name())) {
                applicationConfiguration.setTier(Tier.STANDARD);
            } else {
                applicationConfiguration.setTier(Tier.FREE);
            }
            properties.setApplicationConfiguration(applicationConfiguration);
        }
        log.debug(
            "Application is of type: {} with tier: {}",
            properties.getApplicationConfiguration().getApplicationType(),
            properties.getApplicationConfiguration().getTier()
        );
    }

    void configureNetwork(NubesgenConfiguration properties, String network) {
        if (network.startsWith(NetworkType.VIRTUAL_NETWORK.name())) {
            NetworkConfiguration networkConfiguration = new NetworkConfiguration(NetworkType.VIRTUAL_NETWORK);
            properties.setNetworkConfiguration(networkConfiguration);
        } else {
            properties.setNetworkConfiguration(new NetworkConfiguration());
        }
    }

    void configureDatabase(NubesgenConfiguration properties, String database) {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(DatabaseType.NONE, Tier.FREE);
        if (database.startsWith(DatabaseType.SQL_SERVER.name())) {
            databaseConfiguration = new DatabaseConfiguration(DatabaseType.SQL_SERVER, Tier.SERVERLESS);
        } else if (database.startsWith(DatabaseType.MYSQL.name())) {
            databaseConfiguration = new DatabaseConfiguration(DatabaseType.MYSQL, Tier.BASIC);
            if (!properties.getNetworkConfiguration().getNetworkType().equals(NetworkType.PUBLIC)) {
                log.debug("VNET configuration is requested, so the database configuration was updated to the general purpose tier.");
                databaseConfiguration.setTier(Tier.GENERAL_PURPOSE);
            }
        } else if (database.startsWith(DatabaseType.POSTGRESQL.name())) {
            databaseConfiguration = new DatabaseConfiguration(DatabaseType.POSTGRESQL, Tier.BASIC);
            if (!properties.getNetworkConfiguration().getNetworkType().equals(NetworkType.PUBLIC)) {
                log.debug("VNET configuration is requested, so the database configuration was updated to the general purpose tier.");
                databaseConfiguration.setTier(Tier.GENERAL_PURPOSE);
            }
        }
        if (database.endsWith(Tier.GENERAL_PURPOSE.name())) {
            databaseConfiguration.setTier(Tier.GENERAL_PURPOSE);
        }
        properties.setDatabaseConfiguration(databaseConfiguration);
        log.debug("Database is: {}", properties.getDatabaseConfiguration().getDatabaseType());
    }

    void configureGitOps(NubesgenConfiguration properties, boolean gitops) {
        if (gitops) {
            properties.setGitops(true);
        }
        log.debug("GitOps is: {}", gitops);
    }

    void configureAddOns(NubesgenConfiguration properties, String addons) {
        if (!"".equals(addons)) {
            List<AddonConfiguration> addonConfigurations = new ArrayList<>();
            for (String addon : addons.split(",")) {
                log.debug("Configuring addon: {}", addon);
                if (addon.startsWith(AddonType.APPLICATION_INSIGHTS.name())) {
                    addonConfigurations.add(new AddonConfiguration(AddonType.APPLICATION_INSIGHTS, Tier.BASIC));
                } else if (addon.startsWith(AddonType.KEY_VAULT.name())) {
                    addonConfigurations.add(new AddonConfiguration(AddonType.KEY_VAULT, Tier.STANDARD));
                } else if (addon.startsWith(AddonType.REDIS.name())) {
                    addonConfigurations.add(new AddonConfiguration(AddonType.REDIS, Tier.BASIC));
                } else if (addon.startsWith(AddonType.STORAGE_BLOB.name())) {
                    addonConfigurations.add(new AddonConfiguration(AddonType.STORAGE_BLOB, Tier.BASIC));
                } else if (addon.startsWith(AddonType.COSMOSDB_MONGODB.name())) {
                    addonConfigurations.add(new AddonConfiguration(AddonType.COSMOSDB_MONGODB, Tier.FREE));
                }
            }
            properties.setAddons(addonConfigurations);
        }
    }
}
