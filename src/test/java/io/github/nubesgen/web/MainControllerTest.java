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
        assertTrue(entries.keySet().contains("terraform/main.tf"));
    }

    @Test
    public void generateApplicationWithPostgresql() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/myapplication.zip?region=westeurope&database=POSTGRESQL")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        byte[] zippedContent = result.getResponse().getContentAsByteArray();
        Map<String, String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.keySet().contains("terraform/main.tf"));
        assertTrue(entries.get("terraform/main.tf").contains("modules/postgresql"));
        assertTrue(entries.keySet().contains("terraform/variables.tf"));
        assertTrue(entries.get("terraform/variables.tf").contains("myapplication"));
        assertTrue(entries.get("terraform/variables.tf").contains("westeurope"));
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
