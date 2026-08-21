import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { marked } from 'marked';
import { firstValueFrom } from 'rxjs';

import { ApiService } from '../../core/services/api.service';
import { UserGame } from '../../core/models/api.models';
import { AuthService } from '../../core/services/auth.service';

import { ModalComponent } from '../../shared/components/modal/modal.component';

import { AdminService } from '../../core/services/admin.service';

import { AiSpinnerComponent } from '../../shared/components/ai-spinner/ai-spinner.component';

@Component({
  selector: 'app-create-guide',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalComponent, AiSpinnerComponent],
  templateUrl: './create-guide.html',
})
export class CreateGuide implements OnInit {
  private readonly router = inject(Router);
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);
  private readonly adminApi = inject(AdminService);

  modalOpen = false;
  modalTitle = '';
  modalMessage = '';
  modalType: 'success' | 'error' | 'warning' | 'info' = 'info';

  showModal(title: string, message: string, type: 'success' | 'error' | 'warning' | 'info') {
    this.modalTitle = title;
    this.modalMessage = message;
    this.modalType = type;
    this.modalOpen = true;
  }

  onModalClose() {
    if (this.modalType === 'success') {
      this.goBack();
    }
  }

  isAdminRoute = computed(() => this.router.url.includes('/admin'));

  selectedGameId = signal('');
  title = signal('');
  description = signal('');
  videoUrl = signal('');
  content = signal('');
  
  games = signal<UserGame[]>([]);
  activeTab = signal<'write' | 'preview'>('write');
  isSubmitting = signal(false);
  dropdownOpen = signal(false);

  selectedGame = computed(() => {
    return this.games().find(g => g.gameId === this.selectedGameId());
  });

  selectGame(gameId: string) {
    this.selectedGameId.set(gameId);
    this.dropdownOpen.set(false);
  }

  async ngOnInit(): Promise<void> {
    try {
      // Carrega os jogos do perfil para o usuário selecionar a qual jogo este guia pertence
      const page = await firstValueFrom(this.api.getMyGames(0, 100));
      this.games.set(page.content);
    } catch (e) {
      console.error('Falha ao carregar jogos', e);
    }
  }

  // Converte o Markdown em HTML puro toda vez que o content mudar
  parsedContent = computed(() => {
    let raw = this.content();
    if (!raw.trim()) return '';
    
    // Remove blocos de crase se o usuário copiar acidentalmente de uma IA
    if (raw.startsWith('```markdown')) {
      raw = raw.replace(/^```markdown\n?/, '').replace(/\n?```$/, '');
    }
    
    return marked.parse(raw) as string;
  });

  async saveGuide() {
    if (!this.selectedGameId() || !this.title() || !this.content()) {
      this.showModal('Atenção', 'Preencha os campos obrigatórios: Jogo, Título e Conteúdo (Roadmap).', 'warning');
      return;
    }
    if (this.isSubmitting()) {
      return;
    }
    this.isSubmitting.set(true);
    try {
      await firstValueFrom(this.api.submitGameGuide(this.selectedGameId(), {
        title: this.title(),
        description: this.description(),
        content: this.content(),
        videoUrl: this.videoUrl()
      }));
      // Exibe mensagem informando que o guia está em revisão
      this.showModal('Sucesso!', 'Guia criado com sucesso! Ele foi enviado para revisão da moderação e logo aparecerá na plataforma.', 'success');
      // Navigation moved to onModalClose()
    } catch (error) {
      console.error('Erro ao salvar guia:', error);
      this.isSubmitting.set(false);
      this.showModal('Erro', 'Falha ao publicar guia. Tente novamente mais tarde.', 'error');
    } finally {
      this.isSubmitting.set(false);
    }
  }

  isGeneratingWithAi = signal(false);

  async createAndGenerateWithAi() {
    if (!this.selectedGameId()) {
      this.showModal('Atenção', 'Selecione um jogo primeiro para usar a IA.', 'warning');
      return;
    }
    if (!this.title()) {
      const game = this.selectedGame();
      const gameName = game?.name || '';
      const platformStr = game?.platform ? ` (${game.platform})` : '';
      this.title.set(`Guia de Troféus e Platina: ${gameName}${platformStr}`.replace(' ()', '').trim());
    }
    if (this.isGeneratingWithAi()) {
      return;
    }
    this.isGeneratingWithAi.set(true);
    try {
      // 1. Criar o guia em branco
      await firstValueFrom(this.api.submitGameGuide(this.selectedGameId(), {
        title: this.title(),
        description: this.description(),
        content: this.content() || 'Gerando conteúdo...',
        videoUrl: this.videoUrl()
      }));

      // 2. Buscar o ID do guia recém criado
      if (this.isAdminRoute()) {
        const page = await firstValueFrom(this.adminApi.getAllGuides(0, 10, '', this.title()));
        const guide = page.content.find(g => g.gameId === this.selectedGameId() && g.title === this.title());
        
        if (guide) {
          // 3. Chamar a IA
          await firstValueFrom(this.adminApi.generateGuideAi(guide.id));
          
          // 4. Polling até a IA terminar
          const finalContent = await this.pollUntilGenerated(guide.id);
          
          this.isGeneratingWithAi.set(false);
          
          if (finalContent.includes('Falha na geração do conteúdo via IA')) {
            this.showModal('Erro na IA', 'A IA falhou em gerar o conteúdo. O guia foi salvo e você pode tentar gerar novamente na edição.', 'error');
          } else {
            this.showModal('Sucesso!', 'Guia gerado com sucesso pela IA! Redirecionando para a moderação...', 'success');
          }
          
          setTimeout(() => {
            this.router.navigate(['/admin/all-guides']);
          }, 2000);
          return;
        }
      }
      
      this.isGeneratingWithAi.set(false);
      this.showModal('Sucesso!', 'Guia criado. Não foi possível engatilhar a IA automaticamente, você pode fazer isso pela edição.', 'success');
    } catch (error) {
      console.error('Erro ao gerar guia com IA:', error);
      this.isGeneratingWithAi.set(false);
      this.showModal('Erro', 'Ocorreu um erro ou a geração demorou muito.', 'error');
    }
  }

  private pollUntilGenerated(guideId: string, attempts = 0): Promise<string> {
    return new Promise((resolve, reject) => {
      if (attempts > 38) {
        reject(new Error('Timeout esperando a IA.'));
        return;
      }
      
      setTimeout(async () => {
        try {
          const guide = await firstValueFrom(this.adminApi.getGuideById(guideId));
          if (guide.content && guide.content.trim() !== '' && guide.content !== 'Gerando conteúdo...') {
            resolve(guide.content);
          } else {
            this.pollUntilGenerated(guideId, attempts + 1).then(resolve).catch(reject);
          }
        } catch {
          this.pollUntilGenerated(guideId, attempts + 1).then(resolve).catch(reject);
        }
      }, 4000);
    });
  }

  goBack() {
    if (this.isAdminRoute()) {
      this.router.navigate(['/admin/all-guides']);
    } else {
      this.router.navigate(['/']);
    }
  }
}
