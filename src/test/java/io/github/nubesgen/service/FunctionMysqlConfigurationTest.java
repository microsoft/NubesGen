package io.github.nubesgen.service;

import io.github.nubesgen.configuration.NubesgenConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests NubesGen with the default options.
 */
@SpringBootTest
class DefaultConfigurationTest {

    private final Logger log = LoggerFactory.getLogger(DefaultConfigurationTest.class);
    private static final NubesgenConfiguration properties = new NubesgenConfiguration();
    private final CodeGeneratorService codeGeneratorService;
    private final TemplateListService templateListService;

    @Autowired
    public DefaultConfigurationTest(CodeGeneratorService codeGeneratorService, TemplateListService templateListService) {
        this.codeGeneratorService = codeGeneratorService;
        this.templateListService = templateListService;
    }

    @BeforeAll
    public static void init() {
        properties.setApplicationName("nubesgen-testapp");
        properties.setRegion("westeurope");
    }

    @Test
    void generateDefaultConfiguration() throws IOException {
        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);
        int templatesSize = this.templateListService.listMainTemplates().size() +
                this.templateListService.listMysqlTemplates().size();

        assertEquals(templatesSize, configuration.size());
        for (String filename : templateListService.listMainTemplates()) {
            this.generateAndTestOneFile(filename);
        }
        for (String filename : templateListService.listMysqlTemplates()) {
            this.generateAndTestOneFile(filename);
        }
    }

    private void generateAndTestOneFile(String filename) throws IOException {
        log.info("Validating {}", filename);
        String result = this.codeGeneratorService.generateFile(filename, properties);
        File testFile = new ClassPathResource("nubesgen/default/" + filename).getFile();
        String test = new String(
                Files.readAllBytes(testFile.toPath()));

        assertEquals(test, result);
    }
}
