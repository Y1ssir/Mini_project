# ============================================================
# Dockerfile - Eureka Server (Service Register)
# ============================================================
# Stage 1 : Build avec Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copie du pom.xml en premier pour bénéficier du cache Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copie du code source et build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================================================
# Stage 2 : Image de production légère
FROM eclipse-temurin:17-jre-jammy

LABEL maintainer="incident-management-team"
LABEL service="eureka-server"
LABEL version="1.0.0"

WORKDIR /app

# Utilisateur non-root pour la sécurité
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# curl requis pour le HEALTHCHECK Docker
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copie du jar depuis le stage builder
COPY --from=builder /app/target/eureka-server-*.jar app.jar

# Attribution des droits
RUN chown appuser:appgroup app.jar

USER appuser

# Port Eureka
EXPOSE 8761

# Healthcheck Docker natif
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8761/actuator/health || exit 1

# Options JVM optimisées pour les conteneurs
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
