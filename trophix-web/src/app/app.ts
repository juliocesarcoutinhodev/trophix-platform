import { Component, inject } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';

import { NavbarComponent } from './layout/navbar/navbar.component';
import { FooterComponent } from './layout/footer/footer.component';
import { MaintenanceComponent } from './shared/components/maintenance/maintenance.component';
import { MaintenanceService } from './core/services/maintenance.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, FooterComponent, MaintenanceComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  private readonly router = inject(Router);
  public readonly maintenanceService = inject(MaintenanceService);

  get isAdminRoute(): boolean {
    return this.router.url.startsWith('/admin');
  }
}
