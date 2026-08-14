import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { marked } from 'marked';
import { firstValueFrom } from 'rxjs';

import { ApiService } from '../../core/services/api.service';
import { UserGame } from '../../core/models/api.models';

@Component({
  selector: 'app-create-guide',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './create-guide.html',
})
export class CreateGuide implements OnInit {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);

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
      alert('Preencha os campos obrigatórios: Jogo, Título e Conteúdo (Roadmap).');
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
      alert('Guia criado com sucesso! Ele foi enviado para revisão da moderação e logo aparecerá na plataforma.');
      // Volta para a listagem correspondente
      this.goBack();
    } catch (e) {
      console.error(e);
      alert('Falha ao publicar guia. Tente novamente mais tarde.');
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
