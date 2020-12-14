package io.github.nubesgen.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CodeGeneratorTest {

    @Autowired
    private CodeGenerator generator;

    @Test
    void generate() throws IOException {
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
