-- Sample data for development and testing

-- Sample risk referentials
INSERT INTO operational_risk_referential (risk_code, risk_name, risk_description, risk_category, risk_type, business_line, impact_level, probability_level, active, created_at, updated_at)
VALUES 
('RISK-001', 'Fraude interne', 'Risque de fraude commise par un employé', 'FRAUD', 'INTERNAL', 'Retail Banking', 'HIGH', 'MEDIUM', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('RISK-002', 'Défaillance système IT', 'Panne ou dysfonctionnement des systèmes informatiques', 'TECHNOLOGY', 'SYSTEM', 'IT', 'HIGH', 'MEDIUM', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('RISK-003', 'Erreur de traitement', 'Erreur humaine dans le traitement des opérations', 'EXECUTION', 'HUMAN', 'Operations', 'MEDIUM', 'HIGH', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('RISK-004', 'Non-conformité réglementaire', 'Risque de non-respect des réglementations', 'COMPLIANCE', 'REGULATORY', 'Compliance', 'HIGH', 'LOW', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample incidents
INSERT INTO incident (incident_code, incident_title, incident_description, incident_date, severity, status, entity_code, business_unit, financial_impact, currency, detected_by, created_at, updated_at)
VALUES 
('INC-2024-001', 'Transaction frauduleuse détectée', 'Transaction suspecte de 50000 EUR détectée et bloquée', '2024-11-15', 'HIGH', 'CLOSED', 'ENTITY1', 'Retail Banking', 50000.00, 'EUR', 'Security Team', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('INC-2024-002', 'Panne serveur principal', 'Serveur de production hors service pendant 2 heures', '2024-11-20', 'CRITICAL', 'CLOSED', 'ENTITY2', 'IT', 0.00, 'EUR', 'IT Monitoring', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('INC-2024-003', 'Erreur de saisie client', 'Mauvais montant saisi pour virement client', '2024-11-22', 'MEDIUM', 'IN_PROGRESS', 'ENTITY1', 'Operations', 5000.00, 'EUR', 'Customer Service', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample controls
INSERT INTO control (control_code, control_name, control_description, control_type, frequency, entity_code, responsible_person, status, last_execution_date, last_execution_result, effectiveness, created_at, updated_at)
VALUES 
('CTRL-001', 'Revue des transactions suspectes', 'Analyse quotidienne des transactions pour détecter les anomalies', 'DETECTIVE', 'DAILY', 'ENTITY1', 'Security Manager', 'ACTIVE', '2024-11-24', 'PASSED', 'EFFECTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CTRL-002', 'Backup système automatique', 'Sauvegarde automatique des données critiques', 'PREVENTIVE', 'DAILY', 'ENTITY2', 'IT Manager', 'ACTIVE', '2024-11-24', 'PASSED', 'EFFECTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CTRL-003', 'Double validation virements', 'Validation par deux personnes pour virements > 10000 EUR', 'PREVENTIVE', 'DAILY', 'ENTITY1', 'Operations Manager', 'ACTIVE', '2024-11-24', 'PASSED', 'EFFECTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CTRL-004', 'Audit conformité mensuel', 'Revue mensuelle de la conformité réglementaire', 'DETECTIVE', 'MONTHLY', 'ENTITY3', 'Compliance Officer', 'ACTIVE', '2024-11-01', 'PASSED', 'PARTIALLY_EFFECTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
