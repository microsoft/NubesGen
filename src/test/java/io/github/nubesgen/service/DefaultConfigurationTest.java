package io.github.nubesgen.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

    private static final CodeGeneratorProperties properties = new CodeGeneratorProperties();
    private final CodeGeneratorService codeGeneratorService;
    private final TemplateListService templateListService;

    @Autowired
    public DefaultConfigurationTest(CodeGeneratorService codeGeneratorService, TemplateListService templateListService) {
        this.codeGeneratorService = codeGeneratorService;
        this.templateListService = templateListService;
    }

    @BeforeAll
    public static void init() {
        properties.setResourceGroup("test-resource-group");
        properties.setApplicationName("testapp");
        properties.setLocation("westeurope");
    }

    @Test
    void generateAzureConfiguration() {
        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);
        assertEquals(this.templateListService.listAzureTemplates().size(), configuration.size());
    }

    @Test
    void generateFile() throws IOException {
        String result = this.codeGeneratorService.generateFile("terraform/variables.tf", properties);
        File testFile = new ClassPathResource("nubesgen/default/terraform/variables.tf").getFile();
        String test = new String(
                Files.readAllBytes(testFile.toPath()));

        assertEquals(test, result);
    }
}
