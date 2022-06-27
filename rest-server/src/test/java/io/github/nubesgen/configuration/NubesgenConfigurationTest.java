package io.github.nubesgen.configuration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NubesgenConfigurationTest {

    @Test
    void checkConfigurationDefaultRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();

        assertTrue(properties.isRuntimeDocker());
        assertTrue(properties.isRuntimeDefault());

        assertFalse(properties.isRuntimeJava());
        assertFalse(properties.isRuntimeSpring());
        assertFalse(properties.isRuntimeQuarkus());
        assertFalse(properties.isRuntimeMaven());
        assertFalse(properties.isRuntimeGradle());
        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeNodejs());
        assertFalse(properties.isRuntimePython());
    }

    @Test
    void checkConfigurationDockerRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.DOCKER);

        assertTrue(properties.isRuntimeDocker());
        assertTrue(properties.isRuntimeDefault());

        assertFalse(properties.isRuntimeJava());
        assertFalse(properties.isRuntimeSpring());
        assertFalse(properties.isRuntimeQuarkus());
        assertFalse(properties.isRuntimeMaven());
        assertFalse(properties.isRuntimeGradle());
        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeNodejs());
        assertFalse(properties.isRuntimePython());
    }

    @Test
    void checkConfigurationSpringRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.SPRING);

        assertTrue(properties.isRuntimeSpring());
        assertTrue(properties.isRuntimeJava());
        assertTrue(properties.isRuntimeMaven());

        assertFalse(properties.isRuntimeDefault());
        assertFalse(properties.isRuntimeDocker());
        assertFalse(properties.isRuntimeQuarkus());
        assertFalse(properties.isRuntimeGradle());
        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeNodejs());
        assertFalse(properties.isRuntimePython());
    }

    @Test
    void checkConfigurationQuarkusRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.QUARKUS);

        assertTrue(properties.isRuntimeQuarkus());
        assertTrue(properties.isRuntimeJava());
        assertTrue(properties.isRuntimeMaven());

        assertFalse(properties.isRuntimeDefault());
        assertFalse(properties.isRuntimeSpring());
        assertFalse(properties.isRuntimeDocker());
        assertFalse(properties.isRuntimeGradle());
        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeNodejs());
        assertFalse(properties.isRuntimePython());
    }

    @Test
    void checkConfigurationMicronautRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.MICRONAUT);

        assertTrue(properties.isRuntimeMicronaut());
        assertTrue(properties.isRuntimeJava());
        assertTrue(properties.isRuntimeMaven());

        assertFalse(properties.isRuntimeDefault());
        assertFalse(properties.isRuntimeSpring());
        assertFalse(properties.isRuntimeDocker());
        assertFalse(properties.isRuntimeGradle());
        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeNodejs());
    }

    @Test
    void checkConfigurationMicronautGradleRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.MICRONAUT_GRADLE);

        assertTrue(properties.isRuntimeMicronaut());
        assertTrue(properties.isRuntimeJava());
        assertTrue(properties.isRuntimeGradle());

        assertFalse(properties.isRuntimeDefault());
        assertFalse(properties.isRuntimeSpring());
        assertFalse(properties.isRuntimeDocker());
        assertFalse(properties.isRuntimeMaven());
        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeNodejs());
    }

    @Test
    void checkConfigurationJavaRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.JAVA);

        assertTrue(properties.isRuntimeJava());
        assertTrue(properties.isRuntimeMaven());
        assertTrue(properties.isRuntimeDefault());

        assertFalse(properties.isRuntimeSpring());
        assertFalse(properties.isRuntimeQuarkus());
        assertFalse(properties.isRuntimeDocker());
        assertFalse(properties.isRuntimeGradle());
        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeNodejs());
        assertFalse(properties.isRuntimePython());
    }

    @Test
    void checkConfigurationJavaGradleRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.JAVA_GRADLE);

        assertTrue(properties.isRuntimeJava());
        assertTrue(properties.isRuntimeDefault());
        assertTrue(properties.isRuntimeGradle());

        assertFalse(properties.isRuntimeSpring());
        assertFalse(properties.isRuntimeQuarkus());
        assertFalse(properties.isRuntimeMaven());
        assertFalse(properties.isRuntimeDocker());
        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeNodejs());
        assertFalse(properties.isRuntimePython());
    }

    @Test
    void checkConfigurationSpringGradleRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.SPRING_GRADLE);

        assertTrue(properties.isRuntimeSpring());
        assertTrue(properties.isRuntimeJava());
        assertTrue(properties.isRuntimeGradle());

        assertFalse(properties.isRuntimeDefault());
        assertFalse(properties.isRuntimeQuarkus());
        assertFalse(properties.isRuntimeMaven());
        assertFalse(properties.isRuntimeDocker());
        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeNodejs());
        assertFalse(properties.isRuntimePython());
    }

    @Test
    void checkConfigurationDockerSpringRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.DOCKER_SPRING);

        assertTrue(properties.isRuntimeSpring());
        assertTrue(properties.isRuntimeDocker());

        assertFalse(properties.isRuntimeGradle());
        assertFalse(properties.isRuntimeQuarkus());
        assertFalse(properties.isRuntimeDefault());
        assertFalse(properties.isRuntimeJava());
        assertFalse(properties.isRuntimeMaven());
        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeNodejs());
        assertFalse(properties.isRuntimePython());
    }

    @Test
    void checkConfigurationDotnetRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.DOTNET);

        assertTrue(properties.isRuntimeDotnet());
        assertTrue(properties.isRuntimeDefault());

        assertFalse(properties.isRuntimeQuarkus());
        assertFalse(properties.isRuntimeJava());
        assertFalse(properties.isRuntimeMaven());
        assertFalse(properties.isRuntimeSpring());
        assertFalse(properties.isRuntimeDocker());
        assertFalse(properties.isRuntimeGradle());
        assertFalse(properties.isRuntimeNodejs());
        assertFalse(properties.isRuntimePython());
    }

    @Test
    void checkConfigurationNodeJSRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.NODEJS);

        assertTrue(properties.isRuntimeNodejs());
        assertTrue(properties.isRuntimeDefault());

        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeQuarkus());
        assertFalse(properties.isRuntimeJava());
        assertFalse(properties.isRuntimeMaven());
        assertFalse(properties.isRuntimeSpring());
        assertFalse(properties.isRuntimeDocker());
        assertFalse(properties.isRuntimeGradle());
        assertFalse(properties.isRuntimePython());
    }

    @Test
    void checkConfigurationPythonRuntime() {
        NubesgenConfiguration properties = new NubesgenConfiguration();
        properties.setRuntimeType(RuntimeType.PYTHON);

        assertTrue(properties.isRuntimePython());
        assertTrue(properties.isRuntimeDefault());

        assertFalse(properties.isRuntimeNodejs());
        assertFalse(properties.isRuntimeDotnet());
        assertFalse(properties.isRuntimeQuarkus());
        assertFalse(properties.isRuntimeJava());
        assertFalse(properties.isRuntimeMaven());
        assertFalse(properties.isRuntimeSpring());
        assertFalse(properties.isRuntimeDocker());
        assertFalse(properties.isRuntimeGradle());
    }
}
