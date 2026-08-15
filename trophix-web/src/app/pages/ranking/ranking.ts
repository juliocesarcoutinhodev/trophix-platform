import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { UserProfile } from '../../core/models/api.models';

@Component({
  selector: 'app-ranking',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './ranking.html',
})
export class RankingComponent implements OnInit {
  private readonly api = inject(ApiService);
  
  protected readonly hunters = signal<Array<UserProfile & { rank: number }>>([]);
  protected readonly loading = signal(true);

  async ngOnInit(): Promise<void> {
    try {
      this.loading.set(true);
      // Fetching top 50 for the full ranking page
      const data = await firstValueFrom(this.api.getTopHunters(50));
      const mapped = data.map((h, i) => ({
        ...h,
        rank: i + 1
      }));
      this.hunters.set(mapped);
    } catch (error) {
      console.error('Falha ao carregar ranking:', error);
      // Mock data in case API is not ready
      this.hunters.set([
        { rank: 1, username: 'Cout1nh030', totalPlatinum: 152, psnLevel: 320, levelProgress: 85, avatarUrl: null, totalGold: 450, totalSilver: 1200, totalBronze: 4500 },
        { rank: 2, username: 'Hakoom', totalPlatinum: 145, psnLevel: 310, levelProgress: 40, avatarUrl: null, totalGold: 430, totalSilver: 1100, totalBronze: 4200 },
        { rank: 3, username: 'PlatinumKing', totalPlatinum: 128, psnLevel: 295, levelProgress: 92, avatarUrl: null, totalGold: 380, totalSilver: 950, totalBronze: 3800 },
        { rank: 4, username: 'TrophyLover', totalPlatinum: 110, psnLevel: 250, levelProgress: 15, avatarUrl: null, totalGold: 300, totalSilver: 800, totalBronze: 3000 },
        { rank: 5, username: 'GamerGirl99', totalPlatinum: 98, psnLevel: 215, levelProgress: 60, avatarUrl: null, totalGold: 280, totalSilver: 700, totalBronze: 2500 },
        { rank: 6, username: 'DarkSoulsFan', totalPlatinum: 85, psnLevel: 190, levelProgress: 33, avatarUrl: null, totalGold: 200, totalSilver: 500, totalBronze: 1800 },
        { rank: 7, username: 'RPG_Master', totalPlatinum: 70, psnLevel: 175, levelProgress: 77, avatarUrl: null, totalGold: 180, totalSilver: 450, totalBronze: 1500 },
        { rank: 8, username: 'CasualGamer', totalPlatinum: 55, psnLevel: 140, levelProgress: 20, avatarUrl: null, totalGold: 150, totalSilver: 300, totalBronze: 1000 },
      ]);
    } finally {
      this.loading.set(false);
    }
  }
}
