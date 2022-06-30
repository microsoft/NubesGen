package io.github.nubesgen.configuration;

/**
 * Runtimes supported.
 * <p>
 * Those can also be grouped in the following categories:
 * <p>
 * DOCKER = DOCKER, DOCKER_SPRING
 * JAVA = JAVA, JAVA_GRADLE, SPRING, SPRING_GRADLE, QUARKUS
 * SPRING = DOCKER_SPRING, SPRING, SPRING_GRADLE
 * MAVEN = DOCKER_SPRING, JAVA, SPRING, QUARKUS
 * GRADLE = JAVA_GRADLE, SPRING_GRADLE
 * QUARKUS = QUARKUS
 */
public enum RuntimeType {
    DOCKER,
    DOCKER_SPRING,
    DOCKER_MICRONAUT,
    DOCKER_MICRONAUT_GRADLE,
    JAVA,
    JAVA_GRADLE,
    SPRING,
    SPRING_GRADLE,
    QUARKUS,
    QUARKUS_NATIVE,
    MICRONAUT_GRADLE,
    MICRONAUT,
    DOTNET,
    NODEJS,
    PYTHON,
}
