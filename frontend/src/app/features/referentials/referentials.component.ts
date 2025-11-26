import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableService, TableData } from '../../core/services/table.service';

@Component({
  selector: 'app-referentials',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <h2>Consultation des Référentiels</h2>
      
      <div class="card">
        <div class="filters">
          <div class="form-group">
            <label for="tableSelect">Sélectionner une table :</label>
            <select id="tableSelect" [(ngModel)]="selectedTable" (change)="onTableChange()" class="form-control">
              <option value="">-- Choisir une table --</option>
              <option *ngFor="let table of availableTables" [value]="table">{{ table }}</option>
            </select>
          </div>

          <div class="form-group" *ngIf="selectedTable && columns.length > 0">
            <label for="columnFilter">Filtrer par colonne :</label>
            <select id="columnFilter" [(ngModel)]="filterColumn" class="form-control">
              <option value="">-- Toutes les colonnes --</option>
              <option *ngFor="let col of columns" [value]="col">{{ col }}</option>
            </select>
          </div>

          <div class="form-group" *ngIf="filterColumn">
            <label for="filterValue">Valeur du filtre :</label>
            <input id="filterValue" type="text" [(ngModel)]="filterValue" 
                   (keyup.enter)="applyFilter()" class="form-control" 
                   placeholder="Rechercher...">
          </div>

          <div class="form-actions" *ngIf="selectedTable">
            <button (click)="applyFilter()" class="btn btn-primary">Appliquer le filtre</button>
            <button (click)="clearFilter()" class="btn btn-secondary">Réinitialiser</button>
          </div>
        </div>
      </div>

      <div class="card" *ngIf="tableData.length > 0">
        <div class="table-info">
          <p>{{ tableData.length }} résultat(s) trouvé(s)</p>
        </div>
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th *ngFor="let col of columns">{{ col }}</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let row of tableData">
                <td *ngFor="let col of columns">{{ formatValue(row[col]) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="card" *ngIf="selectedTable && tableData.length === 0 && !loading">
        <p class="no-data">Aucune donnée trouvée pour cette table.</p>
      </div>

      <div class="card" *ngIf="loading">
        <p class="loading">Chargement en cours...</p>
      </div>
    </div>
  `,
  styles: [`
    .container { 
      padding: 20px; 
    }

    .card {
      background: white;
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 20px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .filters {
      display: flex;
      gap: 15px;
      flex-wrap: wrap;
      align-items: flex-end;
    }

    .form-group {
      flex: 1;
      min-width: 200px;
    }

    .form-group label {
      display: block;
      margin-bottom: 5px;
      font-weight: 500;
      color: #333;
    }

    .form-control {
      width: 100%;
      padding: 8px 12px;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 14px;
    }

    .form-control:focus {
      outline: none;
      border-color: #4CAF50;
    }

    .form-actions {
      display: flex;
      gap: 10px;
    }

    .btn {
      padding: 8px 16px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 14px;
      transition: background-color 0.3s;
    }

    .btn-primary {
      background: #4CAF50;
      color: white;
    }

    .btn-primary:hover {
      background: #45a049;
    }

    .btn-secondary {
      background: #6c757d;
      color: white;
    }

    .btn-secondary:hover {
      background: #5a6268;
    }

    .table-info {
      margin-bottom: 10px;
      color: #666;
      font-size: 14px;
    }

    .table-responsive {
      overflow-x: auto;
    }

    .table {
      width: 100%;
      border-collapse: collapse;
      font-size: 14px;
    }

    .table th,
    .table td {
      padding: 12px;
      text-align: left;
      border-bottom: 1px solid #ddd;
    }

    .table th {
      background: #f8f9fa;
      font-weight: 600;
      color: #333;
      position: sticky;
      top: 0;
    }

    .table tbody tr:hover {
      background: #f5f5f5;
    }

    .no-data,
    .loading {
      text-align: center;
      color: #666;
      padding: 20px;
    }
  `]
})
export class ReferentialsComponent implements OnInit {
  availableTables: string[] = [];
  selectedTable: string = '';
  columns: string[] = [];
  tableData: TableData[] = [];
  filterColumn: string = '';
  filterValue: string = '';
  loading: boolean = false;

  constructor(private tableService: TableService) {}

  ngOnInit() {
    this.loadAvailableTables();
  }

  loadAvailableTables() {
    this.tableService.getAvailableTables().subscribe({
      next: (tables) => {
        this.availableTables = tables;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des tables:', err);
      }
    });
  }

  onTableChange() {
    if (!this.selectedTable) {
      this.columns = [];
      this.tableData = [];
      return;
    }

    this.loading = true;
    this.filterColumn = '';
    this.filterValue = '';

    // Charger les colonnes
    this.tableService.getTableColumns(this.selectedTable).subscribe({
      next: (cols) => {
        this.columns = cols;
        this.loadTableData();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des colonnes:', err);
        this.loading = false;
      }
    });
  }

  loadTableData() {
    this.loading = true;
    this.tableService.queryTable(
      this.selectedTable, 
      this.filterColumn || undefined, 
      this.filterValue || undefined
    ).subscribe({
      next: (data) => {
        this.tableData = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des données:', err);
        this.loading = false;
      }
    });
  }

  applyFilter() {
    if (this.selectedTable) {
      this.loadTableData();
    }
  }

  clearFilter() {
    this.filterColumn = '';
    this.filterValue = '';
    this.loadTableData();
  }

  formatValue(value: any): string {
    if (value === null || value === undefined) {
      return '-';
    }
    if (typeof value === 'boolean') {
      return value ? 'Oui' : 'Non';
    }
    if (value instanceof Date || (typeof value === 'string' && value.match(/^\d{4}-\d{2}-\d{2}/))) {
      return new Date(value).toLocaleString('fr-FR');
    }
    return String(value);
  }
}
