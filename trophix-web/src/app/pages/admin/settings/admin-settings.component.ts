import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { AdminService } from '../../../core/services/admin.service';
import { GlobalSettings } from '../../../core/models/api.models';
import { AdminGamesComponent } from '../games/admin-games.component';

@Component({
  selector: 'app-admin-settings',
  standalone: true,
  imports: [FormsModule, AdminGamesComponent],
  templateUrl: './admin-settings.component.html',
})
export class AdminSettingsComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  
  protected readonly activeTab = signal<'general' | 'appearance' | 'moderation' | 'seo' | 'games'>('general');
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly uploadingLogo = signal(false);
  protected readonly uploadingHero = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);

  protected settings = signal<GlobalSettings>({
    siteName: 'Trophix',
    logoUrl: null,
    heroImageUrl: null,
    contactEmail: '',
    discordUrl: '',
    twitterUrl: '',
    youtubeUrl: '',
    instagramUrl: '',
    heroTitle: 'Explore e Crie Guias de Troféus',
    heroSubtitle: 'A maior comunidade de caçadores de platinas.',
    globalAlertEnabled: false,
    globalAlertText: '',
    footerText: '© 2026 Trophix Platform. Desenvolvido para a comunidade.',
    requireGuideApproval: true,
    forbiddenWords: '',
    metaTitle: 'Trophix - Guias de Troféus',
    metaDescription: 'Encontre e crie os melhores guias de platinas.',
  });

  async ngOnInit(): Promise<void> {
    await this.loadSettings();
  }

  async loadSettings() {
    this.loading.set(true);
    this.error.set(null);
    try {
      const data = await firstValueFrom(this.adminService.getGlobalSettings());
      if (data) {
        this.settings.set(data);
      }
    } catch (e) {
      this.error.set('A API do backend para /api/admin/settings ainda não responde. Carregando dados padrão (MOCK).');
    } finally {
      this.loading.set(false);
    }
  }

  async saveSettings() {
    this.saving.set(true);
    this.error.set(null);
    this.successMessage.set(null);
    try {
      await firstValueFrom(this.adminService.updateGlobalSettings(this.settings()));
      this.successMessage.set('Configurações salvas com sucesso!');
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (e) {
      this.error.set('Erro ao salvar as configurações. O endpoint PUT /api/admin/settings está configurado?');
    } finally {
      this.saving.set(false);
    }
  }

  async onLogoUpload(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    this.uploadingLogo.set(true);
    this.error.set(null);
    try {
      const res = await firstValueFrom(this.adminService.uploadMedia(file));
      this.settings.update(s => ({ ...s, logoUrl: res.url }));
    } catch (e) {
      this.error.set('Erro ao fazer upload da Logo. O endpoint POST /api/admin/media/upload está configurado?');
    } finally {
      this.uploadingLogo.set(false);
    }
  }

  async onHeroUpload(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    this.uploadingHero.set(true);
    this.error.set(null);
    try {
      const res = await firstValueFrom(this.adminService.uploadMedia(file));
      this.settings.update(s => ({ ...s, heroImageUrl: res.url }));
    } catch (e) {
      this.error.set('Erro ao fazer upload da Imagem Hero. O endpoint POST /api/admin/media/upload está configurado?');
    } finally {
      this.uploadingHero.set(false);
    }
  }
}
