# ============================================================
# Dockerfile - chat-service
# Build multi-stage : Maven build + runtime JRE 17
# ============================================================

# ---- Étape 1 : Build Maven ----
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copier le pom.xml en premier (cache des dépendances Maven)
COPY pom.xml .

# Télécharger les dépendances (mise en cache si pom.xml inchangé)
# Note : le chat-service n'a pas de Maven Wrapper — on utilise mvn directement
RUN apk add --no-cache maven && mvn dependency:go-offline -B

# Copier le code source et construire
COPY src ./src
RUN mvn package -DskipTests -B

# ---- Étape 2 : Image de production légère ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Utilisateur non-root pour la sécurité
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copier le JAR depuis l'étape de build
COPY --from=builder /app/target/*.jar app.jar

# Port exposé (cohérent avec chat-service.yml : 8084)
EXPOSE 8084

# Variables d'environnement par défaut (surchargées par docker-compose)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Démarrage de l'application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
