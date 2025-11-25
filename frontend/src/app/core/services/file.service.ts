import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface FileUploadResponse {
  fileId: number;
  fileName: string;
  fileType: string;
  category: string;
  fileSize: number;
  status: string;
  totalRecords: number;
  processedRecords: number;
  failedRecords: number;
  message: string;
  uploadedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private apiUrl = `${environment.apiUrl}/files`;

  constructor(private http: HttpClient) {}

  uploadFile(file: File, category: string, uploadedBy: string): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('category', category);
    formData.append('uploadedBy', uploadedBy);

    return this.http.post<FileUploadResponse>(`${this.apiUrl}/upload`, formData);
  }

  getFileStatus(fileId: number): Observable<FileUploadResponse> {
    return this.http.get<FileUploadResponse>(`${this.apiUrl}/${fileId}/status`);
  }
}
