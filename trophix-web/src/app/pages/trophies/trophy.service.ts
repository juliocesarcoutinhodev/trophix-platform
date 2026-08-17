import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ActivityFeedDTO {
  userId: string;
  username: string;
  avatar: string;
  trophyId: string;
  trophyName: string;
  trophyType: string;
  trophyIconUrl: string;
  gameName: string;
  earnedAt: string;
}

export interface MissingTrophyDTO {
  id: string;
  name: string;
  description: string;
  type: string;
  gameName: string;
  iconUrl: string;
  rarity: number;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class TrophyService {
  private http = inject(HttpClient);
  private apiUrl = '/api';

  getActivityFeed(page: number = 0, size: number = 20): Observable<Page<ActivityFeedDTO>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<ActivityFeedDTO>>(`${this.apiUrl}/public/trophies/feed`, { params });
  }

  // NOTE: This endpoint doesn't exist yet on the backend!
  getMissingTrophies(page: number = 0, size: number = 20): Observable<Page<MissingTrophyDTO>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<MissingTrophyDTO>>(`${this.apiUrl}/users/me/trophies/missing`, { params });
  }
}
