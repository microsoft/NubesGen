package io.github.nubesgen.service;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CodeGenerator {

    public String generate(String templatePath, CodeGeneratorProperties properties) throws IOException {
        File file = new ClassPathResource(
                "nubesgen/" + templatePath + ".tf.mustache").getFile();

        String templateString = new String(
                Files.readAllBytes(file.toPath()));

        Template template = Mustache.compiler().compile(templateString);
        return template.execute(properties);
    }
}
