import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { GameDetail, TrophyStatus } from '../../core/models/api.models';
import { PlatformFormatPipe } from '../../core/pipes/platform-format.pipe';
import { ApiService } from '../../core/services/api.service';
import { apiErrorMessage } from '../../core/utils/api-error';

@Component({
  selector: 'app-game-detail',
  standalone: true,
  imports: [DatePipe, PlatformFormatPipe, RouterLink],
  templateUrl: './game-detail.component.html',
})
export class GameDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly api = inject(ApiService);

  protected readonly game = signal<GameDetail | null>(null);
  protected readonly trophies = signal<TrophyStatus[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  async ngOnInit(): Promise<void> {
    const gameId = this.route.snapshot.paramMap.get('id');
    if (!gameId) {
      this.error.set('Jogo não encontrado.');
      this.loading.set(false);
      return;
    }
    await this.loadGame(gameId);
    void this.syncTrophiesSilently(gameId);
  }

  async loadGame(gameId: string): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    try {
      await this.fetchGame(gameId);
    } catch (error) {
      this.error.set(apiErrorMessage(error, 'Falha ao carregar o jogo. Tente novamente.'));
    } finally {
      this.loading.set(false);
    }
  }

  private async fetchGame(gameId: string): Promise<void> {
    const [game, trophies] = await Promise.all([
      firstValueFrom(this.api.getGameDetail(gameId)),
      firstValueFrom(this.api.getMyTrophies(gameId)),
    ]);
    this.game.set(game);
    this.trophies.set(trophies);
  }

  private async syncTrophiesSilently(gameId: string): Promise<void> {
    try {
      await firstValueFrom(this.api.syncGameTrophies(gameId));
      // O sync agora é assíncrono (fila): aguarda o worker processar antes de recarregar.
      await new Promise((resolve) => setTimeout(resolve, 2000));
      await this.fetchGame(gameId);
    } catch {
      // Mantém os dados já exibidos; a sincronização será tentada novamente na próxima visita.
    }
  }

  protected trophyTypeLabel(type: string): string {
    const labels: Record<string, string> = {
      Platinum: 'Platina',
      Gold: 'Ouro',
      Silver: 'Prata',
      Bronze: 'Bronze',
    };
    return labels[type] ?? type;
  }

  protected trophyTypeBadge(type: string): string {
    const badges: Record<string, string> = {
      Platinum: 'border-slate-500/40 bg-slate-200/15 text-slate-300',
      Gold: 'border-yellow-500/40 bg-yellow-500/15 text-yellow-300',
      Silver: 'border-slate-400/40 bg-slate-400/15 text-slate-300',
      Bronze: 'border-amber-700/40 bg-amber-700/20 text-amber-500',
    };
    return badges[type] ?? 'border-slate-500/40 bg-slate-500/15 text-slate-300';
  }

}
