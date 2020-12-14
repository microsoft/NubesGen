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

@SpringBootTest
class CodeGeneratorServiceTest {

    private final CodeGeneratorService codeGeneratorService;

    private final TemplateListService templateListService;

    @Autowired
    public CodeGeneratorServiceTest(CodeGeneratorService codeGeneratorService, TemplateListService templateListService) {
        this.codeGeneratorService = codeGeneratorService;
        this.templateListService = templateListService;
    }

    private static final CodeGeneratorProperties properties = new CodeGeneratorProperties();

    @BeforeAll
    public static void init() {
        properties.setResourceGroup("nubesgen");
        properties.setApplicationName("sampleNubesApplication");
        properties.setLocation("westeurope");
    }

    @Test
    void generateAzureConfiguration() {
        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);
        assertEquals(this.templateListService.listAzureTemplates().size(), configuration.size());
    }

    @Test
    void generateFile() throws IOException {
        String result = this.codeGeneratorService.generateFile("terraform/azure/dev/variables.tf", properties);
        File testFile = new ClassPathResource("nubesgen/terraform/azure/dev/variables.tf").getFile();
        String test = new String(
                Files.readAllBytes(testFile.toPath()));

        assertEquals(test, result);
    }
}
