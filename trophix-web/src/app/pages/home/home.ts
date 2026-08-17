import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { GuideResponse, NewsArticleResponse } from '../../core/models/api.models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, FormsModule, DatePipe],
  templateUrl: './home.html',
})
export class HomeComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly api = inject(ApiService);

  async ngOnInit() {
    try {
      // 1. Carrega Ranking Global
      const hunters = await firstValueFrom(this.api.getTopHunters(5));
      const mapped = hunters.map((h, i) => ({
        rank: i + 1,
        username: h.username,
        platinums: h.totalPlatinum || 0,
        level: h.psnLevel || 0,
        avatarUrl: h.avatarUrl
      }));
      this.topHunters.set(mapped);

      // 2. Carrega Últimos Guias
      const guides = await firstValueFrom(this.api.getLatestGuides());
      this.recentGuides.set(guides.slice(0, 4));

      // 3. Carrega Notícias
      const newsPage = await firstValueFrom(this.api.getLatestNews(0, 4));
      this.latestNews.set(newsPage.content);
    } catch (e) {
      console.error('Erro de API, redirecionando para manutenção:', e);
      this.router.navigate(['/maintenance']);
    }
  }

  // Mock Data (Esses dados virão do backend futuramente)
  settings = signal({
    heroTitle: 'Qual jogo você quer platinar hoje?',
    heroSubtitle: 'A maior comunidade de caçadores de platinas. Explore milhares de guias detalhados criados por jogadores.',
    heroImageUrl: 'https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=2070&auto=format&fit=crop',
    globalAlertEnabled: true,
    globalAlertText: '🔥 Participe do novo Desafio Semanal no nosso Discord e ganhe badges exclusivas!'
  });

  searchQuery = signal('');
  searchResults = signal<GuideResponse[]>([]);
  showDropdown = signal(false);
  isSearching = signal(false);
  toastMessage = signal<string | null>(null);
  private searchTimeout: any;

  onSearchInput() {
    clearTimeout(this.searchTimeout);
    const query = this.searchQuery().trim();
    if (!query) {
      this.searchResults.set([]);
      this.showDropdown.set(false);
      return;
    }
    this.searchTimeout = setTimeout(async () => {
      this.isSearching.set(true);
      try {
        const results = await firstValueFrom(this.api.getLatestGuides(query));
        this.searchResults.set(results);
        this.showDropdown.set(true);
      } catch (e) {
        console.error('Erro na busca live:', e);
      } finally {
        this.isSearching.set(false);
      }
    }, 400);
  }

  hideDropdown() {
    setTimeout(() => this.showDropdown.set(false), 200);
  }

  showToast(msg: string) {
    this.toastMessage.set(msg);
    setTimeout(() => this.toastMessage.set(null), 3000);
  }

  async onSearch() {
    const query = this.searchQuery().trim();
    if (!query) return;

    this.isSearching.set(true);
    try {
      const results = await firstValueFrom(this.api.getLatestGuides(query));
      if (results.length === 0) {
        this.showToast('Guia não localizada para este jogo.');
      } else {
        this.router.navigate(['/guides'], { queryParams: { search: query } });
      }
    } catch (e) {
      this.showToast('Erro ao realizar a busca.');
    } finally {
      this.isSearching.set(false);
      this.showDropdown.set(false);
    }
  }

  recentGuides = signal<GuideResponse[]>([]);

  trendingGames = signal([
    { name: 'Elden Ring', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co4jni.jpg', guidesCount: 12 },
    { name: 'Final Fantasy VII Rebirth', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co7i0e.jpg', guidesCount: 8 },
    { name: 'Helldivers 2', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co79i1.jpg', guidesCount: 24 },
    { name: 'Baldur\'s Gate 3', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co670h.jpg', guidesCount: 15 },
    { name: 'Cyberpunk 2077', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co2mvt.jpg', guidesCount: 30 }
  ]);

  latestNews = signal<NewsArticleResponse[]>([]);

  topHunters = signal<Array<{ rank: number; username: string; platinums: number; level: number; avatarUrl?: string | null }>>([]);
}
