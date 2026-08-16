import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ForumsService } from '../../../../core/services/forums.service';
import { Page, TopicListItem } from '../../../../core/models/api.models';
import { AuthService } from '../../../../core/services/auth.service';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-forum-category',
  standalone: true,
  imports: [RouterLink, DatePipe, FormsModule, PaginationComponent],
  templateUrl: './forum-category.component.html',
  styleUrl: './forum-category.css'
})
export class ForumCategoryComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly forumsApi = inject(ForumsService);
  readonly auth = inject(AuthService);

  categoryId = signal<string>('');
  topicsPage = signal<Page<TopicListItem> | null>(null);
  currentPage = signal<number>(0);

  // New Topic Form state
  showCreateForm = signal(false);
  newTopicTitle = signal('');
  newTopicContent = signal('');
  submitting = signal(false);

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.categoryId.set(id);
        this.loadTopics(0);
      }
    });
  }

  loadTopics(page: number) {
    this.forumsApi.getCategoryTopics(this.categoryId(), page, 20).subscribe({
      next: (data) => {
        this.topicsPage.set(data);
        this.currentPage.set(data.number);
      },
      error: (e) => console.error('Erro ao carregar tópicos:', e)
    });
  }

  onPageChange(newPage: number) {
    this.loadTopics(newPage);
  }

  toggleCreateForm() {
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }
    this.showCreateForm.set(!this.showCreateForm());
    if (this.showCreateForm()) {
      this.newTopicTitle.set('');
      this.newTopicContent.set('');
    }
  }

  submitTopic() {
    if (!this.newTopicTitle().trim() || !this.newTopicContent().trim() || this.submitting()) {
      return;
    }
    this.submitting.set(true);
    this.forumsApi.createTopic({
      categoryId: this.categoryId(),
      title: this.newTopicTitle(),
      content: this.newTopicContent()
    }).subscribe({
      next: (res) => {
        this.submitting.set(false);
        this.showCreateForm.set(false);
        this.router.navigate(['/forums/topic', res.id]);
      },
      error: (err) => {
        console.error('Erro ao criar tópico', err);
        this.submitting.set(false);
      }
    });
  }
}
