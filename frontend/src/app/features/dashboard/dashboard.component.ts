import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RiskService, RiskCalculation, Incident } from '../../core/services/risk.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard">
      <h2>Tableau de Bord</h2>
      
      <div class="stats-grid">
        <div class="stat-card">
          <h3>Incidents Totaux</h3>
          <p class="stat-number">{{ totalIncidents }}</p>
        </div>
        
        <div class="stat-card">
          <h3>Contrôles Actifs</h3>
          <p class="stat-number">{{ totalControls }}</p>
        </div>
        
        <div class="stat-card">
          <h3>Risques Critiques</h3>
          <p class="stat-number risk-critical">{{ criticalRisks }}</p>
        </div>
        
        <div class="stat-card">
          <h3>Impact Financier</h3>
          <p class="stat-number">{{ totalFinancialImpact | number:'1.2-2' }} €</p>
        </div>
      </div>
      
      <div class="card">
        <h3>Derniers Calculs de Risque</h3>
        <div *ngIf="riskCalculations.length > 0; else noData">
          <table class="table">
            <thead>
              <tr>
                <th>Entité</th>
                <th>Incidents</th>
                <th>Contrôles Actifs</th>
                <th>Impact Financier</th>
                <th>Niveau de Risque</th>
                <th>Score</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let calc of riskCalculations">
                <td>{{ calc.entityCode || 'Global' }}</td>
                <td>{{ calc.incidentCount }}</td>
                <td>{{ calc.activeControlCount }}</td>
                <td>{{ calc.totalFinancialImpact | number:'1.2-2' }} €</td>
                <td>
                  <span class="status-badge" [ngClass]="'risk-' + calc.riskLevel.toLowerCase()">
                    {{ calc.riskLevel }}
                  </span>
                </td>
                <td>{{ calc.riskScore | number:'1.2-2' }}</td>
                <td>{{ calc.calculatedAt | date:'short' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <ng-template #noData>
          <p>Aucun calcul de risque disponible</p>
        </ng-template>
      </div>
      
      <div class="card">
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <h3>Derniers Incidents</h3>
          <button class="btn btn-primary" (click)="loadData()">Actualiser</button>
        </div>
        <div *ngIf="recentIncidents.length > 0; else noIncidents">
          <table class="table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Titre</th>
                <th>Date</th>
                <th>Sévérité</th>
                <th>Statut</th>
                <th>Entité</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let incident of recentIncidents">
                <td>{{ incident.incidentCode }}</td>
                <td>{{ incident.incidentTitle }}</td>
                <td>{{ incident.incidentDate | date:'short' }}</td>
                <td>
                  <span class="status-badge" [ngClass]="'risk-' + incident.severity.toLowerCase()">
                    {{ incident.severity }}
                  </span>
                </td>
                <td>{{ incident.status }}</td>
                <td>{{ incident.entityCode }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <ng-template #noIncidents>
          <p>Aucun incident récent</p>
        </ng-template>
      </div>
    </div>
  `,
  styles: [`
    .dashboard {
      padding: 20px;
    }
    
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
    }
    
    .stat-card {
      background: white;
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    
    .stat-card h3 {
      margin: 0 0 10px 0;
      font-size: 14px;
      color: #666;
      text-transform: uppercase;
    }
    
    .stat-number {
      font-size: 32px;
      font-weight: bold;
      color: #1976d2;
      margin: 0;
    }
    
    .card {
      background: white;
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      margin-bottom: 20px;
    }
    
    .card h3 {
      margin-top: 0;
    }
  `]
})
export class DashboardComponent implements OnInit {
  riskCalculations: RiskCalculation[] = [];
  recentIncidents: Incident[] = [];
  totalIncidents = 0;
  totalControls = 0;
  criticalRisks = 0;
  totalFinancialImpact = 0;

  constructor(private riskService: RiskService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.riskService.getRiskCalculations().subscribe({
      next: (data) => {
        this.riskCalculations = data.slice(0, 10);
        this.criticalRisks = data.filter(r => r.riskLevel === 'CRITICAL').length;
        this.totalFinancialImpact = data.reduce((sum, r) => sum + r.totalFinancialImpact, 0);
      },
      error: (err) => console.error('Error loading risk calculations:', err)
    });

    this.riskService.getIncidents().subscribe({
      next: (data) => {
        this.totalIncidents = data.length;
        this.recentIncidents = data.slice(0, 10);
      },
      error: (err) => console.error('Error loading incidents:', err)
    });

    this.riskService.getControls().subscribe({
      next: (data) => {
        this.totalControls = data.filter(c => c.status === 'ACTIVE').length;
      },
      error: (err) => console.error('Error loading controls:', err)
    });
  }
}
