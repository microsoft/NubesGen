package io.github.nubesgen.service;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CodeGeneratorService {

    private final Logger log = LoggerFactory.getLogger(CodeGeneratorService.class);

    private Map<String, Template> templateCache = new HashMap<>();

    private final TemplateListService templateListService;

    public CodeGeneratorService(TemplateListService templateListService) throws IOException {
        this.templateListService = templateListService;
        log.info("Compiling all templates");
        for (String key : templateListService.listAzureTemplates()) {
            log.info("Compiling template key \"{}\"", key);
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:nubesgen/" + key + ".mustache");
            Path path = resources[0].getFile().toPath();
            String templateString = new String(
                    Files.readAllBytes(path));
            Template template = Mustache.compiler().compile(templateString);
            templateCache.put(key, template);
        }
    }

    public Map<String, String> generateAzureConfiguration(CodeGeneratorProperties properties) {
        log.info("Generate Azure configuration");
        Map<String, String> configuration = new HashMap<>();
        for (String key : templateListService.listAzureTemplates()) {
            String generatedFile = this.generateFile(key, properties);
            configuration.put(key, generatedFile);
        }
        return configuration;
    }

    String generateFile(String key, CodeGeneratorProperties properties) {
        Template template = templateCache.get(key);
        if (template == null) {
            log.error("Template key \"{}\" does not exist", key);
            return "Template does not exist";
        }
        return template.execute(properties);
    }
}
