import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RiskService, Incident } from '../../core/services/risk.service';

@Component({
  selector: 'app-incidents',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <h2>Incidents</h2>
      
      <div class="card">
        <table class="table">
          <thead>
            <tr>
              <th>Code</th>
              <th>Titre</th>
              <th>Date</th>
              <th>Sévérité</th>
              <th>Statut</th>
              <th>Entité</th>
              <th>Impact Financier</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let incident of incidents">
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
              <td>{{ incident.financialImpact | number:'1.2-2' }} {{ incident.currency }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .container { padding: 20px; }
  `]
})
export class IncidentsComponent implements OnInit {
  incidents: Incident[] = [];

  constructor(private riskService: RiskService) {}

  ngOnInit() {
    this.riskService.getIncidents().subscribe(data => this.incidents = data);
  }
}
