package io.github.nubesgen.service;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import io.github.nubesgen.configuration.*;
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
import java.util.List;
import java.util.Map;

@Service
public class CodeGeneratorService {

    private final Logger log = LoggerFactory.getLogger(CodeGeneratorService.class);
    private final TemplateListService templateListService;
    private final Map<String, Template> templateCache = new HashMap<>();

    public CodeGeneratorService(TemplateListService templateListService) throws IOException {
        this.templateListService = templateListService;
        log.info("Compiling all templates");
        for (String key : templateListService.listAllTemplates()) {
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

    public Map<String, String> generateAzureConfiguration(NubesgenConfiguration configuration) {
        log.info("Generate Azure configuation");
        Map<String, String> result = new HashMap<>();
        // Main templates
        generateFileList(configuration, templateListService.listMainTemplates(), result);
        // App Services templates
        if (ApplicationType.APP_SERVICE.equals(configuration.getApplicationType())) {
            generateFileList(configuration, templateListService.listAppServiceTemplates(), result);
        }
        // Functions templates
        if (ApplicationType.FUNCTION.equals(configuration.getApplicationType())) {
            generateFileList(configuration, templateListService.listFunctionTemplates(), result);
        }
        // MySQL templates
        if (DatabaseType.MYSQL.equals(configuration.getDatabaseConfiguration().getDatabaseType())) {
            generateFileList(configuration, templateListService.listMysqlTemplates(), result);
        }
        // PostgreSQL templates
        if (DatabaseType.POSTGRESQL.equals(configuration.getDatabaseConfiguration().getDatabaseType())) {
            generateFileList(configuration, templateListService.listPostgresqlTemplates(), result);
        }
        // Add Ons
        for (AddOnConfiguration addOn : configuration.getAddOns()) {
            if (AddOnType.REDIS.equals(addOn.getAddOnType())) {
                generateFileList(configuration, templateListService.listRedisTemplates(), result);
            }
            if (AddOnType.STORAGE_BLOB.equals(addOn.getAddOnType())) {
                generateFileList(configuration, templateListService.listStorageBlobTemplates(), result);
            }
        }
        return result;
    }

    private void generateFileList(NubesgenConfiguration configuration, List<String> fileList, Map<String, String> result) {
        for (String key : fileList) {
            result.put(key, this.generateFile(key, configuration));
        }
    }

    String generateFile(String key, NubesgenConfiguration configuration) {
        Template template = templateCache.get(key);
        if (template == null) {
            log.error("Template key \"{}\" does not exist", key);
            return "Template does not exist";
        }
        return template.execute(configuration);
    }
}
