package io.github.nubesgen.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateListService {

    private final List<String> azureList = new ArrayList<>();

    public TemplateListService() {
        azureList.add("terraform/azure/dev/main.tf");
        azureList.add("terraform/azure/dev/output.tf");
        azureList.add("terraform/azure/dev/variables.tf");
    }

    public List<String> listAzureTemplates() {
        return azureList;
    }
}
