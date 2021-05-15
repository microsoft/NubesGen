package io.github.nubesgen.service.terraform;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TerraformTemplateListService {

    private final List<String> gitOpsList = new ArrayList<>();

    private final List<String> mainList = new ArrayList<>();

    private final List<String> appServiceList = new ArrayList<>();

    private final List<String> functionList = new ArrayList<>();

    private final List<String> sqlServerList = new ArrayList<>();

    private final List<String> mysqlList = new ArrayList<>();

    private final List<String> postgresqlList = new ArrayList<>();

    private final List<String> redisList = new ArrayList<>();

    private final List<String> storageBlobList = new ArrayList<>();

    private final List<String> cosmosdbMongodbList = new ArrayList<>();

    public TerraformTemplateListService() {
        // GitOps files
        gitOpsList.add(".github/workflows/gitops.yml");
        // Main files
        mainList.add("terraform/.gitignore");
        mainList.add("terraform/main.tf");
        mainList.add("terraform/outputs.tf");
        mainList.add("terraform/README.md");
        mainList.add("terraform/variables.tf");
        // App Service module
        appServiceList.add("terraform/modules/app-service/main.tf");
        appServiceList.add("terraform/modules/app-service/outputs.tf");
        appServiceList.add("terraform/modules/app-service/README.md");
        appServiceList.add("terraform/modules/app-service/variables.tf");
        // Azure Functions module
        functionList.add("terraform/modules/function/main.tf");
        functionList.add("terraform/modules/function/outputs.tf");
        functionList.add("terraform/modules/function/README.md");
        functionList.add("terraform/modules/function/variables.tf");
        // SQL Server module
        sqlServerList.add("terraform/modules/sql-server/main.tf");
        sqlServerList.add("terraform/modules/sql-server/outputs.tf");
        sqlServerList.add("terraform/modules/sql-server/README.md");
        sqlServerList.add("terraform/modules/sql-server/variables.tf");
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
        // Cosmos DB MongoDB module
        cosmosdbMongodbList.add("terraform/modules/cosmosdb-mongodb/main.tf");
        cosmosdbMongodbList.add("terraform/modules/cosmosdb-mongodb/outputs.tf");
        cosmosdbMongodbList.add("terraform/modules/cosmosdb-mongodb/README.md");
        cosmosdbMongodbList.add("terraform/modules/cosmosdb-mongodb/variables.tf");
    }

    public List<String> listGitOpsTemplates() {
        return gitOpsList;
    }

    public List<String> listMainTemplates() {
        return mainList;
    }

    public List<String> listAppServiceTemplates() {
        return appServiceList;
    }

    public List<String> listFunctionTemplates() {
        return functionList;
    }

    public List<String> listSqlServerTemplates() {
        return sqlServerList;
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

    public List<String> listCosmosdbMongodbTemplates() {
        return cosmosdbMongodbList;
    }

    public List<String> listAllTemplates() {
        return Stream.of(gitOpsList, mainList, appServiceList, functionList, sqlServerList, mysqlList, postgresqlList, redisList,
                storageBlobList, cosmosdbMongodbList)
                .flatMap(Collection::stream).collect(Collectors.toList());
    }
}
