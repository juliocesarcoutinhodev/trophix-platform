import { Component, inject, OnInit, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AdminService } from '../../../core/services/admin.service';
import { AdminDashboardStats } from '../../../core/models/api.models';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  templateUrl: './admin-dashboard.component.html',
})
export class AdminDashboardComponent implements OnInit {
  private readonly adminApi = inject(AdminService);

  stats = signal<AdminDashboardStats | null>(null);

  async ngOnInit() {
    try {
      const data = await firstValueFrom(this.adminApi.getDashboardStats());
      this.stats.set(data);
    } catch (e) {
      console.error('Erro ao carregar dados do dashboard admin:', e);
    }
  }
}
