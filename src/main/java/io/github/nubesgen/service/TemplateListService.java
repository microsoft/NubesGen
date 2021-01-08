package io.github.nubesgen.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateListService {

    private final List<String> azureList = new ArrayList<>();

    public TemplateListService() {
        // Main files
        azureList.add("terraform/main.tf");
        azureList.add("terraform/outputs.tf");
        azureList.add("terraform/README.md");
        azureList.add("terraform/variables.tf");
        // App Service module
        azureList.add("terraform/modules/app-service/main.tf");
        azureList.add("terraform/modules/app-service/outputs.tf");
        azureList.add("terraform/modules/app-service/README.md");
        azureList.add("terraform/modules/app-service/variables.tf");
        // MySQL module
        azureList.add("terraform/modules/mysql/main.tf");
        azureList.add("terraform/modules/mysql/outputs.tf");
        azureList.add("terraform/modules/mysql/README.md");
        azureList.add("terraform/modules/mysql/variables.tf");
    }

    public List<String> listAzureTemplates() {
        return azureList;
    }
}
