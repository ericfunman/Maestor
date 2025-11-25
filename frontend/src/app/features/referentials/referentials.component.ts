import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RiskService, RiskReferential } from '../../core/services/risk.service';

@Component({
  selector: 'app-referentials',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <h2>Référentiels de Risques</h2>
      
      <div class="card">
        <table class="table">
          <thead>
            <tr>
              <th>Code</th>
              <th>Nom</th>
              <th>Catégorie</th>
              <th>Type</th>
              <th>Ligne Métier</th>
              <th>Impact</th>
              <th>Probabilité</th>
              <th>Statut</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let ref of referentials">
              <td>{{ ref.riskCode }}</td>
              <td>{{ ref.riskName }}</td>
              <td>{{ ref.riskCategory }}</td>
              <td>{{ ref.riskType }}</td>
              <td>{{ ref.businessLine }}</td>
              <td>
                <span class="status-badge" [ngClass]="'risk-' + ref.impactLevel.toLowerCase()">
                  {{ ref.impactLevel }}
                </span>
              </td>
              <td>
                <span class="status-badge" [ngClass]="'risk-' + ref.probabilityLevel.toLowerCase()">
                  {{ ref.probabilityLevel }}
                </span>
              </td>
              <td>{{ ref.active ? 'Actif' : 'Inactif' }}</td>
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
export class ReferentialsComponent implements OnInit {
  referentials: RiskReferential[] = [];

  constructor(private riskService: RiskService) {}

  ngOnInit() {
    this.riskService.getReferentials().subscribe(data => this.referentials = data);
  }
}
