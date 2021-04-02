package io.github.nubesgen.service;

import io.github.nubesgen.configuration.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests NubesGen with the default options.
 */
@SpringBootTest
class CodeGeneratorServiceTest {

    private final Logger log = LoggerFactory.getLogger(CodeGeneratorServiceTest.class);

    private final CodeGeneratorService codeGeneratorService;
    private final TemplateListService templateListService;

    @Autowired
    public CodeGeneratorServiceTest(CodeGeneratorService codeGeneratorService, TemplateListService templateListService) {
        this.codeGeneratorService = codeGeneratorService;
        this.templateListService = templateListService;
    }

    @Test
    void generateDefaultSpringConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "app-service-spring", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listAppServiceTemplates());
    }

    @Test
    void generateGitOpsMavenConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-gitops-testapp");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setGitops(true);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "app-service-maven-gitops", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listGitOpsTemplates(),
                this.templateListService.listAppServiceTemplates());
    }

    @Test
    void generateGitOpsGradleConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-gitops-gradle");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING_GRADLE);
        properties.setGitops(true);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "app-service-gradle-gitops", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listGitOpsTemplates(),
                this.templateListService.listAppServiceTemplates());
    }

    @Test
    void generateGitOpsDockerSpringConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-app-service-docker");
        properties.setRegion("westeurope");
        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.APP_SERVICE, Tier.STANDARD));
        properties.setRuntimeType(RuntimeType.DOCKER_SPRING);
        properties.setGitops(true);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "app-service-docker", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listGitOpsTemplates(),
                this.templateListService.listAppServiceTemplates());
    }

    @Test
    void generateSpringCosmosDbMongoDbConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-mongodb");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.NONE, Tier.BASIC));
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.COSMOSDB_MONGODB, Tier.FREE));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "cosmosdb-mongodb", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listAppServiceTemplates(),
                this.templateListService.listCosmosdbMongodbTemplates());

    }

    @Test
    void generateFunctionMysqlConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-function");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.FUNCTION, Tier.CONSUMPTION));
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.MYSQL, Tier.BASIC));

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "function-mysql", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listFunctionTemplates(),
                this.templateListService.listMysqlTemplates());

    }

    @Test
    void generateFunctionMavenGitopsConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-function");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.FUNCTION, Tier.CONSUMPTION));
        properties.setGitops(true);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "function-maven-gitops", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listFunctionTemplates(),
                this.templateListService.listGitOpsTemplates());

    }

    @Test
    void generateMysqlConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-mysql");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.MYSQL, Tier.BASIC));

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "mysql", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listAppServiceTemplates(),
                this.templateListService.listMysqlTemplates());

    }

    @Test
    void generatePostgreSQLConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-postgresql");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.POSTGRESQL, Tier.BASIC));

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "postgresql", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listAppServiceTemplates(),
                this.templateListService.listPostgresqlTemplates());

    }

    @Test
    void generateRedisConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-redis");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.NONE, Tier.BASIC));
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.REDIS, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "redis", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listAppServiceTemplates(),
                this.templateListService.listRedisTemplates());

    }

    @Test
    void generateSqlServerConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-sql-server");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.SQL_SERVER, Tier.SERVERLESS));

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "sql-server", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listAppServiceTemplates(),
                this.templateListService.listSqlServerTemplates());

    }

    @Test
    void generateStorageBlobConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-storage-blob");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.NONE, Tier.BASIC));
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.STORAGE_BLOB, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "storage-blob", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listAppServiceTemplates(),
                this.templateListService.listStorageBlobTemplates());

    }

    @Test
    void generateAppServiceNodejsConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-app-service-nodejs");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.NODEJS);
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.NONE, Tier.BASIC));
        properties.setGitops(true);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "app-service-nodejs", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listGitOpsTemplates(),
                this.templateListService.listAppServiceTemplates());

    }

    @Test
    void generateAppServiceDotNetConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-app-service-dotnet");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.DOTNET);
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.SQL_SERVER, Tier.SERVERLESS));
        properties.setGitops(true);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(properties, "app-service-dotnet", configuration,
                this.templateListService.listMainTemplates(),
                this.templateListService.listGitOpsTemplates(),
                this.templateListService.listAppServiceTemplates(),
                this.templateListService.listSqlServerTemplates());

    }

    private void testGeneratedFiles(NubesgenConfiguration properties, String testDirectory,
                                    Map<String, String> configuration, List<String>... templateLists)
            throws IOException {

        int numberOfGeneratedFiles = 0;
        for (List<String> templateList : templateLists) {
            numberOfGeneratedFiles += templateList.size();
            for (String filename : templateList) {
                this.generateAndTestOneFile(properties, testDirectory, filename);
            }
        }
        assertEquals(numberOfGeneratedFiles, configuration.size());
    }

    private void generateAndTestOneFile(NubesgenConfiguration properties, String testDirectory, String filename) throws IOException {
        if ("terraform/.gitignore".equals(filename)) {
            // GitHub Actions uses the GitHub REST API and not git, so .gitignore files are missing and can't be tested
            return;
        }
        log.info("Validating {}", filename);
        String result = this.codeGeneratorService.generateFile(filename, properties);
        File testFile = new ClassPathResource("nubesgen/" + testDirectory + "/" + filename).getFile();
        String test = new String(
                Files.readAllBytes(testFile.toPath()));

        assertEquals(test, result);
    }
}
