package io.github.nubesgen.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateListService {

    private List<String> azureList = new ArrayList<>();

    public TemplateListService() {
        azureList.add("azure/dev/main");
        azureList.add("azure/dev/output");
        azureList.add("azure/dev/variables");
    }

    public List<String> listAzureTemplates() {
        return azureList;
    }
}
