import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MarkdownComponent } from 'ngx-markdown';
import { ForumsService } from '../../../../core/services/forums.service';
import { Page, ReplyListItem, TopicDetails } from '../../../../core/models/api.models';
import { AuthService } from '../../../../core/services/auth.service';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-forum-topic',
  standalone: true,
  imports: [RouterLink, DatePipe, FormsModule, MarkdownComponent, PaginationComponent],
  templateUrl: './forum-topic.component.html',
  styleUrl: './forum-topic.css'
})
export class ForumTopicComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly forumsApi = inject(ForumsService);
  readonly auth = inject(AuthService);

  topicId = signal<string>('');
  topic = signal<TopicDetails | null>(null);
  repliesPage = signal<Page<ReplyListItem> | null>(null);
  
  replyContent = signal('');
  submitting = signal(false);

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.topicId.set(id);
        this.loadTopicDetails();
        this.loadReplies(0);
      }
    });
  }

  loadTopicDetails() {
    this.forumsApi.getTopicDetails(this.topicId()).subscribe({
      next: (data) => this.topic.set(data),
      error: (e) => console.error('Erro ao carregar tópico:', e)
    });
  }

  loadReplies(page: number) {
    this.forumsApi.getTopicReplies(this.topicId(), page, 20).subscribe({
      next: (data) => this.repliesPage.set(data),
      error: (e) => console.error('Erro ao carregar respostas:', e)
    });
  }

  onPageChange(newPage: number) {
    this.loadReplies(newPage);
  }

  submitReply() {
    if (!this.replyContent().trim() || this.submitting()) return;
    
    this.submitting.set(true);
    this.forumsApi.createReply(this.topicId(), { content: this.replyContent() }).subscribe({
      next: () => {
        this.replyContent.set('');
        this.submitting.set(false);
        // Refresh replies (go to last page ideally, or just reload current)
        this.loadReplies(this.repliesPage()?.totalPages ? this.repliesPage()!.totalPages - 1 : 0);
        // Refresh topic to update repliesCount
        this.loadTopicDetails();
      },
      error: (e) => {
        console.error('Erro ao enviar resposta:', e);
        this.submitting.set(false);
      }
    });
  }
}
