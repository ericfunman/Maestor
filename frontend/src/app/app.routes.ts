import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'upload',
    loadComponent: () => import('./features/file-upload/file-upload.component').then(m => m.FileUploadComponent)
  },
  {
    path: 'referentials',
    loadComponent: () => import('./features/referentials/referentials.component').then(m => m.ReferentialsComponent)
  },
  {
    path: 'incidents',
    loadComponent: () => import('./features/incidents/incidents.component').then(m => m.IncidentsComponent)
  },
  {
    path: 'controls',
    loadComponent: () => import('./features/controls/controls.component').then(m => m.ControlsComponent)
  },
  {
    path: 'reports',
    loadComponent: () => import('./features/reports/reports.component').then(m => m.ReportsComponent)
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];
