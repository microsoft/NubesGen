package io.github.nubesgen.configuration;

/**
 * Runtimes supported.
 * <p>
 * Those can also be grouped in the following categories:
 * <p>
 * DOCKER = DOCKER, DOCKER_SPRING
 * JAVA = DOCKER_SPRING, JAVA, JAVA_GRADLE, SPRING, SPRING_GRADLE
 * SPRING = DOCKER_SPRING, SPRING, SPRING_GRADLE
 * MAVEN = DOCKER_SPRING, JAVA, SPRING
 * GRADLE = JAVA_GRADLE, SPRING_GRADLE
 */
public enum RuntimeType {
    DOCKER, DOCKER_SPRING, JAVA, JAVA_GRADLE, SPRING, SPRING_GRADLE, DOTNET, NODEJS
}
