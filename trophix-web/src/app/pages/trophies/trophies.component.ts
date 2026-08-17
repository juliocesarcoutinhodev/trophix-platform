import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { TrophyService, ActivityFeedDTO, MissingTrophyDTO } from './trophy.service';

@Component({
  selector: 'app-trophies',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './trophies.component.html'
})
export class TrophiesComponent implements OnInit {
  auth = inject(AuthService);
  private trophyService = inject(TrophyService);
  
  // Tabs: 'feed' | 'missing'
  activeTab = signal<'feed' | 'missing'>('feed');

  activityFeed = signal<ActivityFeedDTO[]>([]);
  missingTrophies = signal<MissingTrophyDTO[]>([]);

  ngOnInit() {
    this.loadActivityFeed();
    if (this.auth.userSignal()) {
      this.loadMissingTrophies();
    }
  }

  loadActivityFeed() {
    this.trophyService.getActivityFeed(20).subscribe({
      next: (feed) => this.activityFeed.set(feed),
      error: (err) => console.error('Failed to load activity feed', err)
    });
  }

  loadMissingTrophies() {
    this.trophyService.getMissingTrophies(20).subscribe({
      next: (trophies) => this.missingTrophies.set(trophies),
      error: (err) => console.error('Failed to load missing trophies', err)
    });
  }

  setTab(tab: 'feed' | 'missing') {
    this.activeTab.set(tab);
  }

  getTrophyTypeColor(type: string): string {
    switch (type.toLowerCase()) {
      case 'platinum': return 'border-blue-300 bg-blue-900/30 text-blue-300';
      case 'gold': return 'border-yellow-500 bg-yellow-900/30 text-yellow-500';
      case 'silver': return 'border-slate-400 bg-slate-800/50 text-slate-300';
      case 'bronze': return 'border-amber-700 bg-amber-900/30 text-amber-600';
      default: return 'border-slate-600 bg-slate-800 text-slate-400';
    }
  }
}
