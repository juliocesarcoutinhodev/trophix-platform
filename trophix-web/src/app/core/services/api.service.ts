import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { GameDetail, MessageResponse, Page, TrophyStatus, UserGame, UserProfile, GuideResponse, SubmitGuideRequest, VoteResponse } from '../models/api.models';

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

  refreshToken() {
    return this.http.post<void>('/api/auth/refresh', {});
  }

  forgotPassword(email: string) {
    return this.http.post<MessageResponse>('/api/auth/forgot-password', { email });
  }

  resetPassword(token: string, newPassword: string) {
    return this.http.post<MessageResponse>('/api/auth/reset-password', { token, newPassword });
  }

  registerCompletion(psnId: string, email: string, password: string) {
    return this.http.post<MessageResponse>(
      '/api/auth/register-completion',
      { psnId, email, password }
    );
  }

  requestAccountLink(psnId: string) {
    return this.http.post<{ psnId: string; token: string; expiresAt: string }>(
      '/api/users/link-request',
      { psnId }
    );
  }

  validateAccountLink(psnId: string) {
    return this.http.post<{ psnId: string; userId: string; message: string }>(
      '/api/users/link-validate',
      { psnId }
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

  getTopHunters(limit = 5) {
    return this.http.get<UserProfile[]>('/api/users/ranking', {
      params: { limit: String(limit) }
    });
  }

  // ---- Games ----
  getGameDetail(gameId: string) {
    return this.http.get<GameDetail>(`/api/games/${gameId}/detail`);
  }

  getGameTrophies(gameId: string) {
    return this.http.get<TrophyStatus[]>(`/api/games/${gameId}/trophies`);
  }

  getMyTrophies(gameId: string) {
    return this.http.get<TrophyStatus[]>(`/api/games/${gameId}/my-trophies`);
  }

  // ---- Trophies (Tips/Guides) ----
  getTrophyGuides(trophyId: string) {
    return this.http.get<GuideResponse[]>(`/api/trophies/${trophyId}/guides`);
  }

  getAuthorTrophyGuides(gameId: string, authorId: string) {
    return this.http.get<GuideResponse[]>(`/api/games/${gameId}/authors/${authorId}/trophy-guides`);
  }

  submitTrophyGuide(trophyId: string, request: SubmitGuideRequest) {
    return this.http.post<MessageResponse>(`/api/trophies/${trophyId}/guides`, request);
  }

  syncGameTrophies(gameId: string) {
    return this.http.post<MessageResponse>(`/api/games/${gameId}/sync-trophies`, {});
  }

  // ---- Guides ----
  getLatestGuides(search?: string) {
    let params: any = {};
    if (search) params.search = search;
    return this.http.get<GuideResponse[]>('/api/guides', { params });
  }

  getGuideById(guideId: string) {
    return this.http.get<GuideResponse>(`/api/guides/${guideId}`);
  }

  voteGuide(guideId: string) {
    return this.http.post<VoteResponse>(`/api/guides/${guideId}/vote`, {});
  }

  submitGameGuide(gameId: string, request: SubmitGuideRequest) {
    return this.http.post<MessageResponse>(`/api/games/${gameId}/guides`, request);
  }
}
