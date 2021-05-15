package io.github.nubesgen.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.github.nubesgen.configuration.NubesgenConfiguration;

@Service
public class Generator {

    private final List<CodeGeneratorService> codeGeneratorServices;

    public Generator(List<CodeGeneratorService> codeGeneratorServices) {
        this.codeGeneratorServices = codeGeneratorServices;
    }

    public Map<String, String> generateAzureConfiguration(NubesgenConfiguration configuration) {
        for (CodeGeneratorService codeGeneratorService : codeGeneratorServices) {
            if (codeGeneratorService.supports(configuration.getIaCTool())) {
                return codeGeneratorService.generateAzureConfiguration(configuration);
            }
        }
        throw new IllegalStateException("no code generator found for " + configuration.getIaCTool());
    }
}
