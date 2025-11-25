# Guide de Démarrage Rapide - Maestror

## 🎯 Objectif

Ce guide vous permet de lancer l'application Maestror en 5 minutes.

## ✅ Étape 1: Prérequis

Installer (si pas déjà fait):
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Git](https://git-scm.com/downloads)

## 🚀 Étape 2: Lancer l'Application

Ouvrir PowerShell et exécuter:

```powershell
# Cloner le projet
cd "C:\Users\Eric LAPINA\Documents"
cd Maestror

# Lancer tous les services
docker-compose up -d

# Attendre que les services démarrent (30-60 secondes)
docker-compose ps
```

## 🌐 Étape 3: Accéder aux Services

Ouvrir votre navigateur:

- **Application Web**: http://localhost:4200
- **API Backend**: http://localhost:8080
- **Documentation API**: http://localhost:8080/swagger-ui.html
- **Base de données**: localhost:5432

**Identifiants DB:**
- Database: `maestror_db`
- User: `maestror_user`
- Password: `maestror_password`

## 📝 Étape 4: Tester l'Application

### 4.1 Accéder au Dashboard

1. Aller sur http://localhost:4200
2. Cliquer sur "Tableau de Bord"
3. Vous verrez les statistiques et données de démo

### 4.2 Uploader un Fichier

1. Cliquer sur "Upload Fichiers"
2. Créer un fichier CSV de test:

**test-referentials.csv:**
```csv
riskCode,riskName,riskDescription,riskCategory,riskType,businessLine,impactLevel,probabilityLevel
RISK-TEST-001,Risque de Test,Description du risque test,TEST,INTERNAL,IT,HIGH,MEDIUM
```

3. Choisir "Référentiel de Risques"
4. Sélectionner le fichier
5. Cliquer sur "Uploader"
6. Observer le statut de traitement

### 4.3 Consulter les Données

1. Aller dans "Référentiels"
2. Voir la liste des risques (y compris votre test)
3. Explorer "Incidents" et "Contrôles"

### 4.4 Déclencher un Calcul de Risque

1. Aller sur http://localhost:8080/swagger-ui.html
2. Ouvrir "Risk Calculation Controller"
3. POST `/api/risk-calculations/calculate`
4. Cliquer "Try it out" puis "Execute"
5. Retourner au Dashboard pour voir les résultats

## 🛑 Arrêter l'Application

```powershell
docker-compose down
```

Pour supprimer également les données:
```powershell
docker-compose down -v
```

## 🐛 Problèmes Courants

### Le backend ne démarre pas

```powershell
# Vérifier les logs
docker-compose logs backend

# Redémarrer le service
docker-compose restart backend
```

### Le frontend affiche une erreur

```powershell
# Vérifier que le backend est accessible
curl http://localhost:8080/actuator/health

# Redémarrer le frontend
docker-compose restart frontend
```

### Port déjà utilisé

Si vous avez une erreur "port already in use":

```powershell
# Modifier les ports dans docker-compose.yml
# Exemple: changer 4200:80 en 4201:80
```

## 📚 Prochaines Étapes

1. Lire le [README.md](README.md) complet
2. Consulter [SAMPLE_DATA.md](SAMPLE_DATA.md) pour plus d'exemples
3. Explorer la documentation API Swagger
4. Configurer le SSO pour la production

## 💡 Conseils

- Les données de démo sont automatiquement chargées
- Le calcul de risque se lance automatiquement à 2h00 chaque jour
- Vous pouvez déclencher manuellement via l'API
- Les fichiers uploadés sont conservés en base de données

## 📞 Besoin d'Aide?

Consulter la section Troubleshooting dans [README.md](README.md)

---

**Bon développement! 🚀**
