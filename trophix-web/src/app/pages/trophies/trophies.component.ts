import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { TrophyService, ActivityFeedDTO, MissingTrophyDTO } from './trophy.service';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-trophies',
  standalone: true,
  imports: [CommonModule, DatePipe, PaginationComponent],
  templateUrl: './trophies.component.html'
})
export class TrophiesComponent implements OnInit {
  auth = inject(AuthService);
  private trophyService = inject(TrophyService);
  
  // Tabs: 'feed' | 'missing'
  activeTab = signal<'feed' | 'missing'>('feed');

  activityFeed = signal<ActivityFeedDTO[]>([]);
  feedPage = signal(0);
  feedTotalPages = signal(0);
  feedTotalElements = signal(0);

  missingTrophies = signal<MissingTrophyDTO[]>([]);
  missingPage = signal(0);
  missingTotalPages = signal(0);
  missingTotalElements = signal(0);

  ngOnInit() {
    this.loadActivityFeed(0);
    if (this.auth.userSignal()) {
      this.loadMissingTrophies(0);
    }
  }

  loadActivityFeed(page: number) {
    this.trophyService.getActivityFeed(page, 20).subscribe({
      next: (pageData) => {
        this.activityFeed.set(pageData.content);
        this.feedPage.set(pageData.number);
        this.feedTotalPages.set(pageData.totalPages);
        this.feedTotalElements.set(pageData.totalElements);
      },
      error: (err) => console.error('Failed to load activity feed', err)
    });
  }

  onFeedPageChange(newPage: number) {
    this.loadActivityFeed(newPage);
  }

  loadMissingTrophies(page: number) {
    this.trophyService.getMissingTrophies(page, 20).subscribe({
      next: (pageData) => {
        this.missingTrophies.set(pageData.content);
        this.missingPage.set(pageData.number);
        this.missingTotalPages.set(pageData.totalPages);
        this.missingTotalElements.set(pageData.totalElements);
      },
      error: (err) => console.error('Failed to load missing trophies', err)
    });
  }

  onMissingPageChange(newPage: number) {
    this.loadMissingTrophies(newPage);
  }

  setTab(tab: 'feed' | 'missing') {
    this.activeTab.set(tab);
  }

  getTrophyTypeColor(type: string | undefined): string {
    if (!type) return 'border-slate-600 bg-slate-800 text-slate-400';
    switch (type.toLowerCase()) {
      case 'platinum': return 'border-blue-300 bg-blue-900/30 text-blue-300';
      case 'gold': return 'border-yellow-500 bg-yellow-900/30 text-yellow-500';
      case 'silver': return 'border-slate-400 bg-slate-800/50 text-slate-300';
      case 'bronze': return 'border-amber-700 bg-amber-900/30 text-amber-600';
      default: return 'border-slate-600 bg-slate-800 text-slate-400';
    }
  }
}
