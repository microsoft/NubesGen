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
    JAVA,
    JAVA_GRADLE,
    SPRING,
    SPRING_GRADLE,
    QUARKUS,
    DOTNET,
    NODEJS,
}
