package io.github.nubesgen.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class TemplateListService {

    public static final String ROOT_DIRECTORY = "ROOT_DIRECTORY";

    private final Logger log = LoggerFactory.getLogger(TemplateListService.class);

    /*
    Templates are stored:
    - Per template pack, like "terraform"
    - Per type of technology, like "mysql"
     */
    private final Map<String, Map<String, List<String>>> templates = new HashMap<>();

    public TemplateListService() throws IOException {
        // App Service module
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] resources = resolver.getResources("classpath*:nubesgen/**");
        String absolutePath = Arrays.stream(resolver.getResources("classpath*:nubesgen/README.md")).findFirst().get().getURL().getPath();
        int rootDirectoryLength = absolutePath.length() - ("README.md").length();

        for (Resource resource : resources) {
            if (Objects.requireNonNull(resource.getFilename()).endsWith(".mustache")) {
                String fullTemplateName = resource.getURL().getPath().substring(rootDirectoryLength);

                int endIndex = fullTemplateName.indexOf(File.separator);
                String templatePackName = fullTemplateName.substring(0, endIndex);
                if (!templates.containsKey(templatePackName)) {
                    templates.put(templatePackName, new HashMap<>());
                }
                Map<String, List<String>> templatePack = templates.get(templatePackName);
                String templateName = fullTemplateName.substring((templatePackName + File.separator).length());
                if (!templateName.startsWith("modules")) {
                    if (!templatePack.containsKey(ROOT_DIRECTORY)) {
                        templatePack.put(ROOT_DIRECTORY, new ArrayList<>());
                    }
                    templatePack.get(ROOT_DIRECTORY).add(fullTemplateName);
                } else {
                    String moduleAndTemplateName = templateName.substring(("modules" + File.separator).length());
                    String moduleName = moduleAndTemplateName.substring(0, moduleAndTemplateName.indexOf(File.separator));
                    String normalizedModuleName = moduleName.toUpperCase(Locale.ROOT).replaceAll("-", "_");
                    if (!templatePack.containsKey(normalizedModuleName)) {
                        templatePack.put(normalizedModuleName, new ArrayList<>());
                    }
                    templatePack.get(normalizedModuleName).add(fullTemplateName);
                }
            }
        }
    }

    public List<String> listAllTemplates() {
        List<String> allTemplates = new ArrayList<>();
        this.templates.values()
            .forEach(map -> {
                map.values().forEach(allTemplates::addAll);
            });
        return allTemplates;
    }

    public Optional<List<String>> listModuleTemplates(String templatePack, String moduleName) {
        return Optional.ofNullable(this.templates.get(templatePack).get(moduleName));
    }
}
