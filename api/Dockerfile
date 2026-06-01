# syntax=docker/dockerfile:1

# --- Build stage: compile and package with the Maven wrapper ---
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Resolve dependencies first so this layer is cached unless pom.xml changes.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

# Build the executable jar (tests run in CI, skipped here for a fast image build).
COPY src ./src
RUN ./mvnw -B -ntp clean package -DskipTests

# --- Runtime stage: minimal JRE, unprivileged user ---
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

COPY --from=build --chown=spring:spring /workspace/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
