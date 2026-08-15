import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { marked } from 'marked';

import { AdminService } from '../../../core/services/admin.service';
import { GuideResponse } from '../../../core/models/api.models';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-admin-guides',
  standalone: true,
  imports: [DatePipe, FormsModule, PaginationComponent, RouterLink],
  templateUrl: './admin-guides.component.html',
})
export class AdminGuidesComponent implements OnInit {
  private readonly adminApi = inject(AdminService);

  protected readonly guides = signal<GuideResponse[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);

  protected readonly processingId = signal<string | null>(null);

  // Pagination
  protected readonly currentPage = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly totalElements = signal(0);

  // Edit state
  protected editingGuideId = signal<string | null>(null);
  protected editTitle = '';
  protected editDescription = '';
  protected editContent = '';
  protected editVideoUrl = '';

  // Preview state
  protected previewStates = signal<Record<string, boolean>>({});

  togglePreview(guideId: string): void {
    this.previewStates.update(states => ({
      ...states,
      [guideId]: !this.isPreviewing(guideId)
    }));
  }

  isPreviewing(guideId: string): boolean {
    const state = this.previewStates()[guideId];
    return state === undefined ? true : state; // True by default
  }

  getParsedContent(raw: string): string {
    if (!raw) return '';
    return marked.parse(raw) as string;
  }

  async ngOnInit(): Promise<void> {
    await this.loadGuides();
  }

  async loadGuides(pageIndex = 0): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    try {
      const page = await firstValueFrom(this.adminApi.getPendingGuides(pageIndex, 20));
      this.guides.set(page.content);
      this.currentPage.set(page.number);
      this.totalPages.set(page.totalPages);
      this.totalElements.set(page.totalElements);
    } catch (e) {
      this.error.set('Falha ao carregar guias pendentes. Verifique se a API /api/admin/guides/pending já foi implementada.');
    } finally {
      this.loading.set(false);
    }
  }

  async approveGuide(guideId: string): Promise<void> {
    this.processingId.set(guideId);
    try {
      await firstValueFrom(this.adminApi.approveGuide(guideId));
      this.guides.update(list => list.filter(g => g.id !== guideId));
      this.successMessage.set('Guia aprovado e publicado com sucesso!');
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (e) {
      this.error.set('Erro ao aprovar guia.');
    } finally {
      this.processingId.set(null);
    }
  }

  async rejectGuide(guideId: string): Promise<void> {
    if (!confirm('Tem certeza que deseja rejeitar e excluir este guia?')) return;
    
    this.processingId.set(guideId);
    try {
      await firstValueFrom(this.adminApi.rejectGuide(guideId));
      this.guides.update(list => list.filter(g => g.id !== guideId));
      this.successMessage.set('Guia rejeitado e removido da fila.');
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (e) {
      this.error.set('Erro ao rejeitar guia.');
    } finally {
      this.processingId.set(null);
    }
  }

  startEditing(guide: GuideResponse): void {
    this.editingGuideId.set(guide.id);
    this.editTitle = guide.title;
    this.editDescription = guide.description;
    this.editContent = guide.content;
    this.editVideoUrl = guide.videoUrl || '';
  }

  cancelEditing(): void {
    this.editingGuideId.set(null);
  }

  async updateGuide(guideId: string): Promise<void> {
    this.processingId.set(guideId);
    this.error.set(null);
    try {
      await firstValueFrom(this.adminApi.updateGuide(guideId, {
        title: this.editTitle,
        description: this.editDescription,
        content: this.editContent,
        videoUrl: this.editVideoUrl || null
      }));
      this.successMessage.set('Guia atualizado com sucesso!');
      this.editingGuideId.set(null);
      this.loadGuides(this.currentPage());
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (e) {
      this.error.set('Erro ao atualizar o guia. Verifique se o endpoint PUT /api/admin/guides/{id} foi criado.');
    } finally {
      this.processingId.set(null);
    }
  }
}
