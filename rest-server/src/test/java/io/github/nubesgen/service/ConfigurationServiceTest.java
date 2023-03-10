package io.github.nubesgen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.github.nubesgen.configuration.*;
import org.junit.jupiter.api.Test;

public class ConfigurationServiceTest {

    ConfigurationService service = new ConfigurationService();

    @Test
    void checkDefaultConfiguration() {
        NubesgenConfiguration configuration = service.generateNubesgenConfiguration(
                "",
            "TERRAFORM",
            "DOCKER",
            "APP_SERVICE",
            "eastus",
            "NONE",
            false,
            "",
            ""
        );

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.DOCKER, configuration.getRuntimeType());
        assertEquals(ApplicationType.APP_SERVICE, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.NONE, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.FREE, configuration.getDatabaseConfiguration().getTier());
        assertFalse(configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.PUBLIC, configuration.getNetworkConfiguration().getNetworkType());
    }

    @Test
    void checkEmptyConfiguration() {
        NubesgenConfiguration configuration = service.generateNubesgenConfiguration("", "", "", "", "", "", false, "", "");

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.DOCKER, configuration.getRuntimeType());
        assertEquals(ApplicationType.APP_SERVICE, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.NONE, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.FREE, configuration.getDatabaseConfiguration().getTier());
        assertFalse(configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.PUBLIC, configuration.getNetworkConfiguration().getNetworkType());
    }

    @Test
    void checkPostgresqlDefaultOptions() {
        NubesgenConfiguration configuration = service.generateNubesgenConfiguration(
                "",
            "TERRAFORM",
            "DOCKER",
            "APP_SERVICE",
            "eastus",
            "POSTGRESQL",
            false,
            "",
            ""
        );

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.DOCKER, configuration.getRuntimeType());
        assertEquals(ApplicationType.APP_SERVICE, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.POSTGRESQL, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.BASIC, configuration.getDatabaseConfiguration().getTier());
        assertFalse(configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.PUBLIC, configuration.getNetworkConfiguration().getNetworkType());
    }

    @Test
    void checkSpringAppsOnlySupportsSpring() {
        NubesgenConfiguration configuration = service.generateNubesgenConfiguration(
                "",
            "TERRAFORM",
            "DOCKER",
            "SPRING_APPS",
            "eastus",
            "POSTGRESQL",
            false,
            "",
            ""
        );

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.SPRING, configuration.getRuntimeType());
        assertEquals(ApplicationType.SPRING_APPS, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.POSTGRESQL, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.BASIC, configuration.getDatabaseConfiguration().getTier());
        assertFalse(configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.PUBLIC, configuration.getNetworkConfiguration().getNetworkType());
    }

    @Test
    void dontUpdateDatabaseTierIfVnetIsSelected() {
        NubesgenConfiguration configuration = service.generateNubesgenConfiguration(
                "",
            "TERRAFORM",
            "DOCKER",
            "APP_SERVICE",
            "eastus",
            "POSTGRESQL",
            false,
            "",
            "VIRTUAL_NETWORK"
        );

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.DOCKER, configuration.getRuntimeType());
        assertEquals(ApplicationType.APP_SERVICE, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.POSTGRESQL, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.BASIC, configuration.getDatabaseConfiguration().getTier());
        assertFalse(configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.VIRTUAL_NETWORK, configuration.getNetworkConfiguration().getNetworkType());
    }

    @Test
    void updateAppsServiceTierIfVnetIsSelected() {
        NubesgenConfiguration configuration = service.generateNubesgenConfiguration(
                "",
                "TERRAFORM",
                "DOCKER",
                "APP_SERVICE",
                "eastus",
                "POSTGRESQL",
                false,
                "",
                "VIRTUAL_NETWORK"
        );

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.DOCKER, configuration.getRuntimeType());
        assertEquals(ApplicationType.APP_SERVICE, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals(Tier.STANDARD, configuration.getApplicationConfiguration().getTier());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.POSTGRESQL, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.BASIC, configuration.getDatabaseConfiguration().getTier());
        assertFalse(configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.VIRTUAL_NETWORK, configuration.getNetworkConfiguration().getNetworkType());
    }

    @Test
    void updateFunctionTierIfVnetIsSelected() {
        NubesgenConfiguration configuration = service.generateNubesgenConfiguration(
                "",
            "TERRAFORM",
            "SPRING",
            "FUNCTION",
            "eastus",
            "POSTGRESQL",
            false,
            "",
            "VIRTUAL_NETWORK"
        );

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.SPRING, configuration.getRuntimeType());
        assertEquals(ApplicationType.FUNCTION, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals(Tier.PREMIUM, configuration.getApplicationConfiguration().getTier());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.POSTGRESQL, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.BASIC, configuration.getDatabaseConfiguration().getTier());
        assertFalse(configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.VIRTUAL_NETWORK, configuration.getNetworkConfiguration().getNetworkType());
    }

    @Test
    void updateSpringAppsTierIfVnetIsSelected() {
        NubesgenConfiguration configuration = service.generateNubesgenConfiguration(
                "",
                "TERRAFORM",
                "SPRING",
                "SPRING_APPS.BASIC",
                "eastus",
                "POSTGRESQL",
                false,
                "",
                "VIRTUAL_NETWORK"
        );

        assertEquals(IaCTool.TERRAFORM, configuration.getIaCTool());
        assertEquals(RuntimeType.SPRING, configuration.getRuntimeType());
        assertEquals(ApplicationType.SPRING_APPS, configuration.getApplicationConfiguration().getApplicationType());
        assertEquals(Tier.STANDARD, configuration.getApplicationConfiguration().getTier());
        assertEquals("eastus", configuration.getRegion());
        assertEquals(DatabaseType.POSTGRESQL, configuration.getDatabaseConfiguration().getDatabaseType());
        assertEquals(Tier.BASIC, configuration.getDatabaseConfiguration().getTier());
        assertFalse(configuration.isGitops());
        assertEquals(0, configuration.getAddons().size());
        assertEquals(NetworkType.VIRTUAL_NETWORK, configuration.getNetworkConfiguration().getNetworkType());
    }
}
