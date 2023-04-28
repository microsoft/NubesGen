FROM eclipse-temurin:20-jre
RUN addgroup spring && adduser spring --ingroup spring
USER spring:spring
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-Djava.security.egd=file:/dev/./urandom", "-XX:+AlwaysPreTouch","-cp","app:app/lib/*","io.github.nubesgen.NubesgenApplication"]
