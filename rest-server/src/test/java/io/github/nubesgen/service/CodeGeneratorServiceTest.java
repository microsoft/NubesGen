package io.github.nubesgen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.nubesgen.configuration.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

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

        testGeneratedFiles(
            properties,
            "terraform/app-service-spring",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name())
        );
    }

    @Test
    void generateDefaultQuarkusConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp");
        properties.setRuntimeType(RuntimeType.QUARKUS);
        properties.setRegion("westeurope");

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/app-service-quarkus",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name())
        );
    }

    @Test
    void generateDefaultMicronautConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp");
        properties.setRuntimeType(RuntimeType.MICRONAUT);
        properties.setRegion("westeurope");

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/app-service-micronaut",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name())
        );
    }

    @Test
    void generateGitOpsGradleMicronautPostgresConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp");
        properties.setRuntimeType(RuntimeType.MICRONAUT_GRADLE);
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.POSTGRESQL, Tier.BASIC));
        properties.setRegion("westeurope");
        properties.setGitops(true);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/app-service-micronaut-gradle-gitops-postgres",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates(".github", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.POSTGRESQL.name())
        );
    }

    @Test
    void generateGitOpsMavenMicronautPostgresConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp");
        properties.setRuntimeType(RuntimeType.MICRONAUT);
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.POSTGRESQL, Tier.BASIC));
        properties.setRegion("westeurope");
        properties.setGitops(true);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/app-service-micronaut-maven-gitops-postgres",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates(".github", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.POSTGRESQL.name())
        );
    }

    @Test
    void generateGitOpsMavenConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-gitops-testapp");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setGitops(true);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/app-service-maven-gitops",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates(".github", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name())
        );
    }

    @Test
    void generateGitOpsGradleConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-gitops-gradle");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING_GRADLE);
        properties.setGitops(true);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/app-service-gradle-gitops",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates(".github", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name())
        );
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

        testGeneratedFiles(
            properties,
            "terraform/app-service-docker",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates(".github", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name())
        );
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

        testGeneratedFiles(
            properties,
            "terraform/cosmosdb-mongodb",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.COSMOSDB_MONGODB.name())
        );
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

        testGeneratedFiles(
            properties,
            "terraform/function-mysql",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.FUNCTION.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.MYSQL.name())
        );
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

        testGeneratedFiles(
            properties,
            "terraform/function-maven-gitops",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates(".github", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.FUNCTION.name())
        );
    }

    @Test
    void generateMysqlConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-mysql");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.MYSQL, Tier.BASIC));

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/mysql",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.MYSQL.name())
        );
    }

    @Test
    void generateMysqlQuarkusConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-mysql-quarkus");
        properties.setRuntimeType(RuntimeType.QUARKUS);
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.MYSQL, Tier.BASIC));

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/mysql-quarkus",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.MYSQL.name())
        );
    }

    @Test
    void generatePostgreSQLConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-postgresql");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.POSTGRESQL, Tier.BASIC));

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/postgresql",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.POSTGRESQL.name())
        );
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

        testGeneratedFiles(
            properties,
            "terraform/redis",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.REDIS.name())
        );
    }

    @Test
    void generateAppInsightsConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-appinsights-java");
        properties.setRuntimeType(RuntimeType.JAVA);
        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.APP_SERVICE, Tier.BASIC));
        properties.setRegion("westeurope");
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.APPLICATION_INSIGHTS, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/app-insights-java",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.APPLICATION_INSIGHTS.name())
        );
    }

    @Test
    void generateKeyVaultConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-key-vault");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.APP_SERVICE, Tier.STANDARD));
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.POSTGRESQL, Tier.BASIC));
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.KEY_VAULT, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/key-vault",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.POSTGRESQL.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.KEY_VAULT.name())
        );
    }

    @Test
    void generateSqlServerConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-sql-server");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setRegion("westeurope");
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.SQL_SERVER, Tier.SERVERLESS));

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/sql-server",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.SQL_SERVER.name())
        );
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

        testGeneratedFiles(
            properties,
            "terraform/storage-blob",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.STORAGE_BLOB.name())
        );
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

        testGeneratedFiles(
            properties,
            "terraform/app-service-nodejs",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates(".github", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name())
        );
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

        testGeneratedFiles(
            properties,
            "terraform/app-service-dotnet",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates(".github", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.SQL_SERVER.name())
        );
    }

    @Test
    void generateAppServicePythonConfiguration() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-app-service-python");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.PYTHON);
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.NONE, Tier.BASIC));
        properties.setGitops(true);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
                properties,
                "terraform/app-service-python",
                configuration,
                this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
                this.templateListService.listModuleTemplates(".github", TemplateListService.ROOT_DIRECTORY),
                this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name())
        );
    }

    @Test
    void generateContainerAppSpring() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-ca-test");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.NONE, Tier.BASIC));
        properties.setGitops(false);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
                properties,
                "terraform/aca-spring",
                configuration,
                this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
                this.templateListService.listModuleTemplates("terraform", ApplicationType.CONTAINER_APPS.name())
        );
    }

    @Test
    void generateSpringAppsWithTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-spring");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING);

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.SPRING_APPS, Tier.BASIC));
        properties.setIaCTool(IaCTool.TERRAFORM);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/asa-public-java",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.SPRING_APPS.name())
        );
    }

    @Test
    void generateSpringAppsAppInsightsWithTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-spring");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING);

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.SPRING_APPS, Tier.BASIC));
        properties.setIaCTool(IaCTool.TERRAFORM);
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.APPLICATION_INSIGHTS, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/asa-insights-java",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.SPRING_APPS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.APPLICATION_INSIGHTS.name())
        );
    }

    @Test
    void generateSpringAppsAddOnsWithTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-spring");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING);

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.SPRING_APPS, Tier.BASIC));
        properties.setIaCTool(IaCTool.TERRAFORM);
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.APPLICATION_INSIGHTS, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.KEY_VAULT, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.COSMOSDB_MONGODB, Tier.FREE));
        addons.add(new AddonConfiguration(AddonType.REDIS, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.STORAGE_BLOB, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/asa-addons-java",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.SPRING_APPS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.APPLICATION_INSIGHTS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.COSMOSDB_MONGODB.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.KEY_VAULT.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.REDIS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.STORAGE_BLOB.name())
        );
    }

    @Test
    void generateSpringAppsMySqlWithTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-spring");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setGitops(true);

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.SPRING_APPS, Tier.BASIC));
        properties.setIaCTool(IaCTool.TERRAFORM);
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.MYSQL, Tier.BASIC));
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.KEY_VAULT, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/asa-mysql-java",
            configuration,
            this.templateListService.listModuleTemplates(".github", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.SPRING_APPS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.KEY_VAULT.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.MYSQL.name())
        );
    }

    @Test
    void generateSpringAppsSqlWithTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-spring");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING);

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.SPRING_APPS, Tier.BASIC));
        properties.setIaCTool(IaCTool.TERRAFORM);
        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.SQL_SERVER, Tier.SERVERLESS));
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.KEY_VAULT, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/asa-sqlserver-java",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.SPRING_APPS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.KEY_VAULT.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.SQL_SERVER.name())
        );
    }

    @Test
    void generateSpringAppsVNetInjectionTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-asa-vnet-java");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setNetworkConfiguration(new NetworkConfiguration(NetworkType.VIRTUAL_NETWORK));

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.SPRING_APPS, Tier.STANDARD));
        properties.setIaCTool(IaCTool.TERRAFORM);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/asa-vnet-java",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.SPRING_APPS.name()),
            this.templateListService.listModuleTemplates("terraform", NetworkType.VIRTUAL_NETWORK.name())
        );
    }

    @Test
    void generateSpringAppsAddonsVNetInjectionTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-asa-vnet-addons");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setNetworkConfiguration(new NetworkConfiguration(NetworkType.VIRTUAL_NETWORK));

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.SPRING_APPS, Tier.STANDARD));
        properties.setIaCTool(IaCTool.TERRAFORM);

        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.POSTGRESQL, Tier.GENERAL_PURPOSE));
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.APPLICATION_INSIGHTS, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.KEY_VAULT, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.COSMOSDB_MONGODB, Tier.FREE));
        addons.add(new AddonConfiguration(AddonType.REDIS, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.STORAGE_BLOB, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/asa-vnet-addons-java",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.SPRING_APPS.name()),
            this.templateListService.listModuleTemplates("terraform", NetworkType.VIRTUAL_NETWORK.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.POSTGRESQL.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.APPLICATION_INSIGHTS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.KEY_VAULT.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.COSMOSDB_MONGODB.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.REDIS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.STORAGE_BLOB.name())
        );
    }

    @Test
    void generateSpringAppsMsSqlVNetInjectionTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-vnet-mssql");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setNetworkConfiguration(new NetworkConfiguration(NetworkType.VIRTUAL_NETWORK));

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.SPRING_APPS, Tier.STANDARD));
        properties.setIaCTool(IaCTool.TERRAFORM);

        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.SQL_SERVER, Tier.GENERAL_PURPOSE));

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/asa-vnet-mssql-java",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.SPRING_APPS.name()),
            this.templateListService.listModuleTemplates("terraform", NetworkType.VIRTUAL_NETWORK.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.SQL_SERVER.name())
        );
    }

    @Test
    void generateSpringAppsMySqlVNetInjectionTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-vnet-mysql");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setNetworkConfiguration(new NetworkConfiguration(NetworkType.VIRTUAL_NETWORK));

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.SPRING_APPS, Tier.STANDARD));
        properties.setIaCTool(IaCTool.TERRAFORM);

        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.MYSQL, Tier.GENERAL_PURPOSE));

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/asa-vnet-mysql-java",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.SPRING_APPS.name()),
            this.templateListService.listModuleTemplates("terraform", NetworkType.VIRTUAL_NETWORK.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.MYSQL.name())
        );
    }

    @Test
    void generateAppServiceSpringVNetInjectionTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-vnet");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.SPRING);
        properties.setNetworkConfiguration(new NetworkConfiguration(NetworkType.VIRTUAL_NETWORK));

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.APP_SERVICE, Tier.STANDARD));
        properties.setIaCTool(IaCTool.TERRAFORM);

        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.POSTGRESQL, Tier.BASIC));
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.APPLICATION_INSIGHTS, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.KEY_VAULT, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/app-service-vnet-spring",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", NetworkType.VIRTUAL_NETWORK.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.POSTGRESQL.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.APPLICATION_INSIGHTS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.KEY_VAULT.name())
        );
    }

    @Test
    void generateAppDockerAddonsVNetInjectionTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testapp-docker");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.DOCKER);
        properties.setNetworkConfiguration(new NetworkConfiguration(NetworkType.VIRTUAL_NETWORK));

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.APP_SERVICE, Tier.STANDARD));
        properties.setIaCTool(IaCTool.TERRAFORM);

        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.POSTGRESQL, Tier.GENERAL_PURPOSE));
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.APPLICATION_INSIGHTS, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.KEY_VAULT, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.COSMOSDB_MONGODB, Tier.FREE));
        addons.add(new AddonConfiguration(AddonType.REDIS, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.STORAGE_BLOB, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/app-service-vnet-docker",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.APP_SERVICE.name()),
            this.templateListService.listModuleTemplates("terraform", NetworkType.VIRTUAL_NETWORK.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.POSTGRESQL.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.APPLICATION_INSIGHTS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.KEY_VAULT.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.COSMOSDB_MONGODB.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.REDIS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.STORAGE_BLOB.name())
        );
    }

    @Test
    void generateFunctionAddonsVNetInjectionTerraform() throws IOException {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setApplicationName("nubesgen-testfunction-vnet");
        properties.setRegion("westeurope");
        properties.setRuntimeType(RuntimeType.JAVA);
        properties.setNetworkConfiguration(new NetworkConfiguration(NetworkType.VIRTUAL_NETWORK));

        properties.setApplicationConfiguration(new ApplicationConfiguration(ApplicationType.FUNCTION, Tier.PREMIUM));
        properties.setIaCTool(IaCTool.TERRAFORM);

        properties.setDatabaseConfiguration(new DatabaseConfiguration(DatabaseType.POSTGRESQL, Tier.GENERAL_PURPOSE));
        List<AddonConfiguration> addons = new ArrayList<>();
        addons.add(new AddonConfiguration(AddonType.APPLICATION_INSIGHTS, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.KEY_VAULT, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.COSMOSDB_MONGODB, Tier.FREE));
        addons.add(new AddonConfiguration(AddonType.REDIS, Tier.BASIC));
        addons.add(new AddonConfiguration(AddonType.STORAGE_BLOB, Tier.BASIC));
        properties.setAddons(addons);

        Map<String, String> configuration = this.codeGeneratorService.generateAzureConfiguration(properties);

        testGeneratedFiles(
            properties,
            "terraform/function-vnet-java",
            configuration,
            this.templateListService.listModuleTemplates("terraform", TemplateListService.ROOT_DIRECTORY),
            this.templateListService.listModuleTemplates("terraform", ApplicationType.FUNCTION.name()),
            this.templateListService.listModuleTemplates("terraform", NetworkType.VIRTUAL_NETWORK.name()),
            this.templateListService.listModuleTemplates("terraform", DatabaseType.POSTGRESQL.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.APPLICATION_INSIGHTS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.KEY_VAULT.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.COSMOSDB_MONGODB.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.REDIS.name()),
            this.templateListService.listModuleTemplates("terraform", AddonType.STORAGE_BLOB.name())
        );
    }

    @SafeVarargs
    private void testGeneratedFiles(
        NubesgenConfiguration properties,
        String testDirectory,
        Map<String, String> configuration,
        Optional<List<String>>... templateLists
    ) throws IOException {
        int numberOfGeneratedFiles = 0;
        for (Optional<List<String>> templateList : templateLists) {
            if (templateList.isPresent()) {
                numberOfGeneratedFiles += templateList.get().size();
                for (String template : templateList.get()) {
                    this.generateAndTestOneFile(properties, testDirectory, template);
                }
            }
        }
        assertEquals(numberOfGeneratedFiles, configuration.size());
    }

    private void generateAndTestOneFile(NubesgenConfiguration properties, String testDirectory, String template) throws IOException {
        if ("terraform/.gitignore.mustache".equals(template)) {
            // GitHub Actions uses the GitHub REST API and not git, so .gitignore files are missing and can't be tested
            return;
        }
        log.info("Validating {}", template);
        String result = this.codeGeneratorService.generateFile(template, properties);
        // The generated file as the same name as the template, without the ".mustache" suffix
        String filename = template.substring(0, template.length() - ".mustache".length());
        File testFile = new ClassPathResource("nubesgen/" + testDirectory + "/" + filename).getFile();
        String test = new String(Files.readAllBytes(testFile.toPath()));

        assertEquals(test, result);
    }
}
