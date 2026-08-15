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

@Component({
  selector: 'app-create-guide',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ModalComponent],
  templateUrl: './create-guide.html',
})
export class CreateGuide implements OnInit {
  private readonly router = inject(Router);
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);

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

  goBack() {
    if (this.isAdminRoute()) {
      this.router.navigate(['/admin/guides']);
    } else {
      this.router.navigate(['/']);
    }
  }
}
