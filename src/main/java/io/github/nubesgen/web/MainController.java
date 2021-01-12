package io.github.nubesgen.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nubesgen.configuration.NubesgenConfiguration;
import io.github.nubesgen.service.CodeGeneratorService;
import io.github.nubesgen.service.TelemetryService;
import io.github.nubesgen.service.ZipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@RestController
@RequestMapping("/")
public class MainController {

    private final Logger log = LoggerFactory.getLogger(MainController.class);

    private final CodeGeneratorService codeGeneratorService;

    private final ZipService zipService;

    private final ObjectMapper objectMapper;

    @Autowired(required = false)
    private TelemetryService telemetryService;

    public MainController(CodeGeneratorService codeGeneratorService, ZipService zipService, ObjectMapper objectMapper) {
        this.codeGeneratorService = codeGeneratorService;
        this.zipService = zipService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/nubesgen.zip")
    public @ResponseBody
    ResponseEntity<byte[]> generateDefaultApplication() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        return generateApplication(properties);
    }

    @PostMapping("/nubesgen.zip")
    public @ResponseBody
    ResponseEntity<byte[]> generateApplication(@RequestBody NubesgenConfiguration properties) {
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
            zippedApplication = this.zipService.zipApplication(generatedFiles);
        } catch (Exception e) {
            log.error("Error generating application", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        byte[] out = zippedApplication.toByteArray();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=nubesgen.zip");
        responseHeaders.add("Content-Type", "application/octet-stream");
        responseHeaders.add("Content-Transfer-Encoding", "binary");
        responseHeaders.add("Content-Length", String.valueOf(out.length));
        stopWatch.stop();
        log.info("Generation finished in {}ms", stopWatch.getTotalTimeMillis());
        return new ResponseEntity<>(out, responseHeaders, HttpStatus.OK);
    }
}
