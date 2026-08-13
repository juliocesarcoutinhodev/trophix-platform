import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { ApiService } from '../../core/services/api.service';
import { apiErrorMessage } from '../../core/utils/api-error';
import { UserGame, UserProfile } from '../../core/models/api.models';
import { PlatformFormatPipe } from '../../core/pipes/platform-format.pipe';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [PlatformFormatPipe, RouterLink, DatePipe],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly profile = signal<UserProfile | null>(null);
  protected readonly games = signal<UserGame[]>([]);
  protected readonly loadingGames = signal(true);
  protected readonly syncing = signal(false);
  protected readonly syncError = signal<string | null>(null);
  protected readonly viewMode = signal<'grid' | 'list'>('grid');

  async ngOnInit(): Promise<void> {
    await Promise.all([this.loadProfile(), this.loadGames()]);
  }

  async loadProfile(): Promise<void> {
    try {
      this.profile.set(await firstValueFrom(this.api.getMyProfile()));
    } catch {
      this.profile.set(null);
    }
  }

  async loadGames(): Promise<void> {
    this.loadingGames.set(true);
    try {
      const page = await firstValueFrom(this.api.getMyGames());
      this.games.set(page.content);
    } catch {
      this.games.set([]);
    } finally {
      this.loadingGames.set(false);
    }
  }

  async syncPsn(): Promise<void> {
    if (this.syncing()) return;
    this.syncing.set(true);
    this.syncError.set(null);
    try {
      await firstValueFrom(this.api.syncPsn());
      // Sync runs in background (202); poll after a delay for fresh data.
      await new Promise((resolve) => setTimeout(resolve, 5000));
      await Promise.all([this.loadProfile(), this.loadGames()]);
    } catch (error) {
      this.syncError.set(apiErrorMessage(error, 'Falha ao sincronizar. Tente novamente.'));
    } finally {
      this.syncing.set(false);
    }
  }
}
