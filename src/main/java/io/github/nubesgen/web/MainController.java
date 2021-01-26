package io.github.nubesgen.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nubesgen.configuration.*;
import io.github.nubesgen.service.CodeGeneratorService;
import io.github.nubesgen.service.TelemetryService;
import io.github.nubesgen.service.compression.CompressionService;
import io.github.nubesgen.service.compression.TarGzService;
import io.github.nubesgen.service.compression.ZipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class MainController {

    private final Logger log = LoggerFactory.getLogger(MainController.class);

    private final CodeGeneratorService codeGeneratorService;

    private final TarGzService tarGzService;

    private final ZipService zipService;

    private final ObjectMapper objectMapper;

    @Autowired(required = false)
    private TelemetryService telemetryService;

    public MainController(CodeGeneratorService codeGeneratorService, TarGzService tarGzService, ZipService zipService, ObjectMapper objectMapper) {
        this.codeGeneratorService = codeGeneratorService;
        this.tarGzService = tarGzService;
        this.zipService = zipService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/{applicationName}.zip")
    public @ResponseBody
    ResponseEntity<byte[]> generateZipApplication(@PathVariable String applicationName,
                                                  @RequestParam(defaultValue = "APP_SERVICE") String type,
                                                  @RequestParam(defaultValue = "eastus") String region,
                                                  @RequestParam(defaultValue = "NONE") String database,
                                                  @RequestParam(defaultValue = "") String addOns) {

        NubesgenConfiguration properties = generateNubesgenConfiguration(type, region, database, addOns);
        return generateZipApplication(applicationName, properties);
    }



    @PostMapping("/{applicationName}.zip")
    public @ResponseBody
    ResponseEntity<byte[]> generateZipApplication(@PathVariable String applicationName,
                                                  @RequestBody NubesgenConfiguration properties) {

        properties.setApplicationName(applicationName);
        return this.generateApplication(properties, this.zipService);
    }

    @GetMapping(value = "/{applicationName}.tgz")
    public @ResponseBody
    ResponseEntity<byte[]> generateTgzApplication(@PathVariable String applicationName,
                                                  @RequestParam(defaultValue = "APP_SERVICE") String type,
                                                  @RequestParam(defaultValue = "eastus") String region,
                                                  @RequestParam(defaultValue = "NONE") String database,
                                                  @RequestParam(defaultValue = "") String addOns) {

        NubesgenConfiguration properties = generateNubesgenConfiguration(type, region, database, addOns);
        return generateTgzApplication(applicationName, properties);
    }

    @PostMapping("/{applicationName}.tgz")
    public @ResponseBody
    ResponseEntity<byte[]> generateTgzApplication(@PathVariable String applicationName,
                                                  @RequestBody NubesgenConfiguration properties) {

        properties.setApplicationName(applicationName);
        return this.generateApplication(properties, this.tarGzService);
    }

    private NubesgenConfiguration generateNubesgenConfiguration(String type, String region, String database, String addOns) {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        if (type.equals(ApplicationType.FUNCTION)) {
            properties.setApplicationType(ApplicationType.FUNCTION);
        } else {
            properties.setApplicationType(ApplicationType.APP_SERVICE);
        }
        log.debug("Application is of type: {}", properties.getApplicationType());
        properties.setRegion(region);
        if ("".equals(database) || database.startsWith(DatabaseType.NONE.name())) {
            properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.NONE, ConfigurationSize.FREE));
        } else if (database.startsWith(DatabaseType.SQL_SERVER.name())) {
            properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.SQL_SERVER, ConfigurationSize.BASIC));
        } else if (database.startsWith(DatabaseType.MYSQL.name())) {
            properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.MYSQL, ConfigurationSize.BASIC));
        } else if (database.startsWith(DatabaseType.POSTGRESQL.name())) {
            properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.POSTGRESQL, ConfigurationSize.BASIC));
        }
        log.debug("Database is: {}", properties.getDatabaseConfiguration().getDatabaseType());
        if (addOns != null && !"".equals(addOns)) {
            List<AddOnConfiguration> addOnConfigurations = new ArrayList<>();
            for (String addOn : addOns.split(",")) {
                log.debug("Configuring addOn: {}", addOn);
                if (addOn.startsWith(AddOnType.REDIS.name())) {
                    addOnConfigurations.add(new AddOnConfiguration(AddOnType.REDIS, ConfigurationSize.BASIC));
                } else if (addOn.startsWith(AddOnType.STORAGE_BLOB.name())) {
                    addOnConfigurations.add(new AddOnConfiguration(AddOnType.STORAGE_BLOB, ConfigurationSize.BASIC));
                }
            }
            properties.setAddOns(addOnConfigurations);
        }
        return properties;
    }

    private ResponseEntity<byte[]> generateApplication(NubesgenConfiguration properties, CompressionService compressionService) {
        try {
            String jsonConfiguration = objectMapper.writeValueAsString(properties);
            log.info("Generating cloud configuration\n{}", jsonConfiguration);
            if (telemetryService != null) {
                this.telemetryService.storeConfiguration(jsonConfiguration);
            }
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
