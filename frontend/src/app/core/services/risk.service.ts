import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface RiskReferential {
  id: number;
  riskCode: string;
  riskName: string;
  riskDescription: string;
  riskCategory: string;
  riskType: string;
  businessLine: string;
  impactLevel: string;
  probabilityLevel: string;
  active: boolean;
}

export interface Incident {
  id: number;
  incidentCode: string;
  incidentTitle: string;
  incidentDescription: string;
  incidentDate: string;
  severity: string;
  status: string;
  entityCode: string;
  businessUnit: string;
  financialImpact: number;
  currency: string;
}

export interface Control {
  id: number;
  controlCode: string;
  controlName: string;
  controlDescription: string;
  controlType: string;
  frequency: string;
  entityCode: string;
  responsiblePerson: string;
  status: string;
  effectiveness: string;
}

export interface RiskCalculation {
  id: number;
  entityCode: string;
  incidentCount: number;
  activeControlCount: number;
  effectiveControlCount: number;
  totalFinancialImpact: number;
  riskLevel: string;
  riskScore: number;
  calculatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class RiskService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Referentials
  getReferentials(): Observable<RiskReferential[]> {
    return this.http.get<RiskReferential[]>(`${this.apiUrl}/referentials`);
  }

  getReferentialById(id: number): Observable<RiskReferential> {
    return this.http.get<RiskReferential>(`${this.apiUrl}/referentials/${id}`);
  }

  // Incidents
  getIncidents(): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.apiUrl}/incidents`);
  }

  getIncidentsByEntity(entityCode: string): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.apiUrl}/incidents/entity/${entityCode}`);
  }

  // Controls
  getControls(): Observable<Control[]> {
    return this.http.get<Control[]>(`${this.apiUrl}/controls`);
  }

  getControlsByEntity(entityCode: string): Observable<Control[]> {
    return this.http.get<Control[]>(`${this.apiUrl}/controls/entity/${entityCode}`);
  }

  // Risk Calculations
  getRiskCalculations(): Observable<RiskCalculation[]> {
    return this.http.get<RiskCalculation[]>(`${this.apiUrl}/risk-calculations`);
  }

  triggerRiskCalculation(): Observable<string> {
    return this.http.post(`${this.apiUrl}/risk-calculations/calculate`, {}, { responseType: 'text' });
  }
}
