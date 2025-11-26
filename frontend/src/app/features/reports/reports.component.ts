import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportService, Report } from '../../core/services/report.service';
import { Chart, ChartConfiguration, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="reports-container">
      <div class="header">
        <h2>Rapports et Dashboards</h2>
        <button class="btn-primary" (click)="showCreateForm()">
          <span>➕</span> Nouveau Rapport
        </button>
      </div>

      <!-- Liste des rapports -->
      <div class="reports-list" *ngIf="!selectedReport && !isCreating">
        <div class="report-card" *ngFor="let report of reports" (click)="viewReport(report)">
          <div class="report-header">
            <h3>{{ report.name }}</h3>
            <span class="chart-type-badge">{{ getChartTypeLabel(report.chartType) }}</span>
          </div>
          <p class="report-description">{{ report.description || 'Aucune description' }}</p>
          <div class="report-actions">
            <button class="btn-view" (click)="viewReport(report); $event.stopPropagation()">
              📊 Voir
            </button>
            <button class="btn-edit" (click)="editReport(report); $event.stopPropagation()">
              ✏️ Modifier
            </button>
            <button class="btn-delete" (click)="deleteReport(report.id!); $event.stopPropagation()">
              🗑️ Supprimer
            </button>
          </div>
        </div>
        
        <div *ngIf="reports.length === 0" class="empty-state">
          <p>Aucun rapport créé. Cliquez sur "Nouveau Rapport" pour commencer.</p>
        </div>
      </div>

      <!-- Formulaire de création/édition -->
      <div class="report-form" *ngIf="isCreating || isEditing">
        <h3>{{ isEditing ? 'Modifier le rapport' : 'Nouveau rapport' }}</h3>
        
        <div class="form-group">
          <label>Nom du rapport *</label>
          <input type="text" [(ngModel)]="currentReport.name" placeholder="Ex: Ventes par mois">
        </div>
        
        <div class="form-group">
          <label>Description</label>
          <textarea [(ngModel)]="currentReport.description" rows="2" 
                    placeholder="Description du rapport"></textarea>
        </div>
        
        <div class="form-group">
          <label>Requête SQL * (SELECT uniquement)</label>
          <textarea [(ngModel)]="currentReport.sqlQuery" rows="6" 
                    placeholder="SELECT colonne1, COUNT(*) as total FROM test_data GROUP BY colonne1"></textarea>
        </div>
        
        <div class="form-row">
          <div class="form-group">
            <label>Type de graphique *</label>
            <select [(ngModel)]="currentReport.chartType">
              <option value="bar">Barres</option>
              <option value="line">Ligne</option>
              <option value="pie">Camembert</option>
              <option value="doughnut">Donut</option>
              <option value="table">Tableau</option>
            </select>
          </div>
          
          <div class="form-group" *ngIf="currentReport.chartType !== 'table'">
            <label>Colonne axe X (Labels)</label>
            <input type="text" [(ngModel)]="currentReport.xAxisColumn" 
                   placeholder="Ex: mois, categorie">
          </div>
          
          <div class="form-group" *ngIf="currentReport.chartType !== 'table' && currentReport.chartType !== 'pie' && currentReport.chartType !== 'doughnut'">
            <label>Colonne axe Y (Valeurs)</label>
            <input type="text" [(ngModel)]="currentReport.yAxisColumn" 
                   placeholder="Ex: total, count">
          </div>
        </div>
        
        <div class="form-actions">
          <button class="btn-secondary" (click)="cancelForm()">Annuler</button>
          <button class="btn-primary" (click)="saveReport()">
            {{ isEditing ? 'Mettre à jour' : 'Créer' }}
          </button>
        </div>
      </div>

      <!-- Visualisation du rapport -->
      <div class="report-view" *ngIf="selectedReport && !isCreating && !isEditing">
        <div class="view-header">
          <div>
            <h3>{{ selectedReport.name }}</h3>
            <p>{{ selectedReport.description }}</p>
          </div>
          <button class="btn-secondary" (click)="backToList()">← Retour</button>
        </div>
        
        <div class="chart-container" *ngIf="selectedReport.chartType !== 'table'">
          <canvas #reportChart></canvas>
        </div>
        
        <div class="table-container" *ngIf="selectedReport.chartType === 'table' && selectedReport.data">
          <table>
            <thead>
              <tr>
                <th *ngFor="let col of getColumns(selectedReport.data)">{{ col }}</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let row of selectedReport.data">
                <td *ngFor="let col of getColumns(selectedReport.data)">{{ row[col] }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div class="sql-query">
          <h4>Requête SQL</h4>
          <pre>{{ selectedReport.sqlQuery }}</pre>
        </div>
      </div>
      
      <div *ngIf="loading" class="loading">Chargement...</div>
      <div *ngIf="errorMessage" class="error-message">{{ errorMessage }}</div>
    </div>
  `,
  styles: [`
    .reports-container {
      padding: 2rem;
      max-width: 1400px;
      margin: 0 auto;
    }
    
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }
    
    .header h2 {
      margin: 0;
      color: #2c3e50;
    }
    
    .btn-primary, .btn-secondary, .btn-view, .btn-edit, .btn-delete {
      padding: 0.6rem 1.2rem;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      font-size: 0.95rem;
      font-weight: 500;
      transition: all 0.3s;
    }
    
    .btn-primary {
      background: #3498db;
      color: white;
    }
    
    .btn-primary:hover {
      background: #2980b9;
    }
    
    .btn-secondary {
      background: #95a5a6;
      color: white;
    }
    
    .btn-secondary:hover {
      background: #7f8c8d;
    }
    
    .reports-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 1.5rem;
    }
    
    .report-card {
      background: white;
      border: 1px solid #e1e8ed;
      border-radius: 8px;
      padding: 1.5rem;
      cursor: pointer;
      transition: all 0.3s;
    }
    
    .report-card:hover {
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
      transform: translateY(-2px);
    }
    
    .report-header {
      display: flex;
      justify-content: space-between;
      align-items: start;
      margin-bottom: 1rem;
    }
    
    .report-header h3 {
      margin: 0;
      color: #2c3e50;
      font-size: 1.2rem;
    }
    
    .chart-type-badge {
      background: #3498db;
      color: white;
      padding: 0.3rem 0.7rem;
      border-radius: 4px;
      font-size: 0.8rem;
    }
    
    .report-description {
      color: #7f8c8d;
      margin-bottom: 1rem;
      font-size: 0.9rem;
    }
    
    .report-actions {
      display: flex;
      gap: 0.5rem;
    }
    
    .btn-view, .btn-edit, .btn-delete {
      flex: 1;
      padding: 0.5rem;
      font-size: 0.85rem;
    }
    
    .btn-view {
      background: #27ae60;
      color: white;
    }
    
    .btn-view:hover {
      background: #229954;
    }
    
    .btn-edit {
      background: #f39c12;
      color: white;
    }
    
    .btn-edit:hover {
      background: #e67e22;
    }
    
    .btn-delete {
      background: #e74c3c;
      color: white;
    }
    
    .btn-delete:hover {
      background: #c0392b;
    }
    
    .empty-state {
      text-align: center;
      padding: 3rem;
      color: #95a5a6;
    }
    
    .report-form {
      background: white;
      border-radius: 8px;
      padding: 2rem;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    
    .report-form h3 {
      margin-top: 0;
      color: #2c3e50;
    }
    
    .form-group {
      margin-bottom: 1.5rem;
    }
    
    .form-group label {
      display: block;
      margin-bottom: 0.5rem;
      color: #34495e;
      font-weight: 500;
    }
    
    .form-group input,
    .form-group textarea,
    .form-group select {
      width: 100%;
      padding: 0.7rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 0.95rem;
      font-family: inherit;
    }
    
    .form-group textarea {
      font-family: 'Courier New', monospace;
    }
    
    .form-row {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
    }
    
    .form-actions {
      display: flex;
      gap: 1rem;
      justify-content: flex-end;
      margin-top: 2rem;
    }
    
    .report-view {
      background: white;
      border-radius: 8px;
      padding: 2rem;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    
    .view-header {
      display: flex;
      justify-content: space-between;
      align-items: start;
      margin-bottom: 2rem;
    }
    
    .view-header h3 {
      margin: 0 0 0.5rem 0;
      color: #2c3e50;
    }
    
    .view-header p {
      color: #7f8c8d;
      margin: 0;
    }
    
    .chart-container {
      margin-bottom: 2rem;
      max-height: 500px;
    }
    
    .table-container {
      overflow-x: auto;
      margin-bottom: 2rem;
    }
    
    table {
      width: 100%;
      border-collapse: collapse;
    }
    
    th, td {
      padding: 0.75rem;
      text-align: left;
      border-bottom: 1px solid #e1e8ed;
    }
    
    th {
      background: #f8f9fa;
      font-weight: 600;
      color: #2c3e50;
    }
    
    tr:hover {
      background: #f8f9fa;
    }
    
    .sql-query {
      background: #f8f9fa;
      border-radius: 4px;
      padding: 1rem;
    }
    
    .sql-query h4 {
      margin-top: 0;
      color: #2c3e50;
    }
    
    .sql-query pre {
      margin: 0;
      font-family: 'Courier New', monospace;
      color: #34495e;
      white-space: pre-wrap;
    }
    
    .loading {
      text-align: center;
      padding: 2rem;
      color: #3498db;
    }
    
    .error-message {
      background: #fee;
      color: #c33;
      padding: 1rem;
      border-radius: 4px;
      margin-top: 1rem;
    }
  `]
})
export class ReportsComponent implements OnInit {
  @ViewChild('reportChart') reportChartRef!: ElementRef<HTMLCanvasElement>;
  
  reports: Report[] = [];
  selectedReport: Report | null = null;
  currentReport: Report = this.getEmptyReport();
  isCreating = false;
  isEditing = false;
  loading = false;
  errorMessage = '';
  chart: Chart | null = null;

  constructor(private reportService: ReportService) {}

  ngOnInit() {
    this.loadReports();
  }

  loadReports() {
    this.loading = true;
    this.reportService.getAllReports().subscribe({
      next: (reports) => {
        this.reports = reports;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des rapports:', err);
        this.errorMessage = 'Erreur lors du chargement des rapports';
        this.loading = false;
      }
    });
  }

  showCreateForm() {
    this.isCreating = true;
    this.currentReport = this.getEmptyReport();
  }

  editReport(report: Report) {
    this.isEditing = true;
    this.currentReport = { ...report };
  }

  cancelForm() {
    this.isCreating = false;
    this.isEditing = false;
    this.currentReport = this.getEmptyReport();
  }

  saveReport() {
    if (!this.currentReport.name || !this.currentReport.sqlQuery || !this.currentReport.chartType) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const operation = this.isEditing
      ? this.reportService.updateReport(this.currentReport.id!, this.currentReport)
      : this.reportService.createReport(this.currentReport);

    operation.subscribe({
      next: () => {
        this.loadReports();
        this.cancelForm();
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors de la sauvegarde:', err);
        this.errorMessage = err.error?.message || 'Erreur lors de la sauvegarde du rapport';
        this.loading = false;
      }
    });
  }

  viewReport(report: Report) {
    this.loading = true;
    this.errorMessage = '';
    
    this.reportService.executeReport(report.id!).subscribe({
      next: (reportWithData) => {
        this.selectedReport = reportWithData;
        this.loading = false;
        
        setTimeout(() => {
          if (reportWithData.chartType !== 'table') {
            this.renderChart(reportWithData);
          }
        }, 100);
      },
      error: (err) => {
        console.error('Erreur lors de l\'exécution du rapport:', err);
        this.errorMessage = err.error?.message || 'Erreur lors de l\'exécution du rapport';
        this.loading = false;
      }
    });
  }

  deleteReport(id: number) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce rapport ?')) {
      return;
    }

    this.reportService.deleteReport(id).subscribe({
      next: () => {
        this.loadReports();
      },
      error: (err) => {
        console.error('Erreur lors de la suppression:', err);
        this.errorMessage = 'Erreur lors de la suppression du rapport';
      }
    });
  }

  backToList() {
    this.selectedReport = null;
    if (this.chart) {
      this.chart.destroy();
      this.chart = null;
    }
  }

  renderChart(report: Report) {
    if (!report.data || report.data.length === 0) {
      this.errorMessage = 'Aucune donnée à afficher';
      return;
    }

    const canvas = this.reportChartRef?.nativeElement;
    if (!canvas) {
      return;
    }

    if (this.chart) {
      this.chart.destroy();
    }

    const labels = report.data.map(row => row[report.xAxisColumn!] || '');
    const dataValues = report.data.map(row => {
      const val = report.yAxisColumn ? row[report.yAxisColumn] : Object.values(row)[1];
      return typeof val === 'number' ? val : parseFloat(val) || 0;
    });

    const isPieOrDoughnut = report.chartType === 'pie' || report.chartType === 'doughnut';

    const config: ChartConfiguration = {
      type: report.chartType as any,
      data: {
        labels: labels,
        datasets: [{
          label: report.name,
          data: dataValues,
          backgroundColor: isPieOrDoughnut ? this.generateColors(labels.length) : 'rgba(52, 152, 219, 0.6)',
          borderColor: isPieOrDoughnut ? this.generateColors(labels.length, 1) : 'rgba(52, 152, 219, 1)',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
          legend: {
            display: isPieOrDoughnut,
            position: 'right'
          },
          title: {
            display: true,
            text: report.name
          }
        }
      }
    };

    this.chart = new Chart(canvas, config);
  }

  generateColors(count: number, alpha: number = 0.6): string[] {
    const colors = [
      `rgba(52, 152, 219, ${alpha})`,
      `rgba(46, 204, 113, ${alpha})`,
      `rgba(241, 196, 15, ${alpha})`,
      `rgba(230, 126, 34, ${alpha})`,
      `rgba(231, 76, 60, ${alpha})`,
      `rgba(155, 89, 182, ${alpha})`,
      `rgba(52, 73, 94, ${alpha})`,
      `rgba(22, 160, 133, ${alpha})`
    ];
    
    const result = [];
    for (let i = 0; i < count; i++) {
      result.push(colors[i % colors.length]);
    }
    return result;
  }

  getColumns(data: any[]): string[] {
    if (!data || data.length === 0) {
      return [];
    }
    return Object.keys(data[0]);
  }

  getChartTypeLabel(type: string): string {
    const labels: { [key: string]: string } = {
      'bar': '📊 Barres',
      'line': '📈 Ligne',
      'pie': '🥧 Camembert',
      'doughnut': '🍩 Donut',
      'table': '📋 Tableau'
    };
    return labels[type] || type;
  }

  getEmptyReport(): Report {
    return {
      name: '',
      description: '',
      sqlQuery: '',
      chartType: 'bar',
      xAxisColumn: '',
      yAxisColumn: ''
    };
  }
}
