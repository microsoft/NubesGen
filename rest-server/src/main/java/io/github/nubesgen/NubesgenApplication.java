package io.github.nubesgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@ImportRuntimeHints(NubesgenRuntimeHints.class)
public class NubesgenApplication {

    public static void main(String[] args) {
        SpringApplication.run(NubesgenApplication.class, args);
    }
}
