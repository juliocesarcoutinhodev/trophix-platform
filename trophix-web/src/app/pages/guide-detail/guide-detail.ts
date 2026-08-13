import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DatePipe } from '@angular/common';
import { DomSanitizer } from '@angular/platform-browser';
import { firstValueFrom } from 'rxjs';
import { marked } from 'marked';

import { ApiService } from '../../core/services/api.service';
import { GuideResponse, TrophyStatus } from '../../core/models/api.models';

@Component({
  selector: 'app-guide-detail',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './guide-detail.html',
})
export class GuideDetailComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly sanitizer = inject(DomSanitizer);

  guide = signal<GuideResponse | null>(null);
  trophies = signal<TrophyStatus[]>([]);
  loading = signal(true);
  voting = signal(false);
  activeTab = signal<'roadmap' | 'trophies'>('roadmap');

  async toggleVote() {
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
      }
    } catch (e) {
      console.error('Falha ao carregar o guia', e);
    } finally {
      this.loading.set(false);
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
}
