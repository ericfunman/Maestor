# 🚀 Installation des Prérequis - Maestror

## ⚠️ Prérequis Manquants

Pour exécuter l'application Maestror, vous devez installer les outils suivants :

## 📦 Option 1 : Installation avec Docker (Recommandé)

### 1. Installer Docker Desktop

**Télécharger et installer :**
- 🔗 [Docker Desktop pour Windows](https://www.docker.com/products/docker-desktop)

**Après installation :**
```powershell
# Vérifier l'installation
docker --version
docker-compose --version
```

**Lancer l'application :**
```powershell
cd "C:\Users\Eric LAPINA\Documents\Maestror"
docker-compose up -d
```

✅ **Avantages :**
- Installation la plus simple
- Tout est pré-configuré
- PostgreSQL inclus
- Pas besoin d'installer Java, Node.js, Maven

---

## 📦 Option 2 : Installation Manuelle (Développement)

Si vous voulez développer et modifier le code :

### 1. Installer Java 21

**Télécharger :**
- 🔗 [Adoptium Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21)

**Installation :**
1. Télécharger la version Windows x64 MSI
2. Installer avec les options par défaut
3. Vérifier :
```powershell
java -version
# Doit afficher : openjdk version "21.x.x"
```

### 2. Installer Node.js 20

**Télécharger :**
- 🔗 [Node.js 20 LTS](https://nodejs.org/en/download/)

**Installation :**
1. Télécharger Windows Installer (.msi)
2. Installer avec les options par défaut (cocher "Add to PATH")
3. Vérifier :
```powershell
node -v
npm -v
```

### 3. Installer Maven 3.9+

**Option A - Via Chocolatey (recommandé) :**
```powershell
# Installer Chocolatey si pas déjà fait
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Installer Maven
choco install maven -y
```

**Option B - Manuel :**
1. Télécharger depuis [Maven Apache](https://maven.apache.org/download.cgi)
2. Extraire dans `C:\Program Files\Apache\maven`
3. Ajouter `C:\Program Files\Apache\maven\bin` au PATH

**Vérifier :**
```powershell
mvn -version
```

### 4. Installer PostgreSQL 16

**Télécharger :**
- 🔗 [PostgreSQL 16](https://www.postgresql.org/download/windows/)

**Installation :**
1. Installer avec le setup Windows
2. Définir le mot de passe pour l'utilisateur postgres
3. Port par défaut : 5432

**Créer la base de données :**
```powershell
# Se connecter à PostgreSQL
psql -U postgres

# Dans psql :
CREATE DATABASE maestror_db;
CREATE USER maestror_user WITH PASSWORD 'maestror_password';
GRANT ALL PRIVILEGES ON DATABASE maestror_db TO maestror_user;
\q
```

### 5. Lancer l'Application Manuellement

**Backend :**
```powershell
cd "C:\Users\Eric LAPINA\Documents\Maestror\backend"
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Frontend (nouveau terminal) :**
```powershell
cd "C:\Users\Eric LAPINA\Documents\Maestror\frontend"
npm install
npm start
```

---

## 🎯 Quelle Option Choisir ?

### Choisir Docker si :
- ✅ Vous voulez tester rapidement
- ✅ Vous ne voulez pas installer plein d'outils
- ✅ Vous voulez un environnement reproductible
- ✅ Vous prévoyez de déployer sur le cloud

### Choisir Installation Manuelle si :
- ✅ Vous allez développer activement
- ✅ Vous voulez déboguer le code
- ✅ Vous avez déjà Java/Node.js installés
- ✅ Vous voulez comprendre chaque composant

---

## 📝 Prochaines Étapes

### Une fois Docker installé :

```powershell
# 1. Naviguer vers le projet
cd "C:\Users\Eric LAPINA\Documents\Maestror"

# 2. Lancer tous les services
docker-compose up -d

# 3. Vérifier que tout fonctionne
docker-compose ps

# 4. Accéder à l'application
# Frontend : http://localhost:4200
# Backend : http://localhost:8080
# Swagger : http://localhost:8080/swagger-ui.html
```

### Une fois l'installation manuelle faite :

```powershell
# Terminal 1 - Backend
cd "C:\Users\Eric LAPINA\Documents\Maestror\backend"
mvn spring-boot:run

# Terminal 2 - Frontend  
cd "C:\Users\Eric LAPINA\Documents\Maestror\frontend"
npm start
```

---

## 🆘 Besoin d'Aide ?

**Problèmes avec Docker :**
- Assurez-vous que la virtualisation est activée dans le BIOS
- Redémarrer Windows après l'installation
- Docker Desktop doit être en cours d'exécution

**Problèmes avec Java/Maven :**
- Vérifier que les variables PATH sont correctement configurées
- Redémarrer PowerShell après installation
- Utiliser PowerShell en mode Administrateur si nécessaire

---

## 🔗 Liens Utiles

- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Java JDK 21](https://adoptium.net/)
- [Node.js](https://nodejs.org/)
- [Maven](https://maven.apache.org/)
- [PostgreSQL](https://www.postgresql.org/)
- [Git for Windows](https://git-scm.com/download/win)

---

**Commencez par installer Docker Desktop - c'est la solution la plus rapide ! 🐳**
