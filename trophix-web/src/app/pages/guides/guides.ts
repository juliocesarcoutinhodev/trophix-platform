import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { DatePipe } from '@angular/common';
import { firstValueFrom } from 'rxjs';

import { ApiService } from '../../core/services/api.service';
import { GuideResponse } from '../../core/models/api.models';

@Component({
  selector: 'app-guides',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './guides.html',
})
export class Guides implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  
  protected readonly guides = signal<GuideResponse[]>([]);
  protected readonly loading = signal(true);
  protected readonly searchQuery = signal<string | null>(null);

  ngOnInit(): void {
    this.route.queryParams.subscribe(async params => {
      const search = params['search'];
      this.searchQuery.set(search || null);
      
      try {
        this.loading.set(true);
        const data = await firstValueFrom(this.api.getLatestGuides(search));
        this.guides.set(data);
      } catch (error) {
        console.error('Falha ao carregar guias:', error);
      } finally {
        this.loading.set(false);
      }
    });
  }
}
