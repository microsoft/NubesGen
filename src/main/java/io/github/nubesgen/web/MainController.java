package io.github.nubesgen.web;

import io.github.nubesgen.configuration.NubesgenConfiguration;
import io.github.nubesgen.service.CodeGeneratorService;
import io.github.nubesgen.service.ZipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.Map;

@RestController
@RequestMapping("/")
public class MainController {

    private final Logger log = LoggerFactory.getLogger(MainController.class);

    private final CodeGeneratorService codeGeneratorService;

    private final ZipService zipService;

    public MainController(CodeGeneratorService codeGeneratorService, ZipService zipService) {
        this.codeGeneratorService = codeGeneratorService;
        this.zipService = zipService;
    }

    @GetMapping(value = "/nubesgen.zip")
    public @ResponseBody
    ResponseEntity<byte[]> generateDefaultApplication(HttpServletRequest request) {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        return generateApplication(properties, request);
    }

    @PostMapping("/nubesgen.zip")
    public @ResponseBody
    ResponseEntity<byte[]> generateApplication(@RequestBody NubesgenConfiguration properties, HttpServletRequest request) {
        log.info("Generating cloud configuration\n{}", properties);
        log.info("request {}", request);
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
