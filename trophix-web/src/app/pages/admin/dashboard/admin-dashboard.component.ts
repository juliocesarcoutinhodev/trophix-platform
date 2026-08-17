import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { AdminService } from '../../../core/services/admin.service';
import { AdminDashboardStats } from '../../../core/models/api.models';
import { OfferService, Offer } from '../../offers/offer.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './admin-dashboard.component.html',
})
export class AdminDashboardComponent implements OnInit {
  private readonly adminApi = inject(AdminService);
  private readonly offerService = inject(OfferService);

  stats = signal<AdminDashboardStats | null>(null);
  topOffers = signal<Offer[]>([]);

  sidecarStatus = signal<'checking' | 'online' | 'offline' | 'unknown'>('unknown');
  sidecarLatency = signal<number | null>(null);
  sidecarLastChecked = signal<Date | null>(null);

  backendStatus = signal<'checking' | 'online' | 'offline' | 'unknown'>('unknown');
  backendLatency = signal<number | null>(null);
  backendLastChecked = signal<Date | null>(null);

  async ngOnInit() {
    try {
      const data = await firstValueFrom(this.adminApi.getDashboardStats());
      this.stats.set(data);
    } catch (e) {
      console.error('Erro ao carregar dados do dashboard admin:', e);
    }

    try {
      const offersData = await firstValueFrom(this.offerService.getTopClickedOffers(5));
      this.topOffers.set(offersData);
    } catch (e) {
      console.error('Erro ao carregar top ofertas:', e);
      // Se a API ainda não estiver pronta, carrega dados visuais falsos para não quebrar a UI
      this.topOffers.set([
        { id: '1', title: 'Carregando dados reais...', storeName: 'Sistema', clickCount: 150 } as any,
        { id: '2', title: 'Integração em andamento...', storeName: 'Sistema', clickCount: 85 } as any,
      ]);
    }
    
    // Auto-check no sidecar e no backend ao carregar a página
    this.checkSidecarStatus();
    this.checkBackendStatus();
  }

  async checkSidecarStatus() {
    if (this.sidecarStatus() === 'checking') return;
    
    this.sidecarStatus.set('checking');
    this.sidecarLatency.set(null);
    const start = Date.now();
    
    try {
      const res = await firstValueFrom(this.adminApi.checkSidecarStatus());
      
      // O Angular HttpClient vai tratar 2xx como sucesso.
      // Mesmo assim, vamos validar se o JSON não veio como DOWN por algum motivo obscuro (se o backend retornar 200 com DOWN)
      if (res && res.status === 'DOWN') {
        throw new Error('Backend retornou 200 OK mas com payload status=DOWN');
      }

      const latency = Date.now() - start;
      this.sidecarStatus.set('online');
      this.sidecarLatency.set(latency);
    } catch (e) {
      console.error('Erro ao verificar status do sidecar:', e);
      this.sidecarStatus.set('offline');
    } finally {
      this.sidecarLastChecked.set(new Date());
    }
  }

  async checkBackendStatus() {
    if (this.backendStatus() === 'checking') return;
    
    this.backendStatus.set('checking');
    this.backendLatency.set(null);
    const start = Date.now();
    
    try {
      await firstValueFrom(this.adminApi.checkBackendStatus());
      const latency = Date.now() - start;
      this.backendStatus.set('online');
      this.backendLatency.set(latency);
    } catch (e) {
      console.error('Erro ao verificar status do backend:', e);
      this.backendStatus.set('offline');
    } finally {
      this.backendLastChecked.set(new Date());
    }
  }
}
