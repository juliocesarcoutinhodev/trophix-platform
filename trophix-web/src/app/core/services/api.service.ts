import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { GameDetail, MessageResponse, Page, TrophyStatus, UserGame, UserProfile } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  // ---- Auth ----
  login(email: string, password: string) {
    return this.http.post<void>('/api/auth/login', { email, password });
  }

  logout() {
    return this.http.post<void>('/api/auth/logout', {});
  }

  registerCompletion(psnId: string, email: string, password: string) {
    return this.http.post<{ id: string; psnId: string; email: string; roles: string[] }>(
      '/api/auth/register-completion',
      { psnId, email, password }
    );
  }

  // ---- Current user ("me") ----
  getMyProfile() {
    return this.http.get<UserProfile>('/api/users/me/profile');
  }

  getMyGames(page = 0, size = 12) {
    return this.http.get<Page<UserGame>>('/api/users/me/games', {
      params: { page: String(page), size: String(size) },
    });
  }

  syncPsn() {
    return this.http.post<MessageResponse>('/api/users/me/sync', {});
  }

  // ---- Public profile ----
  getProfile(username: string) {
    return this.http.get<UserProfile>(`/api/users/${username}/profile`);
  }

  getUserGames(username: string, page = 0, size = 12) {
    return this.http.get<Page<UserGame>>(`/api/users/${username}/games`, {
      params: { page: String(page), size: String(size) },
    });
  }

  // ---- Games ----
  getGameDetail(gameId: string) {
    return this.http.get<GameDetail>(`/api/games/${gameId}/detail`);
  }

  getMyTrophies(gameId: string) {
    return this.http.get<TrophyStatus[]>(`/api/games/${gameId}/my-trophies`);
  }

  syncGameTrophies(gameId: string) {
    return this.http.post<MessageResponse>(`/api/games/${gameId}/sync-trophies`, {});
  }
}
