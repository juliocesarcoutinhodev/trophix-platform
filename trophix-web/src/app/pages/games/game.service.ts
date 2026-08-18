import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserGame } from '../../core/models/api.models';

export interface GameCatalogDTO {
  id: string;
  name: string;
  coverUrl: string;
  players?: number; // Depending on backend
  progress?: number; // Only for logged in user
  isFeatured?: boolean;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  last?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private http = inject(HttpClient);
  private apiUrl = '/api';

  getPublicGames(page: number, size: number, search?: string): Observable<Page<GameCatalogDTO>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<Page<GameCatalogDTO>>(`${this.apiUrl}/public/games`, { params });
  }

  getMyGames(page: number = 0, size: number = 20): Observable<Page<UserGame>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<UserGame>>(`${this.apiUrl}/users/me/games`, { params });
  }
}
