package io.github.nubesgen.service;

import java.util.Map;

import io.github.nubesgen.configuration.IaCToolType;
import io.github.nubesgen.configuration.NubesgenConfiguration;

public interface CodeGeneratorService {

    boolean supports(IaCToolType cToolType);

    Map<String, String> generateAzureConfiguration(NubesgenConfiguration configuration);

}
