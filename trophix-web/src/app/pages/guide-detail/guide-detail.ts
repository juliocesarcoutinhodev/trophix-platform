import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { DomSanitizer } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { marked } from 'marked';

import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { GuideResponse, TrophyStatus } from '../../core/models/api.models';

@Component({
  selector: 'app-guide-detail',
  standalone: true,
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './guide-detail.html',
})
export class GuideDetailComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly sanitizer = inject(DomSanitizer);
  private readonly auth = inject(AuthService);

  guide = signal<GuideResponse | null>(null);
  trophies = signal<TrophyStatus[]>([]);
  loading = signal(true);
  voting = signal(false);
  activeTab = signal<'roadmap' | 'trophies'>('roadmap');

  isAuthor = computed(() => {
    const user = this.auth.userSignal();
    const g = this.guide();
    return !!(user && g && user.username === g.authorName);
  });

  // Dicas oficiais do autor (PowerPyx style)
  authorTips = signal<Record<string, GuideResponse>>({});
  editingTrophy = signal<Record<string, boolean>>({});
  newTipContent = signal<Record<string, string>>({});
  submittingTip = signal<Record<string, boolean>>({});

  toggleEditTrophy(trophyId: string, state: boolean) {
    this.editingTrophy.update(prev => ({ ...prev, [trophyId]: state }));
  }

  async ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    try {
      this.loading.set(true);
      const data = await firstValueFrom(this.api.getGuideById(id));
      this.guide.set(data);

      if (data.gameId) {
        // Busca os troféus globais do jogo associado ao roadmap
        const t = await firstValueFrom(this.api.getGameTrophies(data.gameId));
        this.trophies.set(t);

        // Busca todas as dicas do autor para os troféus deste jogo
        try {
          const tips = await firstValueFrom(this.api.getAuthorTrophyGuides(data.gameId, data.authorId));
          const tipsMap: Record<string, GuideResponse> = {};
          for (const tip of tips) {
            if (tip.trophyId) {
              tipsMap[tip.trophyId] = tip;
              this.newTipContent.update(prev => ({ ...prev, [tip.trophyId as string]: tip.content }));
            }
          }
          this.authorTips.set(tipsMap);
        } catch(e) {
          console.error('Falha ao carregar as dicas do autor', e);
        }
      }
    } catch (e) {
      console.error('Falha ao carregar o guia', e);
    } finally {
      this.loading.set(false);
    }
  }

  updateNewTip(trophyId: string, content: string) {
    this.newTipContent.update(prev => ({ ...prev, [trophyId]: content }));
  }

  async submitTrophyTip(trophyId: string) {
    const content = this.newTipContent()[trophyId];
    if (!content || content.trim().length === 0) return;

    this.submittingTip.update(prev => ({ ...prev, [trophyId]: true }));
    try {
      // Como o backend salva/atualiza pela dupla (trophyId, authorId), um POST com o mesmo trophyId atualiza a dica
      await firstValueFrom(this.api.submitTrophyGuide(trophyId, {
        title: 'Dica do Autor',
        description: '',
        content: content.trim()
      }));
      
      this.editingTrophy.update(prev => ({ ...prev, [trophyId]: false }));
      
      // Recarrega as dicas do autor para atualizar o cache local
      const g = this.guide();
      if (g) {
        const tips = await firstValueFrom(this.api.getAuthorTrophyGuides(g.gameId, g.authorId));
        const tipsMap: Record<string, GuideResponse> = {};
        for (const tip of tips) {
          if (tip.trophyId) tipsMap[tip.trophyId] = tip;
        }
        this.authorTips.set(tipsMap);
      }
      
    } catch (e) {
      console.error('Erro ao enviar dica', e);
      alert('Falha ao salvar a dica.');
    } finally {
      this.submittingTip.update(prev => ({ ...prev, [trophyId]: false }));
    }
  }

  showLoginModal = signal(false);

  async toggleVote() {
    if (!this.auth.isAuthenticated()) {
      this.showLoginModal.set(true);
      return;
    }

    const g = this.guide();
    if (!g || this.voting()) return;

    this.voting.set(true);
    try {
      const response = await firstValueFrom(this.api.voteGuide(g.id));
      this.guide.update(prev => prev ? { 
        ...prev, 
        upvotesCount: response.upvotesCount,
        currentUserVoted: response.voted 
      } : prev);
    } catch (e) {
      console.error('Falha ao votar no guia', e);
    } finally {
      this.voting.set(false);
    }
  }



  videoEmbedUrl = computed(() => {
    const url = this.guide()?.videoUrl;
    if (!url) return null;
    
    // Converte URL do YouTube (vídeo ou playlist) para iframe embed
    const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
    const match = url.match(regExp);

    if (match && match[2].length === 11) {
      return this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/${match[2]}`);
    }
    
    const listMatch = url.match(/[?&]list=([^#\&\?]+)/);
    if (listMatch) {
       return this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/videoseries?list=${listMatch[1]}`);
    }
    
    return null;
  });

  parsedContent = computed(() => {
    const raw = this.guide()?.content;
    if (!raw) return '';
    return marked.parse(raw) as string;
  });

  getParsedMarkdown(raw?: string): string {
    if (!raw) return '';
    return marked.parse(raw) as string;
  }
}
