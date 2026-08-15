import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { marked } from 'marked';

import { AdminService } from '../../../core/services/admin.service';
import { ApiService } from '../../../core/services/api.service';
import { GuideResponse, TrophyStatus } from '../../../core/models/api.models';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-admin-all-guides',
  standalone: true,
  imports: [DatePipe, FormsModule, PaginationComponent, RouterLink],
  templateUrl: './admin-all-guides.component.html',
})
export class AdminAllGuidesComponent implements OnInit {
  private readonly adminApi = inject(AdminService);
  private readonly api = inject(ApiService);

  protected readonly guides = signal<GuideResponse[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);

  protected readonly processingId = signal<string | null>(null);

  // Pagination
  protected readonly currentPage = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly totalElements = signal(0);

  // Edit state
  protected editingGuideId = signal<string | null>(null);
  protected editTitle = '';
  protected editDescription = '';
  protected editContent = '';
  protected editVideoUrl = '';

  // Preview state
  protected previewStates = signal<Record<string, boolean>>({});

  // Inline Trophies state
  protected editingGameTrophies = signal<TrophyStatus[]>([]);
  protected trophyTips = signal<Record<string, { guideId?: string, content: string, videoUrl: string, isSaving: boolean, isExpanded: boolean, isPreviewingTip: boolean }>>({});

  protected statusFilter = signal<string>('');
  protected searchQuery = signal<string>('');
  protected viewMode = signal<'cards' | 'table'>('cards');
  private searchTimeout: any;

  togglePreview(guideId: string): void {
    this.previewStates.update(states => ({
      ...states,
      [guideId]: !this.isPreviewing(guideId)
    }));
  }

  isPreviewing(guideId: string): boolean {
    const state = this.previewStates()[guideId];
    return state === undefined ? true : state; // True by default
  }

  getParsedContent(raw: string): string {
    if (!raw) return '';
    return marked.parse(raw) as string;
  }

  async ngOnInit(): Promise<void> {
    await this.loadGuides();
  }

  onSearchInput(event: Event) {
    const input = (event.target as HTMLInputElement).value;
    this.searchQuery.set(input);
    if (this.searchTimeout) clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => {
      this.loadGuides(0);
    }, 500);
  }

  setStatusFilter(status: string) {
    this.statusFilter.set(status);
    this.loadGuides(0);
  }

  setViewMode(mode: 'cards' | 'table') {
    this.viewMode.set(mode);
  }

  async loadGuides(pageIndex = 0): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    try {
      const page = await firstValueFrom(this.adminApi.getAllGuides(pageIndex, 20, this.statusFilter(), this.searchQuery()));
      this.guides.set(page.content);
      this.currentPage.set(page.number);
      this.totalPages.set(page.totalPages);
      this.totalElements.set(page.totalElements);
    } catch (e) {
      this.error.set('Falha ao carregar guias. Verifique se a API /api/admin/guides já foi implementada.');
    } finally {
      this.loading.set(false);
    }
  }

  async deleteGuide(guideId: string): Promise<void> {
    if (!confirm('Tem certeza que deseja excluir definitivamente este guia?')) return;
    
    this.processingId.set(guideId);
    try {
      await firstValueFrom(this.adminApi.deleteGuide(guideId));
      this.guides.update(list => list.filter(g => g.id !== guideId));
      this.successMessage.set('Guia excluído com sucesso.');
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (e) {
      this.error.set('Erro ao excluir guia.');
    } finally {
      this.processingId.set(null);
    }
  }

  async rejectGuide(guideId: string): Promise<void> {
    if (!confirm('Tem certeza que deseja rejeitar e excluir este guia?')) return;
    
    this.processingId.set(guideId);
    try {
      await firstValueFrom(this.adminApi.rejectGuide(guideId));
      this.guides.update(list => list.filter(g => g.id !== guideId));
      this.successMessage.set('Guia rejeitado e removido da fila.');
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (e) {
      this.error.set('Erro ao rejeitar guia.');
    } finally {
      this.processingId.set(null);
    }
  }

  async startEditing(guide: GuideResponse): Promise<void> {
    this.viewMode.set('cards');
    this.editingGuideId.set(guide.id);
    this.editTitle = guide.title;
    this.editDescription = guide.description;
    this.editContent = guide.content;
    this.editVideoUrl = guide.videoUrl || '';

    // Scroll to the card after Angular renders it
    setTimeout(() => {
      const element = document.getElementById('guide-' + guide.id);
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    }, 50);

    if (!guide.trophyId) {
      try {
        const trophies = await firstValueFrom(this.api.getGameTrophies(guide.gameId));
        this.editingGameTrophies.set(trophies);

        const authorGuides = await firstValueFrom(this.api.getAuthorTrophyGuides(guide.gameId, guide.authorId));
        
        const tipsMap: Record<string, any> = {};
        for (const t of trophies) {
          const existing = authorGuides.find(g => g.trophyId === t.id);
          tipsMap[t.id] = {
            guideId: existing?.id,
            content: existing?.content || '',
            videoUrl: existing?.videoUrl || '',
            isSaving: false,
            isExpanded: !!existing,
            isPreviewingTip: false
          };
        }
        this.trophyTips.set(tipsMap);
      } catch (e) {
        console.error('Falha ao carregar troféus para edição', e);
        this.editingGameTrophies.set([]);
      }
    } else {
      this.editingGameTrophies.set([]);
    }
  }

  async syncTrophies(gameId: string): Promise<void> {
    const guide = this.guides().find(g => g.id === this.editingGuideId());
    if (!guide) return;

    this.processingId.set(guide.id);
    try {
      await firstValueFrom(this.api.syncGameTrophies(gameId));
      
      let trophies: any[] = [];
      // Poll por até 10 segundos para dar tempo do backend processar e salvar no banco
      for (let i = 0; i < 10; i++) {
        await new Promise(resolve => setTimeout(resolve, 1000));
        trophies = await firstValueFrom(this.api.getGameTrophies(gameId));
        if (trophies && trophies.length > 0) {
          break;
        }
      }
      
      this.editingGameTrophies.set(trophies);
      
      const authorGuides = await firstValueFrom(this.api.getAuthorTrophyGuides(gameId, guide.authorId));
      
      const tipsMap: Record<string, any> = {};
      for (const t of trophies) {
        const existing = authorGuides.find(g => g.trophyId === t.id);
        tipsMap[t.id] = {
          guideId: existing?.id,
          content: existing?.content || '',
          videoUrl: existing?.videoUrl || '',
          isSaving: false,
          isExpanded: !!existing,
          isPreviewingTip: false
        };
      }
      this.trophyTips.set(tipsMap);
      
      this.successMessage.set('Troféus sincronizados com sucesso!');
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (e) {
      console.error('Falha ao sincronizar troféus', e);
      this.error.set('Erro ao sincronizar troféus com a PSN.');
      setTimeout(() => this.error.set(null), 4000);
    } finally {
      this.processingId.set(null);
    }
  }

  toggleTrophyTip(trophyId: string) {
    this.trophyTips.update(map => {
      const tip = map[trophyId];
      if (tip) tip.isExpanded = !tip.isExpanded;
      return { ...map };
    });
  }

  togglePreviewTip(trophyId: string) {
    this.trophyTips.update(map => {
      const tip = map[trophyId];
      if (tip) tip.isPreviewingTip = !tip.isPreviewingTip;
      return { ...map };
    });
  }

  async saveTrophyTip(trophyId: string, trophyName: string) {
    const tip = this.trophyTips()[trophyId];
    if (!tip || (!tip.content && !tip.videoUrl)) return;

    this.trophyTips.update(map => { map[trophyId].isSaving = true; return { ...map }; });
    try {
      const payload = {
        title: `Dica: ${trophyName}`,
        description: '',
        content: tip.content,
        videoUrl: tip.videoUrl || undefined
      };

      if (tip.guideId) {
        await firstValueFrom(this.adminApi.updateGuide(tip.guideId, payload));
      } else {
        await firstValueFrom(this.api.submitTrophyGuide(trophyId, payload));
        // Simple reload to fetch newly created guideIds (could be optimized)
        const guide = this.guides().find(g => g.id === this.editingGuideId());
        if (guide) {
           const authorGuides = await firstValueFrom(this.api.getAuthorTrophyGuides(guide.gameId, guide.authorId));
           const created = authorGuides.find(g => g.trophyId === trophyId);
           if (created) tip.guideId = created.id;
        }
      }
      this.successMessage.set(`Dica salva com sucesso!`);
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (e) {
      this.error.set(`Erro ao salvar dica.`);
    } finally {
      this.trophyTips.update(map => { map[trophyId].isSaving = false; return { ...map }; });
    }
  }

  cancelEditing(): void {
    this.editingGuideId.set(null);
    this.editingGameTrophies.set([]);
  }

  async updateGuide(guideId: string): Promise<void> {
    this.processingId.set(guideId);
    this.error.set(null);
    try {
      await firstValueFrom(this.adminApi.updateGuide(guideId, {
        title: this.editTitle,
        description: this.editDescription,
        content: this.editContent,
        videoUrl: this.editVideoUrl || null
      }));
      this.successMessage.set('Guia atualizado com sucesso!');
      this.editingGuideId.set(null);
      this.editingGameTrophies.set([]);
      this.loadGuides(this.currentPage());
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (e) {
      this.error.set('Erro ao atualizar o guia.');
    } finally {
      this.processingId.set(null);
    }
  }
}
