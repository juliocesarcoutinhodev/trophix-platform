import { Component, inject, OnInit, signal, OnDestroy } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { firstValueFrom, Subscription } from 'rxjs';
import { marked } from 'marked';

import { AdminService } from '../../../core/services/admin.service';
import { ApiService } from '../../../core/services/api.service';
import { GuideResponse, TrophyStatus } from '../../../core/models/api.models';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { ModalComponent } from '../../../shared/components/modal/modal.component';
import { AiSpinnerComponent } from '../../../shared/components/ai-spinner/ai-spinner.component';

@Component({
  selector: 'app-admin-all-guides',
  standalone: true,
  imports: [CommonModule, DatePipe, FormsModule, PaginationComponent, RouterLink, ModalComponent, AiSpinnerComponent],
  templateUrl: './admin-all-guides.component.html',
})
export class AdminAllGuidesComponent implements OnInit, OnDestroy {
  private readonly adminApi = inject(AdminService);
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private routeSub?: Subscription;

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
  protected activeEditTab = signal<'write' | 'preview'>('write');

  // Preview state
  protected previewStates = signal<Record<string, boolean>>({});

  // Inline Trophies state
  protected editingGameTrophies = signal<TrophyStatus[]>([]);
  protected trophyTips = signal<Record<string, { guideId?: string, content: string, videoUrl: string, isSaving: boolean, isExpanded: boolean, isPreviewingTip: boolean }>>({});

  protected statusFilter = signal<string>('');
  protected searchQuery = signal<string>('');
  protected readonly typeFilter = signal<'game' | 'trophy' | undefined>(undefined);
  protected readonly viewMode = signal<'cards' | 'table'>('table');
  private searchTimeout: any;

  // PSN Import State
  protected readonly importModalOpen = signal(false);
  protected readonly importSearchQuery = signal('');
  protected readonly importLoading = signal(false);

  // AI Generation State
  protected readonly isGeneratingMainGuide = signal(false);
  protected readonly generatingTips = signal<Record<string, boolean>>({});

  protected openImportModal(): void {
    this.importSearchQuery.set('');
    this.importModalOpen.set(true);
  }

  protected closeImportModal(): void {
    this.importModalOpen.set(false);
  }

  protected async importGame(): Promise<void> {
    if (!this.importSearchQuery().trim()) return;
    
    this.importLoading.set(true);
    this.error.set(null);
    
    try {
      await firstValueFrom(this.api.importGameFromPsn(this.importSearchQuery().trim()));
      this.successMessage.set('Jogo importado com sucesso da PSN!');
      this.closeImportModal();
      // Optional: Could navigate to create guide or just show success
    } catch (e: any) {
      this.error.set(e.error?.detalhe || e.error?.error || 'Erro ao importar jogo. Verifique se o ID está correto ou se a PSN está respondendo.');
    } finally {
      this.importLoading.set(false);
    }
  }

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

  ngOnInit(): void {
    this.routeSub = this.route.queryParams.subscribe(params => {
      const type = params['type'];
      if (type === 'game' || type === 'trophy') {
        this.typeFilter.set(type);
      } else {
        this.typeFilter.set(undefined);
      }
      this.loadGuides(0);
    });
  }

  ngOnDestroy(): void {
    this.routeSub?.unsubscribe();
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
      const page = await firstValueFrom(this.adminApi.getAllGuides(pageIndex, 20, this.statusFilter(), this.searchQuery(), this.typeFilter()));
      this.guides.set(page.content);
      this.currentPage.set(page.number);
      this.totalPages.set(page.totalPages);
      this.totalElements.set(page.totalElements);

      // Retoma o polling se algum guia estiver em geração
      page.content.forEach(guide => {
        if (guide.content === 'Gerando conteúdo...') {
          this.isGeneratingMainGuide.set(true);
          this.pollMainGuideGeneration(guide.id);
        }
      });
    } catch (e) {
      this.error.set('Falha ao carregar guias. Verifique se a API /api/admin/guides já foi implementada.');
    } finally {
      this.loading.set(false);
    }
  }
  // Confirm Modal state
  protected confirmModalOpen = signal(false);
  protected confirmModalTitle = signal('');
  protected confirmModalMessage = signal('');
  private pendingAction: (() => Promise<void>) | null = null;

  deleteGuide(guideId: string): void {
    this.confirmModalTitle.set('Excluir Guia');
    this.confirmModalMessage.set('Tem certeza que deseja excluir definitivamente este guia?');
    this.pendingAction = async () => {
      this.processingId.set(guideId);
      try {
        await firstValueFrom(this.adminApi.deleteGuide(guideId));
        this.guides.update(list => list.filter(g => g.id !== guideId));
        this.successMessage.set('Guia excluído com sucesso.');
        setTimeout(() => this.successMessage.set(null), 3000);
      } catch (e: any) {
        console.error('Erro na API de exclusão:', e);
        this.error.set('Erro ao excluir guia: ' + (e?.message || 'Erro desconhecido'));
      } finally {
        this.processingId.set(null);
      }
    };
    this.confirmModalOpen.set(true);
  }

  rejectGuide(guideId: string): void {
    this.confirmModalTitle.set('Rejeitar Guia');
    this.confirmModalMessage.set('Tem certeza que deseja rejeitar e excluir este guia?');
    this.pendingAction = async () => {
      this.processingId.set(guideId);
      try {
        await firstValueFrom(this.adminApi.rejectGuide(guideId));
        this.guides.update(list => list.filter(g => g.id !== guideId));
        this.successMessage.set('Guia rejeitado e removido da fila.');
        setTimeout(() => this.successMessage.set(null), 3000);
      } catch (e: any) {
        console.error('Erro na API de rejeição:', e);
        this.error.set('Erro ao rejeitar guia: ' + (e?.message || 'Erro desconhecido'));
      } finally {
        this.processingId.set(null);
      }
    };
    this.confirmModalOpen.set(true);
  }

  protected executePendingAction(): void {
    console.log('executePendingAction called. pendingAction exists?', !!this.pendingAction);
    this.confirmModalOpen.set(false);
    if (this.pendingAction) {
      this.pendingAction().then(() => {
        console.log('pendingAction finished successfully');
      }).catch(err => {
        console.error('pendingAction error', err);
      });
      this.pendingAction = null;
    }
  }

  protected cancelPendingAction(): void {
    this.confirmModalOpen.set(false);
    this.pendingAction = null;
  }

  async approveGuide(guideId: string): Promise<void> {
    this.processingId.set(guideId);
    this.error.set(null);
    try {
      await firstValueFrom(this.adminApi.approveGuide(guideId));
      this.guides.update(list => {
        const index = list.findIndex(g => g.id === guideId);
        if (index !== -1) list[index] = { ...list[index], status: 'APPROVED' };
        return [...list];
      });
      this.successMessage.set('Guia aprovado com sucesso!');
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (e) {
      this.error.set('Erro ao aprovar guia.');
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
      await firstValueFrom(this.adminApi.syncGameCatalog(gameId));
      
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
      
      if (trophies.length > 0) {
        this.successMessage.set('Troféus sincronizados com sucesso!');
        setTimeout(() => this.successMessage.set(null), 3000);
      } else {
        this.error.set('Nenhum troféu encontrado na PSN. Verifique se o ID do jogo está correto (NPWR...).');
        setTimeout(() => this.error.set(null), 5000);
      }
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

  async generateMainGuideAi(guideId: string): Promise<void> {
    this.isGeneratingMainGuide.set(true);
    this.error.set(null);
    try {
      await firstValueFrom(this.adminApi.generateGuideAi(guideId));
      this.successMessage.set('✨ IA iniciou a pesquisa na web... (Isso leva cerca de 10-30 seg)');
      this.pollMainGuideGeneration(guideId);
    } catch (e) {
      this.error.set('Erro ao iniciar a geração com IA.');
      this.isGeneratingMainGuide.set(false);
    }
  }

  private pollMainGuideGeneration(guideId: string, attempts = 0) {
    if (attempts > 30) {
      this.error.set('A geração demorou muito. Verifique novamente mais tarde ou atualize a página.');
      this.isGeneratingMainGuide.set(false);
      return;
    }
    
    setTimeout(async () => {
      try {
        const guide = await firstValueFrom(this.adminApi.getGuideById(guideId));
        if (guide.content && guide.content.trim() !== '' && guide.content !== 'Gerando conteúdo...') {
          if (this.editingGuideId() === guideId) {
            this.editContent = guide.content;
          }
          this.guides.update(list => {
            const index = list.findIndex(g => g.id === guideId);
            if (index !== -1) list[index] = { ...list[index], content: guide.content };
            return [...list];
          });
          
          this.isGeneratingMainGuide.set(false);
          
          if (guide.content.includes('Falha na geração do conteúdo via IA')) {
            this.error.set('A IA falhou em gerar o conteúdo. Tente novamente.');
            setTimeout(() => this.error.set(null), 5000);
          } else {
            this.successMessage.set('✨ Guia gerado com sucesso pela IA!');
            setTimeout(() => this.successMessage.set(null), 3000);
          }
        } else {
          this.pollMainGuideGeneration(guideId, attempts + 1);
        }
      } catch {
        this.pollMainGuideGeneration(guideId, attempts + 1);
      }
    }, 5000);
  }

  async generateTrophyTipAi(trophyId: string, trophyName: string): Promise<void> {
    const tip = this.trophyTips()[trophyId];
    if (!tip) return;

    this.generatingTips.update(map => ({ ...map, [trophyId]: true }));
    this.error.set(null);

    try {
      let currentGuideId = tip.guideId;

      if (!currentGuideId) {
        const payload = {
          title: `Dica: ${trophyName}`,
          description: '',
          content: '',
        };
        await firstValueFrom(this.api.submitTrophyGuide(trophyId, payload));
        const guide = this.guides().find(g => g.id === this.editingGuideId());
        if (guide) {
           const authorGuides = await firstValueFrom(this.api.getAuthorTrophyGuides(guide.gameId, guide.authorId));
           const created = authorGuides.find(g => g.trophyId === trophyId);
           if (created) {
             currentGuideId = created.id;
             this.trophyTips.update(m => { m[trophyId].guideId = currentGuideId; return { ...m }; });
           }
        }
      }

      if (!currentGuideId) throw new Error('Falha ao criar o draft da dica.');

      await firstValueFrom(this.adminApi.generateTrophyGuideAi(currentGuideId, trophyId));
      this.successMessage.set(`✨ IA pesquisando dica para: ${trophyName}...`);
      this.pollTrophyTipGeneration(currentGuideId, trophyId, 0);

    } catch (e) {
      this.error.set('Erro ao gerar dica com IA. Tente novamente.');
      this.generatingTips.update(map => ({ ...map, [trophyId]: false }));
    }
  }

  private pollTrophyTipGeneration(guideId: string, trophyId: string, attempts = 0) {
    if (attempts > 12) {
      this.error.set('A geração da dica demorou muito.');
      this.generatingTips.update(map => ({ ...map, [trophyId]: false }));
      return;
    }
    
    setTimeout(async () => {
      try {
        const guide = await firstValueFrom(this.adminApi.getGuideById(guideId));
        if (guide.content && guide.content.trim() !== '' && guide.content !== 'Gerando conteúdo...') {
          this.trophyTips.update(m => { 
            if (m[trophyId]) m[trophyId].content = guide.content;
            return { ...m }; 
          });
          this.generatingTips.update(map => ({ ...map, [trophyId]: false }));
          
          if (guide.content.includes('Falha na geração do conteúdo via IA')) {
            this.error.set('A IA falhou em gerar a dica. Tente novamente.');
            setTimeout(() => this.error.set(null), 5000);
          } else {
            this.successMessage.set('✨ Dica gerada com sucesso pela IA!');
            setTimeout(() => this.successMessage.set(null), 3000);
          }
        } else {
          this.pollTrophyTipGeneration(guideId, trophyId, attempts + 1);
        }
      } catch {
        this.pollTrophyTipGeneration(guideId, trophyId, attempts + 1);
      }
    }, 5000);
  }
}
