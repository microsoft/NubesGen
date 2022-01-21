package io.github.nubesgen.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nubesgen.configuration.NubesgenConfiguration;
import io.github.nubesgen.service.CodeGeneratorService;
import io.github.nubesgen.service.ConfigurationService;
import io.github.nubesgen.service.TelemetryService;
import io.github.nubesgen.service.compression.CompressionService;
import io.github.nubesgen.service.compression.TarGzService;
import io.github.nubesgen.service.compression.ZipService;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class MainController {

    public static final String DEFAULT_REGION = "eastus";

    private final Logger log = LoggerFactory.getLogger(MainController.class);

    private final CodeGeneratorService codeGeneratorService;

    private final ConfigurationService configurationService;

    private final TarGzService tarGzService;

    private final ZipService zipService;

    private final ObjectMapper objectMapper;

    private final TelemetryService telemetryService;

    public MainController(
        CodeGeneratorService codeGeneratorService,
        ConfigurationService configurationService,
        TarGzService tarGzService,
        ZipService zipService,
        ObjectMapper objectMapper,
        TelemetryService telemetryService
    ) {
        this.codeGeneratorService = codeGeneratorService;
        this.configurationService = configurationService;
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
        @RequestParam(defaultValue = DEFAULT_REGION) String region,
        @RequestParam(defaultValue = "NONE") String database,
        @RequestParam(defaultValue = "false") boolean gitops,
        @RequestParam(defaultValue = "") String addons,
        @RequestParam(defaultValue = "") String network,
        HttpServletRequest request
    ) {
        NubesgenConfiguration properties = configurationService.generateNubesgenConfiguration(
            getNubesgenUrl(request),
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
        @RequestParam(defaultValue = DEFAULT_REGION) String region,
        @RequestParam(defaultValue = "NONE") String database,
        @RequestParam(defaultValue = "false") boolean gitops,
        @RequestParam(defaultValue = "") String addons,
        @RequestParam(defaultValue = "") String network,
        HttpServletRequest request
    ) {
        NubesgenConfiguration properties = configurationService.generateNubesgenConfiguration(
            getNubesgenUrl(request),
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

    /**
     * Calculate the full URL used to generate the current project.
     */
    private String getNubesgenUrl(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        String parameters = request.getQueryString();
        if (parameters != null) {
            url += "?" + parameters;
        }
        return url;
    }
}
