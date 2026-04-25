# ============================================================
# Dockerfile - Config Server (Configuration Centralisée)
# ============================================================
# Stage 1 : Build avec Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================================================
# Stage 2 : Image de production légère
FROM eclipse-temurin:17-jre-jammy

LABEL maintainer="incident-management-team"
LABEL service="config-server"
LABEL version="1.0.0"

WORKDIR /app

# Utilisateur non-root
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Installation de git (requis par Spring Cloud Config Server pour lire le dépôt)
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copie du jar
COPY --from=builder /app/target/config-server-*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

# Port Config Server
EXPOSE 8888

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
  CMD curl -f http://localhost:8888/actuator/health || exit 1

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
