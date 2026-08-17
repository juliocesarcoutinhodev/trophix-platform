import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { GameService, GameCatalogDTO } from './game.service';
import { UserGame } from '../../core/models/api.models';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { PlatformFormatPipe } from '../../core/pipes/platform-format.pipe';

@Component({
  selector: 'app-games',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, PaginationComponent, PlatformFormatPipe],
  templateUrl: './games.component.html'
})
export class GamesComponent implements OnInit {
  auth = inject(AuthService);
  private gameService = inject(GameService);
  
  // Tabs: 'catalog' | 'my-games'
  activeTab = signal<'catalog' | 'my-games'>('catalog');

  catalogGames = signal<GameCatalogDTO[]>([]);
  catalogPage = signal(0);
  catalogTotalPages = signal(0);
  catalogTotalElements = signal(0);

  myGames = signal<UserGame[]>([]);
  myGamesPage = signal(0);
  myGamesTotalPages = signal(0);
  myGamesTotalElements = signal(0);

  ngOnInit() {
    this.loadCatalogGames(0);
    if (this.auth.userSignal()) {
      this.activeTab.set('my-games');
      this.loadMyGames(0);
    }
  }

  loadCatalogGames(page: number) {
    this.gameService.getPublicGames(page, 20).subscribe({
      next: (pageData) => {
        this.catalogGames.set(pageData.content);
        this.catalogPage.set(pageData.number);
        this.catalogTotalPages.set(pageData.totalPages);
        this.catalogTotalElements.set(pageData.totalElements);
      },
      error: (err) => console.error('Failed to load catalog games', err)
    });
  }

  onCatalogPageChange(newPage: number) {
    this.loadCatalogGames(newPage);
  }

  loadMyGames(page: number) {
    this.gameService.getMyGames(page, 20).subscribe({
      next: (pageData) => {
        this.myGames.set(pageData.content);
        this.myGamesPage.set(pageData.number);
        this.myGamesTotalPages.set(pageData.totalPages);
        this.myGamesTotalElements.set(pageData.totalElements);
      },
      error: (err) => console.error('Failed to load my games', err)
    });
  }

  onMyGamesPageChange(newPage: number) {
    this.loadMyGames(newPage);
  }

  setTab(tab: 'catalog' | 'my-games') {
    this.activeTab.set(tab);
  }

  getRankLetter(progress: number): string {
    const p = progress || 0;
    if (p === 100) return 'S';
    if (p >= 80) return 'A';
    if (p >= 50) return 'B';
    if (p >= 20) return 'C';
    if (p >= 1) return 'D';
    return 'E';
  }

  getRankColor(progress: number): string {
    const p = progress || 0;
    if (p === 100) return 'text-yellow-400 drop-shadow-[0_0_8px_rgba(250,204,21,0.5)]';
    if (p >= 80) return 'text-emerald-400';
    if (p >= 50) return 'text-blue-400';
    if (p >= 20) return 'text-orange-400';
    if (p >= 1) return 'text-slate-300';
    return 'text-red-500';
  }
}
