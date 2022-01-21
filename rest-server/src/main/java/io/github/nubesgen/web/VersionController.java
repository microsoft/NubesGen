package io.github.nubesgen.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nubesgen.service.ConfigurationService;

@RestController
public class VersionController {

    private final ConfigurationService configurationService;

    public VersionController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
    
    @GetMapping(value = "/version")
    public String version() {
        return "{ \"version\": \"" + this.configurationService.getVersion() + "\"}";
    }
}
