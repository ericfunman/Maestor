import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RiskService, Control } from '../../core/services/risk.service';

@Component({
  selector: 'app-controls',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <h2>Contrôles</h2>
      
      <div class="card">
        <table class="table">
          <thead>
            <tr>
              <th>Code</th>
              <th>Nom</th>
              <th>Type</th>
              <th>Fréquence</th>
              <th>Entité</th>
              <th>Responsable</th>
              <th>Efficacité</th>
              <th>Statut</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let control of controls">
              <td>{{ control.controlCode }}</td>
              <td>{{ control.controlName }}</td>
              <td>{{ control.controlType }}</td>
              <td>{{ control.frequency }}</td>
              <td>{{ control.entityCode }}</td>
              <td>{{ control.responsiblePerson }}</td>
              <td>
                <span class="status-badge" [ngClass]="getEffectivenessClass(control.effectiveness)">
                  {{ control.effectiveness }}
                </span>
              </td>
              <td>{{ control.status }}</td>
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
export class ControlsComponent implements OnInit {
  controls: Control[] = [];

  constructor(private riskService: RiskService) {}

  ngOnInit() {
    this.riskService.getControls().subscribe(data => this.controls = data);
  }

  getEffectivenessClass(effectiveness: string): string {
    switch (effectiveness) {
      case 'EFFECTIVE': return 'risk-low';
      case 'PARTIALLY_EFFECTIVE': return 'risk-medium';
      case 'INEFFECTIVE': return 'risk-high';
      default: return '';
    }
  }
}
