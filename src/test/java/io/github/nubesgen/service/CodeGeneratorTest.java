package io.github.nubesgen.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeGeneratorTest {

    @Test
    void generate() throws IOException {
        CodeGenerator generator = new CodeGenerator();
        CodeGeneratorProperties properties = new CodeGeneratorProperties();
        properties.setResourceGroup("nubesgen");
        properties.setApplicationName("sampleNubesApplication");
        properties.setLocation("westeurope");
        String result = generator.generate("azure/dev/variables", properties);

        File testFile = new ClassPathResource("nubesgen/azure/dev/variables.tf").getFile();
        String test = new String(
                Files.readAllBytes(testFile.toPath()));

        assertEquals(test, result);
    }
}
