export interface UserProfile {
  username: string;
  avatarUrl: string | null;
  psnLevel: number | null;
  levelProgress: number | null;
  totalPlatinum: number | null;
  totalGold: number | null;
  totalSilver: number | null;
  totalBronze: number | null;
}

export interface UserGame {
  gameId: string;
  name: string;
  imageUrl: string;
  platform: string;
  progressPercentage: number;
  earnedTrophies: number;
  totalTrophies: number;
  lastPlayedAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface MessageResponse {
  message: string;
}
