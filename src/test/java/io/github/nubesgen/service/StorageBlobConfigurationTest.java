package io.github.nubesgen.service;

import io.github.nubesgen.configuration.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests NubesGen with the App Service + Blob storage options.
 */
@SpringBootTest
class StorageBlobConfigurationTest {

    private final Logger log = LoggerFactory.getLogger(StorageBlobConfigurationTest.class);
    private static final NubesgenConfiguration properties = new NubesgenConfiguration();
    private final CodeGeneratorService codeGeneratorService;
    private final TemplateListService templateListService;

    @Autowired
    public StorageBlobConfigurationTest(CodeGeneratorService codeGeneratorService, TemplateListService templateListService) {
        this.codeGeneratorService = codeGeneratorService;
        this.templateListService = templateListService;
    }

    @BeforeAll
    public static void init() {
        properties.setApplicationName("nubesgen-testapp-storage-blob");
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.NONE, ConfigurationSize.BASIC));
        List<AddOnConfiguration> addOns = new ArrayList<>();
        addOns.add(new AddOnConfiguration(AddOnType.STORAGE_BLOB, ConfigurationSize.BASIC));
        properties.setAddOns(addOns);
    }

    @Test
    void generateStorageBlobConfiguration() throws IOException {
        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);
        int templatesSize = this.templateListService.listMainTemplates().size() +
                this.templateListService.listAppServiceTemplates().size() +
                this.templateListService.listStorageBlobTemplates().size();

        assertEquals(templatesSize, configuration.size());
        for (String filename : templateListService.listMainTemplates()) {
            this.generateAndTestOneFile(filename);
        }
        for (String filename : templateListService.listAppServiceTemplates()) {
            this.generateAndTestOneFile(filename);
        }
        for (String filename : templateListService.listStorageBlobTemplates()) {
            this.generateAndTestOneFile(filename);
        }
    }

    private void generateAndTestOneFile(String filename) throws IOException {
        log.info("Validating {}", filename);
        String result = this.codeGeneratorService.generateFile(filename, properties);
        File testFile = new ClassPathResource("nubesgen/storage-blob/" + filename).getFile();
        String test = new String(
                Files.readAllBytes(testFile.toPath()));

        assertEquals(test, result);
    }
}
