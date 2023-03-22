package io.github.nubesgen.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI myOpenAPI() {
        Info info = new Info()
                .title("NubesGen REST API reference")
                .version("1.0")
                .description("This API exposes endpoints to generating a project using REST API.");

        return new OpenAPI().info(info);
    }
}
