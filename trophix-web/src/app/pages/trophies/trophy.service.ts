import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ActivityFeedDTO {
  id: string;
  userId: string;
  user: {
    username: string;
    avatar: string;
  };
  trophy: {
    name: string;
    type: string;
    iconUrl: string;
  };
  game: {
    name: string;
  };
  date: Date;
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

@Injectable({
  providedIn: 'root'
})
export class TrophyService {
  private http = inject(HttpClient);
  private apiUrl = '/api';

  getActivityFeed(limit: number = 20): Observable<ActivityFeedDTO[]> {
    return this.http.get<ActivityFeedDTO[]>(`${this.apiUrl}/public/trophies/feed`, {
      params: { limit: limit.toString() }
    });
  }

  getMissingTrophies(limit: number = 20): Observable<MissingTrophyDTO[]> {
    return this.http.get<MissingTrophyDTO[]>(`${this.apiUrl}/users/me/trophies/missing`, {
      params: { limit: limit.toString() }
    });
  }
}
