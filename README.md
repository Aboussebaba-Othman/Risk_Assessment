# Risk Assessment Platform

Une plateforme microservices complète pour l'évaluation des risques d'entreprise.

## 🏗️ Architecture

Cette plateforme est construite avec une architecture microservices basée sur Spring Boot 3 et Spring Cloud.

### Services

- **API Gateway** (Port 8080) - Point d'entrée unique pour tous les services
- **Eureka Server** (Port 8761) - Service de découverte et registration
- **Config Server** (Port 8888) - Gestion centralisée de la configuration
- **Auth Service** (Port 8081) - Authentification et autorisation JWT
- **Company Service** (Port 8082) - Gestion des entreprises
- **Scoring Service** (Port 8083) - Calcul des scores de risque
- **Analysis Service** (Port 8084) - Analyse des données financières
- **Report Service** (Port 8085) - Génération de rapports
- **Alert Service** (Port 8086) - Gestion des alertes

### Technologies

- **Backend**: Java 17, Spring Boot 3.x, Spring Cloud
- **Bases de données**: 
  - PostgreSQL (données relationnelles)
  - MongoDB (documents, rapports, alertes)
  - Redis (cache, sessions)
- **Messaging**: RabbitMQ
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## 🚀 Démarrage rapide

### Prérequis

- Java 17 ou supérieur
- Maven 3.8+
- Docker & Docker Compose

### 1. Démarrer les services d'infrastructure

```bash
docker-compose up -d
```

Cela démarre:
- PostgreSQL (port 5432)
- MongoDB (port 27017)
- Redis (port 6379)
- RabbitMQ (port 5672, UI: 15672)
- Adminer (port 8090) - Interface PostgreSQL
- Mongo Express (port 8091) - Interface MongoDB

### 2. Compiler tous les services

```bash
mvn clean install
```

### 3. Démarrer les services Spring Boot

Dans l'ordre:

```bash
# 1. Config Server
cd config-server && mvn spring-boot:run

# 2. Eureka Server
cd eureka-server && mvn spring-boot:run

# 3. API Gateway
cd api-gateway && mvn spring-boot:run

# 4. Services métier (dans n'importe quel ordre)
cd auth-service && mvn spring-boot:run
cd company-service && mvn spring-boot:run
cd scoring-service && mvn spring-boot:run
cd analysis-service && mvn spring-boot:run
cd report-service && mvn spring-boot:run
cd alert-service && mvn spring-boot:run
```

## 📊 Interfaces d'administration

- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **Adminer (PostgreSQL)**: http://localhost:8090
- **Mongo Express**: http://localhost:8091

## 🔧 Configuration

Les fichiers de configuration se trouvent dans `src/main/resources/application.yml` de chaque service.

## 📝 API Documentation

Une fois les services démarrés, la documentation Swagger est disponible sur:
- Auth Service: http://localhost:8081/swagger-ui.html

## 🛠️ Développement

### Structure du projet

```
risk-assessment-platform/
├── api-gateway/
├── eureka-server/
├── config-server/
├── auth-service/
├── company-service/
├── scoring-service/
├── analysis-service/
├── report-service/
├── alert-service/
├── docker-compose.yml
└── pom.xml (parent)
```

