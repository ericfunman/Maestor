import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FileService, FileUploadResponse } from '../../core/services/file.service';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="upload-container">
      <h2>Upload de Fichiers</h2>
      
      <div class="card">
        <h3>Charger un nouveau fichier</h3>
        
        <div class="form-group">
          <label>Type de fichier</label>
          <select [(ngModel)]="selectedCategory" class="form-control">
            <option value="REFERENTIAL">Référentiel de Risques</option>
            <option value="INCIDENT">Incidents</option>
            <option value="CONTROL">Contrôles</option>
            <option value="TEST">Test</option>
          </select>
        </div>
        
        <div class="form-group">
          <label>Chargé par</label>
          <input type="text" [(ngModel)]="uploadedBy" class="form-control" placeholder="Votre nom">
        </div>
        
        <div class="form-group">
          <label>Fichier (CSV, Excel ou TXT)</label>
          <input type="file" (change)="onFileSelected($event)" accept=".csv,.xlsx,.xls,.txt" class="form-control">
        </div>
        
        <button class="btn btn-primary" (click)="uploadFile()" [disabled]="!selectedFile || uploading">
          {{ uploading ? 'Upload en cours...' : 'Uploader' }}
        </button>
        
        <div *ngIf="uploadResponse" class="response-message" [ngClass]="uploadResponse.status">
          <p><strong>{{ uploadResponse.message }}</strong></p>
          <p>Fichier: {{ uploadResponse.fileName }}</p>
          <p>Statut: {{ uploadResponse.status }}</p>
          <p *ngIf="uploadResponse.totalRecords">Records: {{ uploadResponse.processedRecords }}/{{ uploadResponse.totalRecords }}</p>
        </div>
      </div>
      
      <div class="card" *ngIf="uploadHistory.length > 0">
        <h3>Historique des uploads</h3>
        <table class="table">
          <thead>
            <tr>
              <th>Fichier</th>
              <th>Type</th>
              <th>Catégorie</th>
              <th>Taille</th>
              <th>Statut</th>
              <th>Records</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let file of uploadHistory">
              <td>{{ file.fileName }}</td>
              <td>{{ file.fileType }}</td>
              <td>{{ file.category }}</td>
              <td>{{ (file.fileSize / 1024) | number:'1.0-0' }} KB</td>
              <td>
                <span class="status-badge" [ngClass]="'status-' + file.status.toLowerCase()">
                  {{ file.status }}
                </span>
              </td>
              <td>{{ file.processedRecords }}/{{ file.totalRecords }}</td>
              <td>{{ file.uploadedAt | date:'short' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .upload-container {
      padding: 20px;
    }
    
    .form-group {
      margin-bottom: 15px;
    }
    
    .form-group label {
      display: block;
      margin-bottom: 5px;
      font-weight: 500;
    }
    
    .form-control {
      width: 100%;
      padding: 10px;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 14px;
    }
    
    .response-message {
      margin-top: 20px;
      padding: 15px;
      border-radius: 4px;
    }
    
    .response-message.COMPLETED {
      background-color: #e8f5e9;
      border: 1px solid #4caf50;
    }
    
    .response-message.FAILED {
      background-color: #ffebee;
      border: 1px solid #f44336;
    }
    
    .response-message.PROCESSING {
      background-color: #fff3e0;
      border: 1px solid #ff9800;
    }
  `]
})
export class FileUploadComponent {
  selectedFile: File | null = null;
  selectedCategory = 'REFERENTIAL';
  uploadedBy = 'Admin';
  uploading = false;
  uploadResponse: FileUploadResponse | null = null;
  uploadHistory: FileUploadResponse[] = [];

  constructor(private fileService: FileService) {}

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  uploadFile() {
    if (!this.selectedFile) return;

    this.uploading = true;
    this.uploadResponse = null;

    this.fileService.uploadFile(this.selectedFile, this.selectedCategory, this.uploadedBy).subscribe({
      next: (response) => {
        this.uploadResponse = response;
        this.uploadHistory.unshift(response);
        this.uploading = false;
        this.selectedFile = null;
      },
      error: (error) => {
        console.error('Upload error:', error);
        this.uploading = false;
      }
    });
  }
}
