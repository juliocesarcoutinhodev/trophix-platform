import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ForumsService } from '../../../../core/services/forums.service';
import { CategoryListItem } from '../../../../core/models/api.models';

@Component({
  selector: 'app-forum-home',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './forum-home.component.html',
  styleUrl: './forum-home.css'
})
export class ForumHomeComponent implements OnInit {
  private readonly forumsApi = inject(ForumsService);
  
  categories = signal<CategoryListItem[] | null>(null);

  ngOnInit() {
    this.forumsApi.getCategories().subscribe({
      next: (data) => this.categories.set(data),
      error: (e) => console.error('Erro ao carregar categorias do fórum:', e)
    });
  }
}
