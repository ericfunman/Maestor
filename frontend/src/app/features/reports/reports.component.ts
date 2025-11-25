import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <h2>Rapports et Exports</h2>
      
      <div class="card">
        <h3>Générer des Rapports</h3>
        <p>Fonctionnalité d'export Excel/PDF en cours de développement</p>
        
        <div class="button-group">
          <button class="btn btn-primary" (click)="exportExcel()">
            Export Excel
          </button>
          <button class="btn btn-secondary" (click)="exportPDF()">
            Export PDF
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container { padding: 20px; }
    .button-group {
      margin-top: 20px;
      display: flex;
      gap: 10px;
    }
  `]
})
export class ReportsComponent {
  exportExcel() {
    alert('Export Excel - Fonctionnalité à implémenter');
  }

  exportPDF() {
    alert('Export PDF - Fonctionnalité à implémenter');
  }
}
