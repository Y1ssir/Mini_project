# ============================================================
# Dockerfile - API Gateway
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
LABEL service="gateway"
LABEL version="1.0.0"

WORKDIR /app

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/gateway-*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

# Port API Gateway
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=5 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Plus de mémoire pour le Gateway (gère toutes les connexions)
ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
