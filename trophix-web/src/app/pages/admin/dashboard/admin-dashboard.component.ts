import { Component, inject, OnInit, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  templateUrl: './admin-dashboard.component.html',
})
export class AdminDashboardComponent implements OnInit {
  private readonly adminApi = inject(AdminService);

  newUsersCount = signal<number>(0);
  pendingGuidesCount = signal<number>(0);
  syncsCount = signal<number>(0);
  reportsCount = signal<number>(0);

  async ngOnInit() {
    try {
      // Busca a primeira página com tamanho 1 só para pegar o totalElements
      const pendingPage = await firstValueFrom(this.adminApi.getPendingGuides(0, 1));
      this.pendingGuidesCount.set(pendingPage.totalElements);
    } catch (e) {
      console.error('Erro ao carregar dados do dashboard admin:', e);
    }
  }
}
