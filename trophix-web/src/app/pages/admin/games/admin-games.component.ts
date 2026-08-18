import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe, NgClass } from '@angular/common';
import { firstValueFrom, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { toObservable } from '@angular/core/rxjs-interop';

import { GameCatalogDTO, GameService, Page } from '../../games/game.service';
import { ApiService } from '../../../core/services/api.service';
import { apiErrorMessage } from '../../../core/utils/api-error';

@Component({
  selector: 'app-admin-games',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './admin-games.component.html'
})
export class AdminGamesComponent implements OnInit {
  private readonly gameService = inject(GameService);
  private readonly apiService = inject(ApiService);

  protected readonly games = signal<GameCatalogDTO[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  
  protected readonly searchQuery = signal('');
  protected readonly page = signal(0);
  protected readonly totalPages = signal(1);

  constructor() {
    toObservable(this.searchQuery)
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe(() => {
        this.page.set(0);
        this.loadGames();
      });
  }

  protected readonly importModalOpen = signal(false);
  protected readonly importSearchQuery = signal('');
  protected readonly importLoading = signal(false);

  ngOnInit(): void {
    // initial load is triggered by the subscribe in constructor due to signal initial value
  }

  protected async loadGames(): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    try {
      const p = await firstValueFrom(this.gameService.getPublicGames(this.page(), 20, this.searchQuery()));
      this.games.set(p.content);
      this.totalPages.set(p.totalPages);
    } catch (e) {
      this.error.set(apiErrorMessage(e, 'Erro ao carregar jogos.'));
    } finally {
      this.loading.set(false);
    }
  }

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
      await firstValueFrom(this.apiService.importGameFromPsn(this.importSearchQuery().trim()));
      this.successMessage.set('Jogo importado com sucesso!');
      this.closeImportModal();
      this.loadGames(); // Refresh list
    } catch (e) {
      this.error.set(apiErrorMessage(e, 'Erro ao importar jogo. Verifique se o ID está correto ou se o backend já tem essa rota implementada.'));
    } finally {
      this.importLoading.set(false);
    }
  }

  protected async toggleFeature(game: GameCatalogDTO): Promise<void> {
    try {
      const newStatus = !game.isFeatured;
      await firstValueFrom(this.apiService.toggleGameFeature(game.id, newStatus));
      
      // Update local state
      this.games.update(list => list.map(g => g.id === game.id ? { ...g, isFeatured: newStatus } : g));
      
      this.successMessage.set(`Jogo '${game.name}' ${newStatus ? 'destacado' : 'removido dos destaques'} com sucesso.`);
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (e) {
      this.error.set(apiErrorMessage(e, 'Erro ao destacar jogo.'));
      setTimeout(() => this.error.set(null), 3000);
    }
  }

  protected nextPage(): void {
    if (this.page() < this.totalPages() - 1) {
      this.page.update(p => p + 1);
      this.loadGames();
    }
  }

  protected prevPage(): void {
    if (this.page() > 0) {
      this.page.update(p => p - 1);
      this.loadGames();
    }
  }
}
