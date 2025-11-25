# Maestror - Gestion des Risques Opérationnels

Application de gestion des risques opérationnels pour Crédit Agricole.

## 📋 Vue d'ensemble

Maestror est une application full-stack permettant de :
- Recevoir et traiter des fichiers (CSV, Excel) de référentiels de risques, incidents et contrôles
- Calculer automatiquement les niveaux de risque
- Monitorer les flux de données
- Générer des rapports et exports (Excel, PDF)
- Visualiser les données via des tableaux de bord

## 🏗️ Architecture

### Stack Technique

**Backend:**
- Java 21 (LTS)
- Spring Boot 3.2.0
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Spring Security (OAuth2/SAML)
- Spring Batch (calculs schedulés)
- Apache POI (Excel)
- OpenCSV (CSV parsing)
- Swagger/OpenAPI

**Frontend:**
- Angular 17
- TypeScript
- Material Design
- Chart.js (visualisations)
- RxJS

**Infrastructure:**
- Docker & Docker Compose
- PostgreSQL 16
- Nginx
- SonarQube (qualité de code)

**CI/CD:**
- GitHub Actions / GitLab CI
- Maven
- Jacoco (coverage)
- SonarQube

## 📁 Structure du Projet

```
maestror/
├── backend/                    # Spring Boot Application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/creditagricole/maestror/
│   │   │   │   ├── config/           # Configuration (Security, OpenAPI)
│   │   │   │   ├── controller/       # REST Controllers
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── entity/           # JPA Entities
│   │   │   │   ├── repository/       # Spring Data Repositories
│   │   │   │   └── service/          # Business Logic
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── db/               # SQL scripts
│   │   └── test/                     # Tests unitaires
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                   # Angular Application
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/               # Services, Interceptors
│   │   │   └── features/           # Composants métier
│   │   └── environments/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── angular.json
│   └── package.json
│
├── docker-compose.yml          # Orchestration Docker
├── .github/workflows/          # CI/CD GitHub Actions
├── .gitlab-ci.yml              # CI/CD GitLab
└── sonar-project.properties    # Configuration SonarQube
```

## 🚀 Démarrage Rapide

### Prérequis

- Java 21 JDK
- Node.js 20+
- Docker & Docker Compose
- Maven 3.9+
- PostgreSQL 16 (ou via Docker)

### Option 1: Avec Docker Compose (Recommandé)

```powershell
# Cloner le projet
git clone <repository-url>
cd Maestror

# Lancer tous les services
docker-compose up -d

# Avec SonarQube
docker-compose --profile with-sonar up -d
```

Accès aux services:
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **SonarQube**: http://localhost:9000

### Option 2: Développement Local

#### Backend

```powershell
cd backend

# Installer les dépendances et compiler
mvn clean install

# Lancer l'application (profile dev)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Ou avec le JAR
java -jar target/maestror-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

#### Frontend

```powershell
cd frontend

# Installer les dépendances
npm install

# Lancer le serveur de développement
npm start

# L'application sera disponible sur http://localhost:4200
```

#### Base de données

```powershell
# Créer la base de données PostgreSQL
psql -U postgres
CREATE DATABASE maestror_db;
CREATE USER maestror_user WITH PASSWORD 'maestror_password';
GRANT ALL PRIVILEGES ON DATABASE maestror_db TO maestror_user;
\q

# Exécuter les scripts SQL
psql -U maestror_user -d maestror_db -f backend/src/main/resources/db/init.sql
psql -U maestror_user -d maestror_db -f backend/src/main/resources/db/sample-data.sql
```

## 📊 Fonctionnalités

### 1. Upload de Fichiers
- Support CSV et Excel (.xlsx, .xls)
- Trois catégories: Référentiels, Incidents, Contrôles
- Validation des données
- Stockage des fichiers en base (BYTEA)
- Suivi du statut de traitement

### 2. Calcul de Risque
- Batch schedulé (tous les jours à 2h00)
- Calcul basé sur:
  - Nombre d'incidents
  - Nombre de contrôles actifs/efficaces
  - Impact financier
  - Niveau d'impact et probabilité
- 4 niveaux de risque: CRITICAL, HIGH, MEDIUM, LOW

### 3. Dashboard & Monitoring
- Vue d'ensemble des indicateurs clés
- Derniers incidents et calculs de risque
- Filtrage par entité
- Actualisation en temps réel

### 4. Gestion des Données
- CRUD Référentiels de risques
- CRUD Incidents
- CRUD Contrôles
- Recherche et filtrage avancés

### 5. Exports & Rapports
- Export Excel (Apache POI)
- Export PDF (iText)
- Rapports personnalisables

## 🔐 Sécurité

### Profils de Sécurité

**Développement (`dev`):**
- Sécurité désactivée pour faciliter les tests
- CORS activé pour localhost:4200

**Production (`prod`):**
- OAuth2/SAML SSO activé
- Rôles: ADMIN, ENTITY1, ENTITY2, ENTITY3...
- JWT tokens
- HTTPS recommandé

### Configuration SSO

Éditer `application-prod.properties`:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://your-sso-provider.com
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://your-sso-provider.com/.well-known/jwks.json
```

## 🧪 Tests

### Backend

```powershell
cd backend

# Tests unitaires
mvn test

# Tests avec coverage
mvn test jacoco:report

# Le rapport est dans: target/site/jacoco/index.html
```

### Frontend

```powershell
cd frontend

# Tests unitaires
npm test

# Tests en mode watch
npm test -- --watch

# Coverage
npm test -- --code-coverage
```

## 📈 Qualité de Code (SonarQube)

```powershell
# Lancer SonarQube avec Docker Compose
docker-compose --profile with-sonar up -d

# Analyser le backend
cd backend
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=admin

# Accéder à SonarQube: http://localhost:9000
# Login par défaut: admin / admin
```

## 🔧 Configuration

### Variables d'Environnement Backend

| Variable | Description | Défaut |
|----------|-------------|--------|
| `SPRING_PROFILES_ACTIVE` | Profil actif (dev/prod) | `dev` |
| `SPRING_DATASOURCE_URL` | URL PostgreSQL | `jdbc:postgresql://localhost:5432/maestror_db` |
| `SPRING_DATASOURCE_USERNAME` | User DB | `maestror_user` |
| `SPRING_DATASOURCE_PASSWORD` | Password DB | `maestror_password` |

### Variables d'Environnement Frontend

Éditer `src/environments/environment.ts` ou `environment.prod.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## 📝 API Documentation

Swagger UI accessible à: `http://localhost:8080/swagger-ui.html`

### Endpoints Principaux

**Upload:**
- `POST /api/files/upload` - Upload un fichier
- `GET /api/files/{id}/status` - Statut du traitement

**Référentiels:**
- `GET /api/referentials` - Liste tous les référentiels
- `GET /api/referentials/{id}` - Détail d'un référentiel
- `GET /api/referentials/code/{code}` - Par code

**Incidents:**
- `GET /api/incidents` - Liste tous les incidents
- `GET /api/incidents/entity/{code}` - Par entité
- `GET /api/incidents/severity/{level}` - Par sévérité

**Contrôles:**
- `GET /api/controls` - Liste tous les contrôles
- `GET /api/controls/entity/{code}` - Par entité

**Calculs de Risque:**
- `GET /api/risk-calculations` - Tous les calculs
- `POST /api/risk-calculations/calculate` - Déclencher calcul manuel

## 🐛 Troubleshooting

### Backend ne démarre pas

```powershell
# Vérifier Java version
java -version  # Doit être 21+

# Vérifier PostgreSQL
psql -U maestror_user -d maestror_db -c "SELECT 1"

# Logs
tail -f backend/logs/spring.log
```

### Frontend - Erreurs npm

```powershell
# Nettoyer et réinstaller
rm -rf node_modules package-lock.json
npm install

# Vérifier Node version
node -v  # Doit être 20+
```

### Docker Compose

```powershell
# Logs des services
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres

# Redémarrer un service
docker-compose restart backend

# Tout supprimer et reconstruire
docker-compose down -v
docker-compose up --build
```

## 📦 Build Production

### Backend

```powershell
cd backend
mvn clean package -Pprod
# JAR dans: target/maestror-1.0.0-SNAPSHOT.jar
```

### Frontend

```powershell
cd frontend
npm run build
# Fichiers dans: dist/maestror-frontend/
```

### Docker Images

```powershell
# Build backend
docker build -t maestror-backend:1.0.0 ./backend

# Build frontend
docker build -t maestror-frontend:1.0.0 ./frontend
```

## 🚢 Déploiement

### Cloud (Azure/AWS/GCP)

1. Créer les ressources cloud (VM, AKS, ECS, etc.)
2. Configurer les secrets (DB credentials, SSO keys)
3. Pousser les images Docker vers un registry
4. Déployer via Kubernetes ou service managé

### On-Premise

1. Installer Docker sur le serveur
2. Configurer les variables d'environnement
3. `docker-compose -f docker-compose.prod.yml up -d`

## 📞 Support

- **Documentation**: Ce README
- **API Docs**: http://localhost:8080/swagger-ui.html
- **Contact**: support@creditagricole.fr

## 📄 Licence

Proprietary - Crédit Agricole © 2025

---

**Développé avec ❤️ pour Crédit Agricole**
