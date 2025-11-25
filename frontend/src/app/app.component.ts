import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="app-container">
      <header class="app-header">
        <div class="header-content">
          <h1>Maestror</h1>
          <p>Gestion des Risques Opérationnels - Crédit Agricole</p>
        </div>
        <nav class="main-nav">
          <a routerLink="/dashboard" routerLinkActive="active">Tableau de Bord</a>
          <a routerLink="/upload" routerLinkActive="active">Upload Fichiers</a>
          <a routerLink="/referentials" routerLinkActive="active">Référentiels</a>
          <a routerLink="/incidents" routerLinkActive="active">Incidents</a>
          <a routerLink="/controls" routerLinkActive="active">Contrôles</a>
          <a routerLink="/reports" routerLinkActive="active">Rapports</a>
        </nav>
      </header>
      
      <main class="app-content">
        <router-outlet></router-outlet>
      </main>
      
      <footer class="app-footer">
        <p>&copy; 2025 Crédit Agricole - Maestror v1.0.0</p>
      </footer>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }
    
    .app-header {
      background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
      color: white;
      padding: 20px 0;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    
    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 20px;
    }
    
    .header-content h1 {
      margin: 0;
      font-size: 32px;
      font-weight: 500;
    }
    
    .header-content p {
      margin: 5px 0 0 0;
      opacity: 0.9;
    }
    
    .main-nav {
      max-width: 1200px;
      margin: 20px auto 0;
      padding: 0 20px;
      display: flex;
      gap: 20px;
    }
    
    .main-nav a {
      color: white;
      text-decoration: none;
      padding: 10px 15px;
      border-radius: 4px;
      transition: background-color 0.3s ease;
    }
    
    .main-nav a:hover,
    .main-nav a.active {
      background-color: rgba(255,255,255,0.2);
    }
    
    .app-content {
      flex: 1;
      max-width: 1200px;
      width: 100%;
      margin: 0 auto;
      padding: 20px;
    }
    
    .app-footer {
      background-color: #333;
      color: white;
      text-align: center;
      padding: 20px;
      margin-top: auto;
    }
    
    .app-footer p {
      margin: 0;
    }
  `]
})
export class AppComponent {
  title = 'maestror-frontend';
}
