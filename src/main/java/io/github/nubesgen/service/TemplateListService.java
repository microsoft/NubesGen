package io.github.nubesgen.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateListService {

    private List<String> azureList = new ArrayList<>();

    public TemplateListService() {
        azureList.add("azure/dev/main.tf");
        azureList.add("azure/dev/output.tf");
        azureList.add("azure/dev/variables.tf");
    }

    public List<String> listAzureTemplates() {
        return azureList;
    }
}
