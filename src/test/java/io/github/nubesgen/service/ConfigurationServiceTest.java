package io.github.nubesgen.service;

import io.github.nubesgen.configuration.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationServiceTest {

    ConfigurationService service = new ConfigurationService();

    @Test
    void checkDefaultConfiguration() {
        NubesgenConfiguration configuration =
                service.generateNubesgenConfiguration("TERRAFORM",
                        "DOCKER",
                        "APP_SERVICE",
                        "eastus",
                        "NONE",
                        false,
                        "",
                        "");

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.DOCKER, configuration.getRuntimeType());
        assertEquals(ApplicationType.APP_SERVICE, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.NONE, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.FREE, configuration.getDatabaseConfiguration().getTier());
        assertEquals(false, configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.PUBLIC, configuration.getNetworkConfiguration().getNetworkType());
    }

    @Test
    void checkEmptyConfiguration() {
        NubesgenConfiguration configuration =
                service.generateNubesgenConfiguration("",
                        "",
                        "",
                        "",
                        "",
                        false,
                        "",
                        "");

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.DOCKER, configuration.getRuntimeType());
        assertEquals(ApplicationType.APP_SERVICE, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.NONE, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.FREE, configuration.getDatabaseConfiguration().getTier());
        assertEquals(false, configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.PUBLIC, configuration.getNetworkConfiguration().getNetworkType());
    }

    @Test
    void checkPostgresqlDefaultOptions() {
        NubesgenConfiguration configuration =
                service.generateNubesgenConfiguration("TERRAFORM",
                        "DOCKER",
                        "APP_SERVICE",
                        "eastus",
                        "POSTGRESQL",
                        false,
                        "",
                        "");

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.DOCKER, configuration.getRuntimeType());
        assertEquals(ApplicationType.APP_SERVICE, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.POSTGRESQL, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.BASIC, configuration.getDatabaseConfiguration().getTier());
        assertEquals(false, configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.PUBLIC, configuration.getNetworkConfiguration().getNetworkType());
    }

    @Test
    void checkSpringCloudOnlySupportsSpring() {
        NubesgenConfiguration configuration =
                service.generateNubesgenConfiguration("TERRAFORM",
                        "DOCKER",
                        "SPRING_CLOUD",
                        "eastus",
                        "POSTGRESQL",
                        false,
                        "",
                        "");

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.SPRING, configuration.getRuntimeType());
        assertEquals(ApplicationType.SPRING_CLOUD, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.POSTGRESQL, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.BASIC, configuration.getDatabaseConfiguration().getTier());
        assertEquals(false, configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.PUBLIC, configuration.getNetworkConfiguration().getNetworkType());
    }

    @Test
    void updateDatabaseTierIfVnetIsSelected() {
        NubesgenConfiguration configuration =
                service.generateNubesgenConfiguration("TERRAFORM",
                        "DOCKER",
                        "APP_SERVICE",
                        "eastus",
                        "POSTGRESQL",
                        false,
                        "",
                        "VIRTUAL_NETWORK");

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.DOCKER, configuration.getRuntimeType());
        assertEquals(ApplicationType.APP_SERVICE, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.POSTGRESQL, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.GENERAL_PURPOSE, configuration.getDatabaseConfiguration().getTier());
        assertEquals(false, configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.VIRTUAL_NETWORK, configuration.getNetworkConfiguration().getNetworkType());
    }
}
