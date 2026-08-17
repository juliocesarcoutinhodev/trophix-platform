import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { NewsArticleResponse, Page } from '../../core/models/api.models';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-news',
  standalone: true,
  imports: [RouterLink, DatePipe, PaginationComponent],
  templateUrl: './news.component.html',
})
export class NewsComponent implements OnInit {
  private readonly api = inject(ApiService);

  newsPage = signal<Page<NewsArticleResponse> | null>(null);
  loading = signal(true);
  
  async ngOnInit() {
    await this.loadNews(0);
  }

  async loadNews(page: number) {
    this.loading.set(true);
    try {
      const res = await firstValueFrom(this.api.getLatestNews(page, 13)); // 1 destaque + 12 normais
      this.newsPage.set(res);
    } catch (e) {
      console.error('Erro ao carregar notícias', e);
    } finally {
      this.loading.set(false);
    }
  }

  onPageChange(newPage: number) {
    this.loadNews(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
