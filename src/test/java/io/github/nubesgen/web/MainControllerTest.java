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
import java.util.ArrayList;
import java.util.List;
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
        List<String> entries = extractZipEntries(zippedContent);
        assertTrue(entries.contains("terraform/main.tf"));
    }

    private static List<String> extractZipEntries(byte[] content) throws IOException {
        List<String> entries = new ArrayList<>();

        ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(content));
        while (zipStream.available() == 1) {
            ZipEntry entry = zipStream.getNextEntry();
            if (entry != null) {
                entries.add(entry.getName());
            }
        }
        zipStream.close();
        return entries;
    }
}
