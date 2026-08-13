import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
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
  
  protected readonly guides = signal<GuideResponse[]>([]);
  protected readonly loading = signal(true);

  async ngOnInit(): Promise<void> {
    try {
      this.loading.set(true);
      const data = await firstValueFrom(this.api.getLatestGuides());
      this.guides.set(data);
    } catch (error) {
      console.error('Falha ao carregar guias:', error);
    } finally {
      this.loading.set(false);
    }
  }
}
