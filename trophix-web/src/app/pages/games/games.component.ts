import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { GameService, GameCatalogDTO } from './game.service';

@Component({
  selector: 'app-games',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './games.component.html'
})
export class GamesComponent implements OnInit {
  auth = inject(AuthService);
  private gameService = inject(GameService);
  
  // Tabs: 'catalog' | 'my-games'
  activeTab = signal<'catalog' | 'my-games'>('catalog');

  trendingGames = signal<GameCatalogDTO[]>([]);
  myGames = signal<GameCatalogDTO[]>([]);

  ngOnInit() {
    this.loadTrendingGames();
    if (this.auth.userSignal()) {
      this.loadMyGames();
    }
  }

  loadTrendingGames() {
    this.gameService.getTrendingGames().subscribe({
      next: (games) => this.trendingGames.set(games),
      error: (err) => console.error('Failed to load trending games', err)
    });
  }

  loadMyGames() {
    this.gameService.getMyGames(0, 20).subscribe({
      next: (page) => this.myGames.set(page.content),
      error: (err) => console.error('Failed to load my games', err)
    });
  }

  setTab(tab: 'catalog' | 'my-games') {
    this.activeTab.set(tab);
  }
}
