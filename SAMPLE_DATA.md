# Exemples de fichiers CSV pour Maestror

## 1. Référentiels de Risques (referentials.csv)

```csv
riskCode,riskName,riskDescription,riskCategory,riskType,businessLine,impactLevel,probabilityLevel
RISK-001,Fraude interne,Risque de fraude commise par un employé,FRAUD,INTERNAL,Retail Banking,HIGH,MEDIUM
RISK-002,Défaillance système IT,Panne ou dysfonctionnement des systèmes informatiques,TECHNOLOGY,SYSTEM,IT,HIGH,MEDIUM
RISK-003,Erreur de traitement,Erreur humaine dans le traitement des opérations,EXECUTION,HUMAN,Operations,MEDIUM,HIGH
RISK-004,Non-conformité réglementaire,Risque de non-respect des réglementations,COMPLIANCE,REGULATORY,Compliance,HIGH,LOW
RISK-005,Cyber-attaque,Risque d'intrusion ou attaque informatique,TECHNOLOGY,EXTERNAL,IT,CRITICAL,HIGH
```

## 2. Incidents (incidents.csv)

```csv
incidentCode,incidentTitle,incidentDescription,incidentDate,severity,status,entityCode,businessUnit,financialImpact,currency,detectedBy
INC-2024-001,Transaction frauduleuse détectée,Transaction suspecte de 50000 EUR détectée et bloquée,2024-11-15,HIGH,CLOSED,ENTITY1,Retail Banking,50000.00,EUR,Security Team
INC-2024-002,Panne serveur principal,Serveur de production hors service pendant 2 heures,2024-11-20,CRITICAL,CLOSED,ENTITY2,IT,0.00,EUR,IT Monitoring
INC-2024-003,Erreur de saisie client,Mauvais montant saisi pour virement client,2024-11-22,MEDIUM,IN_PROGRESS,ENTITY1,Operations,5000.00,EUR,Customer Service
INC-2024-004,Tentative de phishing,Email de phishing ciblant des employés,2024-11-23,HIGH,OPEN,ENTITY3,Security,0.00,EUR,Security Team
INC-2024-005,Erreur de reporting,Données incorrectes dans rapport réglementaire,2024-11-24,HIGH,IN_PROGRESS,ENTITY2,Compliance,0.00,EUR,Audit
```

## 3. Contrôles (controls.csv)

```csv
controlCode,controlName,controlDescription,controlType,frequency,entityCode,responsiblePerson,status,effectiveness
CTRL-001,Revue des transactions suspectes,Analyse quotidienne des transactions pour détecter les anomalies,DETECTIVE,DAILY,ENTITY1,Security Manager,ACTIVE,EFFECTIVE
CTRL-002,Backup système automatique,Sauvegarde automatique des données critiques,PREVENTIVE,DAILY,ENTITY2,IT Manager,ACTIVE,EFFECTIVE
CTRL-003,Double validation virements,Validation par deux personnes pour virements > 10000 EUR,PREVENTIVE,DAILY,ENTITY1,Operations Manager,ACTIVE,EFFECTIVE
CTRL-004,Audit conformité mensuel,Revue mensuelle de la conformité réglementaire,DETECTIVE,MONTHLY,ENTITY3,Compliance Officer,ACTIVE,PARTIALLY_EFFECTIVE
CTRL-005,Formation sécurité,Formation trimestrielle sur la cybersécurité,PREVENTIVE,QUARTERLY,ENTITY1,HR Manager,ACTIVE,EFFECTIVE
CTRL-006,Tests de pénétration,Tests annuels de sécurité informatique,DETECTIVE,YEARLY,ENTITY2,Security Team,ACTIVE,EFFECTIVE
CTRL-007,Réconciliation comptable,Rapprochement quotidien des comptes,DETECTIVE,DAILY,ENTITY1,Finance Manager,ACTIVE,EFFECTIVE
CTRL-008,Analyse des logs,Analyse hebdomadaire des logs système,DETECTIVE,WEEKLY,ENTITY2,IT Security,ACTIVE,EFFECTIVE
```

## Instructions d'utilisation

1. Créer des fichiers CSV avec les en-têtes ci-dessus
2. Remplir avec vos données
3. Uploader via l'interface web (http://localhost:4200/upload)
4. Sélectionner la catégorie appropriée (REFERENTIAL, INCIDENT, ou CONTROL)
5. Suivre le statut de traitement

## Notes importantes

- Les dates doivent être au format ISO: YYYY-MM-DD
- Les montants financiers doivent utiliser le point comme séparateur décimal
- Respecter les valeurs énumérées pour les champs comme severity, status, etc.
- Les fichiers Excel (.xlsx) sont également supportés avec la même structure
