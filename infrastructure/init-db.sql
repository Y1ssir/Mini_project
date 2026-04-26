-- ============================================================
-- init-db.sql
-- Script d'initialisation des bases de données PostgreSQL
-- Exécuté automatiquement au premier démarrage du conteneur
-- ============================================================

-- Base de données Keycloak
CREATE DATABASE keycloak_db;

-- Base de données User Service
CREATE DATABASE user_db;

-- Base de données Incident Service
CREATE DATABASE incident_db;

-- Base de données Comment Service
CREATE DATABASE comment_db;

-- Base de données Chat Service
CREATE DATABASE chat_db;

-- Permissions (l'utilisateur postgres a déjà tous les droits par défaut)
GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE user_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE incident_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE comment_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE chat_db TO postgres;
