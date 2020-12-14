package io.github.nubesgen.service;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CodeGenerator {

    private final Logger log = LoggerFactory.getLogger(CodeGenerator.class);

    private Map<String, Template> templateCache = new ConcurrentHashMap<>();

    public String generate(String key, CodeGeneratorProperties properties) {
        Template template = templateCache.get(key);
        if (template == null) {
            log.info("Compiling template key \"{}\"", key);
            try {
                ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Resource[] resources = resolver.getResources("classpath*:nubesgen/" + key + ".tf.mustache");
                Path path = resources[0].getFile().toPath();
                String templateString = new String(
                        Files.readAllBytes(path));
                template = Mustache.compiler().compile(templateString);
            } catch (IOException ioe) {
                log.error("Template key \"{}\" doesn't exist", key, ioe);
                return "Template does not exist";
            }
            templateCache.put(key, template);
        }
        return template.execute(properties);
    }
}
