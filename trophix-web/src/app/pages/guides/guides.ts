import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { toObservable } from '@angular/core/rxjs-interop';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { firstValueFrom } from 'rxjs';

import { ApiService } from '../../core/services/api.service';
import { GuideResponse } from '../../core/models/api.models';

@Component({
  selector: 'app-guides',
  standalone: true,
  imports: [RouterLink, DatePipe, FormsModule],
  templateUrl: './guides.html',
})
export class Guides implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  
  protected readonly guides = signal<GuideResponse[]>([]);
  protected readonly loading = signal(true);
  protected readonly searchQuery = signal<string | null>(null);
  protected searchInput = signal<string>('');

  constructor() {
    toObservable(this.searchInput).pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(() => {
      this.onSearch();
    });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(async params => {
      const search = params['search'];
      this.searchQuery.set(search || null);
      if (search) {
        this.searchInput.set(search);
      } else {
        this.searchInput.set('');
      }
      
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

  onSearch(): void {
    const term = this.searchInput().trim();
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { search: term ? term : null },
      queryParamsHandling: 'merge',
    });
  }
}
