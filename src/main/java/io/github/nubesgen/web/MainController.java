package io.github.nubesgen.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nubesgen.configuration.*;
import io.github.nubesgen.service.CodeGeneratorService;
import io.github.nubesgen.service.TelemetryService;
import io.github.nubesgen.service.compression.CompressionService;
import io.github.nubesgen.service.compression.TarGzService;
import io.github.nubesgen.service.compression.ZipService;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class MainController {

    private final Logger log = LoggerFactory.getLogger(MainController.class);

    private final CodeGeneratorService codeGeneratorService;

    private final TarGzService tarGzService;

    private final ZipService zipService;

    private final ObjectMapper objectMapper;

    private final TelemetryService telemetryService;

    public MainController(
        CodeGeneratorService codeGeneratorService,
        TarGzService tarGzService,
        ZipService zipService,
        ObjectMapper objectMapper,
        TelemetryService telemetryService
    ) {
        this.codeGeneratorService = codeGeneratorService;
        this.tarGzService = tarGzService;
        this.zipService = zipService;
        this.objectMapper = objectMapper;
        this.telemetryService = telemetryService;
    }

    @GetMapping(value = "/{applicationName}.zip")
    public @ResponseBody ResponseEntity<byte[]> generateZipApplication(
        @PathVariable String applicationName,
        @RequestParam(defaultValue = "TERRAFORM") String iactool,
        @RequestParam(defaultValue = "DOCKER") String runtime,
        @RequestParam(defaultValue = "APP_SERVICE") String application,
        @RequestParam(defaultValue = "eastus") String region,
        @RequestParam(defaultValue = "NONE") String database,
        @RequestParam(defaultValue = "false") boolean gitops,
        @RequestParam(defaultValue = "") String addons,
        @RequestParam(defaultValue = "") String network
    ) {
        NubesgenConfiguration properties = generateNubesgenConfiguration(
            iactool,
            runtime,
            application,
            region,
            database,
            gitops,
            addons,
            network
        );
        return generateZipApplication(applicationName, properties);
    }

    @PostMapping("/{applicationName}.zip")
    public @ResponseBody ResponseEntity<byte[]> generateZipApplication(
        @PathVariable String applicationName,
        @RequestBody NubesgenConfiguration properties
    ) {
        properties.setApplicationName(applicationName);
        return this.generateApplication(properties, this.zipService);
    }

    @GetMapping(value = "/{applicationName}.tgz")
    public @ResponseBody ResponseEntity<byte[]> generateTgzApplication(
        @PathVariable String applicationName,
        @RequestParam(defaultValue = "TERRAFORM") String iactool,
        @RequestParam(defaultValue = "DOCKER") String runtime,
        @RequestParam(defaultValue = "APP_SERVICE") String application,
        @RequestParam(defaultValue = "eastus") String region,
        @RequestParam(defaultValue = "NONE") String database,
        @RequestParam(defaultValue = "false") boolean gitops,
        @RequestParam(defaultValue = "") String addons,
        @RequestParam(defaultValue = "") String network
    ) {
        NubesgenConfiguration properties = generateNubesgenConfiguration(
            iactool,
            runtime,
            application,
            region,
            database,
            gitops,
            addons,
            network
        );
        return generateTgzApplication(applicationName, properties);
    }

    @PostMapping("/{applicationName}.tgz")
    public @ResponseBody ResponseEntity<byte[]> generateTgzApplication(
        @PathVariable String applicationName,
        @RequestBody NubesgenConfiguration properties
    ) {
        properties.setApplicationName(applicationName);
        return this.generateApplication(properties, this.tarGzService);
    }

    private NubesgenConfiguration generateNubesgenConfiguration(
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
        if (iactool.equals(IaCTool.BICEP.name())) {
            properties.setIaCTool(IaCTool.BICEP);
        } else if (iactool.equals(IaCTool.PULUMI.name())) {
            properties.setIaCTool(IaCTool.PULUMI);
        } else {
            properties.setIaCTool(IaCTool.TERRAFORM);
        }
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
        if (network.startsWith(NetworkType.VIRTUAL_NETWORK.name())) {
            PublicEndpointType publicEndpointType;
            if (network.endsWith(PublicEndpointType.FRONTDOOR.name())) {
                publicEndpointType = PublicEndpointType.FRONTDOOR;
            } else {
                // Azure Application Gateway isn't supported yet, so we fall back to private network.
                publicEndpointType = PublicEndpointType.PRIVATE;
            }
            NetworkConfiguration networkConfiguration = new NetworkConfiguration(NetworkType.VIRTUAL_NETWORK, publicEndpointType);
            properties.setNetworkConfiguration(networkConfiguration);
        } else {
            properties.setNetworkConfiguration(new NetworkConfiguration(NetworkType.PUBLIC));
        }
        log.debug(
            "Application is of type: {} with tier: {}",
            properties.getApplicationConfiguration().getApplicationType(),
            properties.getApplicationConfiguration().getTier()
        );

        properties.setRegion(region);
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(DatabaseType.NONE, Tier.FREE);
        if (database.startsWith(DatabaseType.SQL_SERVER.name())) {
            databaseConfiguration = new DatabaseConfiguration(DatabaseType.SQL_SERVER, Tier.SERVERLESS);
            if (database.endsWith(Tier.GENERAL_PURPOSE.name())) {
                databaseConfiguration.setTier(Tier.GENERAL_PURPOSE);
            }
        } else if (database.startsWith(DatabaseType.MYSQL.name())) {
            databaseConfiguration = new DatabaseConfiguration(DatabaseType.MYSQL, Tier.BASIC);
            if (database.endsWith(Tier.GENERAL_PURPOSE.name())) {
                databaseConfiguration.setTier(Tier.GENERAL_PURPOSE);
            }
        } else if (database.startsWith(DatabaseType.POSTGRESQL.name())) {
            databaseConfiguration = new DatabaseConfiguration(DatabaseType.POSTGRESQL, Tier.BASIC);
            if (database.endsWith(Tier.GENERAL_PURPOSE.name())) {
                databaseConfiguration.setTier(Tier.GENERAL_PURPOSE);
            }
        }
        if (!databaseConfiguration.getDatabaseType().equals(DatabaseType.NONE)
                && !properties.getNetworkConfiguration().getNetworkType().equals(NetworkType.PUBLIC)) {

            log.debug("VNET configuration is requested, so the database configuration was updated to the general purpose tier.");
            databaseConfiguration.setTier(Tier.GENERAL_PURPOSE);
        }
        properties.setDatabaseConfiguration(databaseConfiguration);
        log.debug("Database is: {}", properties.getDatabaseConfiguration().getDatabaseType());
        if (gitops) {
            properties.setGitops(true);
        }
        log.debug("GitOps is: {}", gitops);
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
        return properties;
    }

    private ResponseEntity<byte[]> generateApplication(NubesgenConfiguration properties, CompressionService compressionService) {
        try {
            String jsonConfiguration = objectMapper.writeValueAsString(properties);
            log.info("Generating cloud configuration\n{}", jsonConfiguration);
            this.telemetryService.storeConfiguration(jsonConfiguration);
        } catch (JsonProcessingException e) {
            log.error("Nubesgen configuration could not be mapped to JSON", e);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ByteArrayOutputStream zippedApplication;
        try {
            Map<String, String> generatedFiles = this.codeGeneratorService.generateAzureConfiguration(properties);
            zippedApplication = compressionService.compressApplication(generatedFiles);
        } catch (Exception e) {
            log.error("Error generating application", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        byte[] out = zippedApplication.toByteArray();
        HttpHeaders responseHeaders = new HttpHeaders();
        if (compressionService.isZip()) {
            responseHeaders.add("content-disposition", "attachment; filename=" + properties.getApplicationName() + ".zip");
        } else {
            responseHeaders.add("content-disposition", "attachment; filename=" + properties.getApplicationName() + ".tgz");
        }
        responseHeaders.add("Content-Type", "application/octet-stream");
        responseHeaders.add("Content-Transfer-Encoding", "binary");
        responseHeaders.add("Content-Length", String.valueOf(out.length));
        stopWatch.stop();
        log.info("Generation finished in {}ms", stopWatch.getTotalTimeMillis());
        return new ResponseEntity<>(out, responseHeaders, HttpStatus.OK);
    }
}
