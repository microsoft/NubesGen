package io.github.nubesgen.web;

import io.github.nubesgen.service.CodeGeneratorProperties;
import io.github.nubesgen.service.CodeGeneratorService;
import io.github.nubesgen.service.ZipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    ResponseEntity<byte[]> generateApplication() {

        //TODO use URL parameters
        CodeGeneratorProperties properties = new CodeGeneratorProperties();
        properties.setResourceGroup("nubesgen");
        properties.setApplicationName("sampleNubesApplication");
        properties.setLocation("westeurope");

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
        return new ResponseEntity<>(out, responseHeaders, HttpStatus.OK);
    }
}
