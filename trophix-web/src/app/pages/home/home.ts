import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { GuideResponse } from '../../core/models/api.models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './home.html',
})
export class HomeComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly api = inject(ApiService);

  async ngOnInit() {
    // 1. Carrega Ranking Global
    try {
      const hunters = await firstValueFrom(this.api.getTopHunters(5));
      const mapped = hunters.map((h, i) => ({
        rank: i + 1,
        username: h.username,
        platinums: h.totalPlatinum || 0,
        level: h.psnLevel || 0,
        avatarUrl: h.avatarUrl
      }));
      this.topHunters.set(mapped);
    } catch (e) {
      console.error('Erro ao carregar ranking:', e);
      // Fallback visual caso o backend ainda não tenha o endpoint implementado
      this.topHunters.set([
        { rank: 1, username: 'Cout1nh030', platinums: 152, level: 320, avatarUrl: null },
        { rank: 2, username: 'Hakoom', platinums: 145, level: 310, avatarUrl: null },
        { rank: 3, username: 'PlatinumKing', platinums: 128, level: 295, avatarUrl: null },
        { rank: 4, username: 'TrophyLover', platinums: 110, level: 250, avatarUrl: null },
        { rank: 5, username: 'GamerGirl99', platinums: 98, level: 215, avatarUrl: null },
      ]);
    }

    // 2. Carrega Últimos Guias
    try {
      const guides = await firstValueFrom(this.api.getLatestGuides());
      this.recentGuides.set(guides.slice(0, 4));
    } catch (e) {
      console.error('Erro ao carregar guias recentes:', e);
      // Mantém os guias mocados como fallback em caso de erro
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
    const query = this.searchQuery().trim();
    if (!query) {
      this.searchResults.set([]);
      this.showDropdown.set(false);
      return;
    }

    clearTimeout(this.searchTimeout);
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

  recentGuides = signal<GuideResponse[]>([
    {
      id: '1',
      gameId: '1',
      authorId: '1',
      status: 'APPROVED',
      createdAt: new Date().toISOString(),
      trophyId: null,
      videoUrl: null,
      content: '',
      description: '',
      gameName: 'Elden Ring',
      title: 'Elden Ring - Guia de Platina (100%)',
      authorName: 'Cout1nh030',
      upvotesCount: 342,
      imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co4jni.jpg'
    },
    {
      id: '2',
      gameId: '2',
      authorId: '2',
      status: 'APPROVED',
      createdAt: new Date().toISOString(),
      trophyId: null,
      videoUrl: null,
      content: '',
      description: '',
      gameName: 'God of War Ragnarök',
      title: 'Guia de Colecionáveis (Corvos, Artefatos, Baús Nornir)',
      authorName: 'KratosHunter',
      upvotesCount: 215,
      imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co5s5v.jpg'
    },
    {
      id: '3',
      gameId: '3',
      authorId: '3',
      status: 'APPROVED',
      createdAt: new Date().toISOString(),
      trophyId: null,
      videoUrl: null,
      content: '',
      description: '',
      gameName: 'Marvel\'s Spider-Man 2',
      title: 'Platina em 25 Horas - Passo a Passo Eficiente',
      authorName: 'WebSlinger',
      upvotesCount: 189,
      imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co69f1.jpg'
    },
    {
      id: '4',
      gameId: '4',
      authorId: '4',
      status: 'APPROVED',
      createdAt: new Date().toISOString(),
      trophyId: null,
      videoUrl: null,
      content: '',
      description: '',
      gameName: 'The Last of Us Part I',
      title: 'Dificuldade Sobrevivente e Todos os Pingentes',
      authorName: 'EllieFan',
      upvotesCount: 156,
      imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co5x99.jpg'
    }
  ]);

  trendingGames = signal([
    { name: 'Elden Ring', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co4jni.jpg', guidesCount: 12 },
    { name: 'Final Fantasy VII Rebirth', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co7i0e.jpg', guidesCount: 8 },
    { name: 'Helldivers 2', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co79i1.jpg', guidesCount: 24 },
    { name: 'Baldur\'s Gate 3', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co670h.jpg', guidesCount: 15 },
    { name: 'Cyberpunk 2077', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co2mvt.jpg', guidesCount: 30 }
  ]);

  topHunters = signal<Array<{ rank: number; username: string; platinums: number; level: number; avatarUrl?: string | null }>>([]);
}
