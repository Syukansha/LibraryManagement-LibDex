# syntax=docker/dockerfile:1

# ---- Build stage: package the boot jar with the project's Gradle wrapper ----
# Tests are skipped here: they need a Docker daemon (Testcontainers), which is
# not available inside the build container. CI runs them separately.
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon -x test

# ---- Run stage: minimal JRE image ----
# No config is baked in: DB credentials and the JWT secret arrive as env vars
# at container start (12-factor III). The gitignored .env is never copied.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
