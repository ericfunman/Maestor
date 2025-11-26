import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface TableData {
  [key: string]: any;
}

@Injectable({
  providedIn: 'root'
})
export class TableService {
  private apiUrl = `${environment.apiUrl}/tables`;

  constructor(private http: HttpClient) {}

  getAvailableTables(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/available`);
  }

  getTableColumns(tableName: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/${tableName}/columns`);
  }

  queryTable(tableName: string, filterColumn?: string, filterValue?: string, limit: number = 100): Observable<TableData[]> {
    let url = `${this.apiUrl}/${tableName}/data?limit=${limit}`;
    
    if (filterColumn && filterValue) {
      url += `&filterColumn=${encodeURIComponent(filterColumn)}&filterValue=${encodeURIComponent(filterValue)}`;
    }
    
    return this.http.get<TableData[]>(url);
  }
}
