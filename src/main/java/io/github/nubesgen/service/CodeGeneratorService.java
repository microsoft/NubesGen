package io.github.nubesgen.service;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class CodeGeneratorService {

    private final Logger log = LoggerFactory.getLogger(CodeGeneratorService.class);
    private final TemplateListService templateListService;
    private final Map<String, Template> templateCache = new HashMap<>();

    public CodeGeneratorService(TemplateListService templateListService) throws IOException {
        this.templateListService = templateListService;
        log.info("Compiling all templates");
        for (String key : templateListService.listAzureTemplates()) {
            log.info("Compiling template key \"{}\"", key);
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:nubesgen/" + key + ".mustache");
            try {
                InputStream inputStream = resources[0].getInputStream();
                String templateString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Template template = Mustache.compiler().compile(templateString);
                templateCache.put(key, template);
            } catch (Exception e) {
                log.error("Could not compile template {}", key, e);
                throw e;
            }
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
