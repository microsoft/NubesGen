package io.github.nubesgen.web;

import io.github.nubesgen.service.CodeGeneratorService;
import io.github.nubesgen.service.TemplateListService;
import io.github.nubesgen.service.compression.TarGzService;
import io.github.nubesgen.service.compression.ZipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MainController.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class AdditionalConfig {

        @Bean
        public TemplateListService templateListService() {
            return new TemplateListService();
        }

        @Bean
        public CodeGeneratorService codeGeneratorService() throws IOException {
            return new CodeGeneratorService(templateListService());
        }

        @Bean
        public TarGzService tarGzService() {
            return new TarGzService();
        }

        @Bean
        public ZipService zipService() {
            return new ZipService();
        }
    }

    @Test
    public void generateDefaultApplication() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/nubesgen.zip")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("azurerm_app_service"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("JAVA|11-java11"));
    }

    @Test
    public void generateApplicationWithPostgresql() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/myapplication.zip?region=westeurope&database=POSTGRESQL")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/postgresql"));
        assertTrue(entries.containsKey("terraform/variables.tf"));
        assertTrue(entries.get("terraform/variables.tf").contains("myapplication"));
        assertTrue(entries.get("terraform/variables.tf").contains("westeurope"));
    }

    @Test
    public void generateApplicationWithSpringRuntime() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/myapplication.zip?region=westeurope&runtime=spring&database=POSTGRESQL")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("azurerm_app_service"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("JAVA|11-java11"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("\"SPRING_DATASOURCE_URL\"      = \"jdbc:postgresql://${var.database_url}\""));
    }

    @Test
    public void generateApplicationWithDotnetRuntime() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/myapplication.zip?region=westeurope&runtime=dotnet&database=POSTGRESQL")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("azurerm_app_service"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("DOTNETCORE|"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
    }

    @Test
    public void generateApplicationWithJavaRuntime() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/myapplication.zip?region=westeurope&runtime=java&database=POSTGRESQL")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/app-service"));
        assertTrue(entries.containsKey("terraform/modules/app-service/main.tf"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("azurerm_app_service"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("JAVA|"));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("DATABASE_URL"));
    }

    @Test
    public void generateApplicationWithAddons() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/myapplication.zip?addons=STORAGE_BLOB,REDIS")).andDo(print()).andExpect(status().isOk())
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
    }

    @Test
    public void generateFunctionWithCosmosdb() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/myapplication.zip?application=function&addons=cosmosdb_mongodb")).andDo(print()).andExpect(status().isOk())
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
        MvcResult result = this.mockMvc.perform(get("/myapplication.zip?application=function.premium")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.containsKey("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/function"));
        assertTrue(entries.containsKey("terraform/modules/function/main.tf"));
        assertTrue(entries.get("terraform/modules/function/main.tf").contains("azurerm_function_app"));
        assertTrue(entries.get("terraform/modules/function/main.tf").contains("kind     = \"elastic\""));
        assertTrue(entries.get("terraform/modules/function/main.tf").contains("tier     = \"ElasticPremium\""));
    }

    @Test
    public void generateApplicationWithStandardTier() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/myapplication.zip?region=westeurope&application=app_service.standard&database=mysql.general_purpose")).andDo(print()).andExpect(status().isOk())
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
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("tier = \"Standard\""));
        assertTrue(entries.get("terraform/modules/app-service/main.tf").contains("size = \"S1\""));
        assertTrue(entries.containsKey("terraform/modules/mysql/main.tf"));
        assertTrue(entries.get("terraform/modules/mysql/main.tf").contains("sku_name                          = \"GP_Gen5_2\""));
    }

    private static Map<String, String> extractZipEntries(byte[] content) throws IOException {
        Map<String, String> entries = new HashMap<>();

        ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(content));
        boolean hasMoreEntries = true;
        while (hasMoreEntries) {
            ZipEntry entry = zipStream.getNextEntry();
            if (entry != null) {
                StringBuilder s = new StringBuilder();
                byte[] buffer = new byte[1024];
                int read = 0;
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
}
