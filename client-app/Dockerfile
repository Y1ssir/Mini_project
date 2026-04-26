# ============================================================
# Dockerfile - client-app
# Build multi-stage : Node.js pour le build + Nginx pour servir
# ============================================================

# ---- Étape 1 : Build de l'application React ----
FROM node:18-alpine AS builder
WORKDIR /app

# Copier les fichiers de dépendances
COPY package.json package-lock.json ./

# Installer les dépendances
RUN npm ci

# Copier le reste du code source
COPY . .

# Construire l'application pour la production
RUN npm run build

# ---- Étape 2 : Serveur Web Nginx ----
FROM nginx:alpine

# Copier les fichiers buildés depuis l'étape 1 vers le dossier Nginx
COPY --from=builder /app/build /usr/share/nginx/html

# Configuration Nginx pour le routage côté client (React Router)
RUN echo 'server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

# Port exposé
EXPOSE 80

# Démarrage de Nginx
CMD ["nginx", "-g", "daemon off;"]
