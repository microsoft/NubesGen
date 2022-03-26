package io.github.nubesgen.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static Map<String, String> extractZipEntries(byte[] content) throws IOException {
        Map<String, String> entries = new HashMap<>();

        ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(content));
        boolean hasMoreEntries = true;
        while (hasMoreEntries) {
            ZipEntry entry = zipStream.getNextEntry();
            if (entry != null) {
                StringBuilder s = new StringBuilder();
                byte[] buffer = new byte[1024];
                int read;
                while ((read = zipStream.read(buffer, 0, 1024)) >= 0) {
                    s.append(new String(buffer, 0, read));
                }
                entries.put(entry.getName(), s.toString());
            } else {
                hasMoreEntries = false;
            }
        }
        zipStream.close();
        return entries;
    }

    @Test
    public void generateDefaultApplication() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/nubesgen.zip?gitops=true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("azurerm_app_service"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("docker_image"));
        assertTrue(entries.get(".github/workflows/gitops.yml").contains("name: Build and deploy a Docker image"));
        assertFalse(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
        assertFalse(entries.get(".github/workflows/gitops.yml").contains("-Dquarkus.package.type=uber-jar"));
    }

    @Test
    public void generateApplicationWithDefaultDemoName() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/demo.zip?gitops=true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.get("terraform/variables.tf").matches("(?s).*demo-\\d{4}-\\d{4}.*"));
    }

    @Test
    public void generateDefaultSpringApplication() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/nubesgen.zip?runtime=spring&gitops=true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("azurerm_app_service"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("java_server"));
        assertTrue(entries.get(".github/workflows/gitops.yml").contains("name: Build a Java project using Maven"));
        assertFalse(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
        assertFalse(entries.get(".github/workflows/gitops.yml").contains("-Dquarkus.package.type=uber-jar"));
    }

    @Test
    public void generateApplicationWithPostgresql() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?region=westeurope&database=POSTGRESQL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/postgresql"));
        assertTrue(entries.containsKey("terraform/variables.tf"));
        assertTrue(entries.get("terraform/variables.tf").contains("myapplication"));
        assertTrue(entries.get("terraform/variables.tf").contains("westeurope"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
    }

    @Test
    public void generateApplicationWithSpringRuntime() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?region=westeurope&runtime=spring&database=POSTGRESQL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("azurerm_app_service"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("java_server"));
        assertTrue(
            entries
                .get("terraform/modules/app-service/main.tf")
                .contains("\"SPRING_DATASOURCE_URL\"      = \"jdbc:postgresql://${var.database_url}\"")
        );
        assertFalse(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
    }

    @Test
    public void generateSpringApplicationWithRedisAddons() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?runtime=spring&addons=REDIS"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/redis"));
        assertTrue(entries.containsKey("terraform/modules/redis/main.tf"));
        assertTrue(entries.get("terraform/modules/redis/main.tf").contains("azurerm_redis_cache"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("SPRING_REDIS_HOST"));
        assertFalse(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
    }

    @Test
    public void generateDefaultQuarkusApplication() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/nubesgen.zip?runtime=quarkus&gitops=true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("azurerm_app_service"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("java_server"));
        assertTrue(
            entries.get(".github/workflows/gitops.yml").contains("build_command: mvn package -Pprod,azure -Dquarkus.package.type=uber-jar")
        );
        assertFalse(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
    }

    @Test
    public void generateQuarkusApplicationWithRedisAddons() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?runtime=quarkus&addons=REDIS"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/redis"));
        assertTrue(entries.containsKey("terraform/modules/redis/main.tf"));
        assertTrue(entries.get("terraform/modules/redis/main.tf").contains("azurerm_redis_cache"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("QUARKUS_REDIS_HOSTS"));
        assertFalse(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
    }

    @Test
    public void generateQuarkusApplicationWithPostgresql() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?region=westeurope&runtime=quarkus&database=POSTGRESQL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("azurerm_app_service"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("java_server"));
        assertTrue(
            entries
                .get("terraform/modules/app-service/main.tf")
                .contains("\"QUARKUS_DATASOURCE_JDBC_URL\" = \"jdbc:postgresql://${var.database_url}\"")
        );
        assertFalse(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
    }

    @Test
    public void generateApplicationWithDotnetRuntime() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?region=westeurope&runtime=dotnet&database=POSTGRESQL&gitops=true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("azurerm_app_service"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("dotnet_version"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
        assertTrue(entries.get(".github/workflows/gitops.yml").contains("DOTNET_VERSION"));
    }

    @Test
    public void generateApplicationWithJavaRuntime() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?region=westeurope&runtime=java&database=POSTGRESQL&gitops=true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("azurerm_app_service"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("java_server"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
        assertTrue(entries.get(".github/workflows/gitops.yml").contains("name: Build a Java project using Maven"));
        assertFalse(entries.get(".github/workflows/gitops.yml").contains("-Dquarkus.package.type=uber-jar"));
    }

    @Test
    public void generateApplicationWithAddons() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?addons=STORAGE_BLOB,REDIS"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/redis"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/storage-blob"));
        assertTrue(entries.containsKey("terraform/modules/redis/main.tf"));
        assertTrue(entries.get("terraform/modules/redis/main.tf").contains("azurerm_redis_cache"));
        assertTrue(entries.containsKey("terraform/modules/storage-blob/main.tf"));
        assertTrue(entries.get("terraform/modules/storage-blob/main.tf").contains("azurerm_storage_account"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("REDIS_HOST"));
        assertFalse(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
    }

    @Test
    public void generateFunctionWithCosmosdb() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?application=function&addons=cosmosdb_mongodb"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/function"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/cosmosdb-mongodb"));
        assertTrue(entries.containsKey("terraform/modules/function/main.tf"));
        assertTrue(entries.get("terraform/modules/function/main.tf").contains("azurerm_function_app"));
        assertTrue(entries.containsKey("terraform/modules/cosmosdb-mongodb/main.tf"));
        assertTrue(entries.get("terraform/modules/cosmosdb-mongodb/main.tf").contains("azurerm_cosmosdb_mongo_database"));
    }

    @Test
    public void generateFunctionWithPremiumTier() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?application=function.premium"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/function"));
        assertTrue(entries.containsKey("terraform/modules/function/main.tf"));
        assertTrue(entries.get("terraform/modules/function/main.tf").contains("azurerm_function_app"));
        assertTrue(entries.get("terraform/modules/function/main.tf").contains("sku_name = \"EP1\""));
    }

    @Test
    public void generateApplicationWithStandardTier() throws Exception {
        MvcResult result =
            this.mockMvc.perform(
                    get("/myapplication.zip?region=westeurope&application=app_service.standard&database=mysql.general_purpose")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/mysql"));
        assertTrue(entries.containsKey("terraform/variables.tf"));
        assertTrue(entries.get("terraform/variables.tf").contains("myapplication"));
        assertTrue(entries.get("terraform/variables.tf").contains("westeurope"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("sku_name = \"S1\""));
        assertTrue(entries.containsKey("terraform/modules/mysql/main.tf"));
        assertTrue(entries.get("terraform/modules/mysql/main.tf").contains("sku_name                     = \"GP_Standard_D2ds_v4\""));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
    }

    @Test
    public void generateApplicationWithBicep() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?iactool=bicep&region=westeurope&application=app_service.standard"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertFalse(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.containsKey("bicep/main.bicep"));
        assertTrue(entries.get("bicep/main.bicep").contains("modules/app-service/app-service.bicep"));
        assertTrue(entries.containsKey("bicep/modules/app-service/app-service.bicep"));
        assertTrue(entries.get("bicep/modules/app-service/app-service.bicep").contains("Microsoft.Web/serverFarms"));
    }

    @Test
    public void generateApplicationWithBicepAndGitOps() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?iactool=bicep&region=westeurope&application=app_service.standard&gitops=true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("bicep/main.bicep"));
        assertTrue(entries.containsKey(".github/workflows/gitops.yml"));
        assertTrue(entries.get(".github/workflows/gitops.yml").contains("gitops-apply-bicep"));
    }

    @Test
    public void generateSpringCloudWithTerraform() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?region=westeurope&application=spring_cloud.basic"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/spring-cloud"));
        assertFalse(entries.get("terraform/main.tf").contains("modules/virtual-network"));
        assertTrue(entries.containsKey("terraform/variables.tf"));
        assertTrue(entries.get("terraform/variables.tf").contains("myapplication"));
        assertTrue(entries.get("terraform/variables.tf").contains("westeurope"));
        assertTrue(entries.containsKey("terraform/modules/spring-cloud/main.tf"));
        assertTrue(entries.get("terraform/modules/spring-cloud/main.tf").contains("sku_name            = \"B0\""));
    }

    @Test
    public void generateSpringCloudVNetWithTerraform() throws Exception {
        MvcResult result =
            this.mockMvc.perform(get("/myapplication.zip?region=westeurope&application=spring_cloud.standard&network=VIRTUAL_NETWORK"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/spring-cloud"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/virtual-network"));
        assertTrue(entries.containsKey("terraform/variables.tf"));
        assertTrue(entries.get("terraform/variables.tf").contains("myapplication"));
        assertTrue(entries.get("terraform/variables.tf").contains("westeurope"));
        assertTrue(entries.containsKey("terraform/modules/spring-cloud/main.tf"));
        assertTrue(entries.get("terraform/modules/spring-cloud/main.tf").contains("sku_name            = \"S0\""));
        assertTrue(entries.containsKey("terraform/modules/virtual-network/main.tf"));
    }
}
