package io.github.nubesgen.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TemplateListService {

    private final List<String> mainList = new ArrayList<>();

    private final List<String> mysqlList = new ArrayList<>();

    private final List<String> postgresqlList = new ArrayList<>();

    private final List<String> redisList = new ArrayList<>();

    private final List<String> storageBlobList = new ArrayList<>();

    public TemplateListService() {
        // Main files
        mainList.add("terraform/main.tf");
        mainList.add("terraform/outputs.tf");
        mainList.add("terraform/README.md");
        mainList.add("terraform/variables.tf");
        // App Service module
        mainList.add("terraform/modules/app-service/main.tf");
        mainList.add("terraform/modules/app-service/outputs.tf");
        mainList.add("terraform/modules/app-service/README.md");
        mainList.add("terraform/modules/app-service/variables.tf");
        // MySQL module
        mysqlList.add("terraform/modules/mysql/main.tf");
        mysqlList.add("terraform/modules/mysql/outputs.tf");
        mysqlList.add("terraform/modules/mysql/README.md");
        mysqlList.add("terraform/modules/mysql/variables.tf");
        // PostgreSQL module
        postgresqlList.add("terraform/modules/postgresql/main.tf");
        postgresqlList.add("terraform/modules/postgresql/outputs.tf");
        postgresqlList.add("terraform/modules/postgresql/README.md");
        postgresqlList.add("terraform/modules/postgresql/variables.tf");
        // Redis module
        redisList.add("terraform/modules/redis/main.tf");
        redisList.add("terraform/modules/redis/outputs.tf");
        redisList.add("terraform/modules/redis/README.md");
        redisList.add("terraform/modules/redis/variables.tf");
        // Storage Blob module
        storageBlobList.add("terraform/modules/storage-blob/main.tf");
        storageBlobList.add("terraform/modules/storage-blob/outputs.tf");
        storageBlobList.add("terraform/modules/storage-blob/README.md");
        storageBlobList.add("terraform/modules/storage-blob/variables.tf");
    }

    public List<String> listMainTemplates() {
        return mainList;
    }

    public List<String> listMysqlTemplates() {
        return mysqlList;
    }

    public List<String> listPostgresqlTemplates() {
        return postgresqlList;
    }

    public List<String> listRedisTemplates() {
        return redisList;
    }

    public List<String> listStorageBlobTemplates() {
        return storageBlobList;
    }

    public List<String> listAllTemplates() {
        return Stream.of(mainList, mysqlList, postgresqlList, redisList, storageBlobList)
                .flatMap(list -> list.stream()).collect(Collectors.toList());
    }
}
