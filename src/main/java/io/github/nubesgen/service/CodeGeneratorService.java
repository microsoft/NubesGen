package io.github.nubesgen.service;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import io.github.nubesgen.configuration.AddonConfiguration;
import io.github.nubesgen.configuration.DatabaseType;
import io.github.nubesgen.configuration.NetworkType;
import io.github.nubesgen.configuration.NubesgenConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

/**
 * Generate the code, using the configuration and a list of templates.
 */
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
            Resource[] resources = resolver.getResources("classpath*:nubesgen/" + key);
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
        log.info("Generate Azure configuration");
        Map<String, String> result = new HashMap<>();

        // GitOps templates
        if (configuration.isGitops()) {
            generateFileList(configuration, ".github", TemplateListService.ROOT_DIRECTORY, result);
        }

        // Main templates
        generateFileList(configuration, TemplateListService.ROOT_DIRECTORY, result);

        // Application templates
        generateFileList(configuration, configuration.getApplicationConfiguration().getApplicationType().name(), result);

        // Database templates
        if (!configuration.getDatabaseConfiguration().getDatabaseType().equals(DatabaseType.NONE)) {
            generateFileList(configuration, configuration.getDatabaseConfiguration().getDatabaseType().name(), result);
        }

        // Add Ons
        for (AddonConfiguration addon : configuration.getAddons()) {
            generateFileList(configuration, addon.getAddonType().name(), result);
        }

        // Isolated Network
        if (!configuration.getNetworkConfiguration().getNetworkType().equals(NetworkType.PUBLIC)) {
            generateFileList(configuration, NetworkType.VIRTUAL_NETWORK.name(), result);
        }
        return result;
    }

    private void generateFileList(NubesgenConfiguration configuration, String moduleName, Map<String, String> result) {
        this.generateFileList(configuration, configuration.getIaCTool().name().toLowerCase(), moduleName, result);
    }

    private void generateFileList(NubesgenConfiguration configuration, String templatePack, String moduleName, Map<String, String> result) {
        Optional<List<String>> templatesList = templateListService.listModuleTemplates(templatePack, moduleName);
        if (templatesList.isPresent()) {
            for (String template : templatesList.get()) {
                // The generated file as the same name as the template, without the ".mustache" suffix
                String filename = template.substring(0, template.length() - ".mustache".length());
                result.put(filename, this.generateFile(template, configuration));
            }
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
