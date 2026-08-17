import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Offer {
  id: string;
  title: string;
  imageUrl: string;
  originalPrice: number;
  discountPrice: number;
  discountPercentage: number;
  storeName: string;
  affiliateLink: string;
  category: string;
  isFlashDeal: boolean;
  isActive: boolean;
}

export interface OfferPage {
  content: Offer[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class OfferService {
  private readonly http = inject(HttpClient);
  private readonly publicUrl = '/api/public/offers';
  private readonly adminUrl = '/api/admin/offers';

  // --- Public Methods ---

  getPublicOffers(page: number = 0, size: number = 10, category?: string): Observable<OfferPage> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (category && category !== 'Todos') {
      params = params.set('category', category);
    }

    return this.http.get<OfferPage>(this.publicUrl, { params });
  }

  // --- Admin Methods ---

  getAdminOffers(page: number = 0, size: number = 100): Observable<OfferPage> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<OfferPage>(this.adminUrl, { params });
  }

  createOffer(offer: Partial<Offer>): Observable<Offer> {
    return this.http.post<Offer>(this.adminUrl, offer);
  }

  updateOffer(id: string, offer: Partial<Offer>): Observable<Offer> {
    return this.http.put<Offer>(`${this.adminUrl}/${id}`, offer);
  }

  deleteOffer(id: string): Observable<void> {
    return this.http.delete<void>(`${this.adminUrl}/${id}`);
  }
}
