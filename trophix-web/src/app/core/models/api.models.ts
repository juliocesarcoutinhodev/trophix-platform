export interface UserProfile {
  username: string;
  avatarUrl: string | null;
  psnLevel: number | null;
  levelProgress: number | null;
  totalPlatinum: number | null;
  totalGold: number | null;
  totalSilver: number | null;
  totalBronze: number | null;
  roles?: string[];
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

export interface GameDetail {
  gameId: string;
  name: string;
  imageUrl: string | null;
  platform: string | null;
  progressPercentage: number;
  earnedTrophies: number;
  totalTrophies: number;
  rarity: {
    platinum: number;
    gold: number;
    silver: number;
    bronze: number;
  };
}

export interface TrophyStatus {
  id: string;
  psnTrophyId: number;
  name: string;
  description: string | null;
  type: string;
  iconUrl: string | null;
  earned: boolean;
  earnedAt: string | null;
}

export interface GuideResponse {
  id: string;
  trophyId: string | null;
  gameId: string;
  authorId: string;
  authorName?: string;
  authorAvatarUrl?: string;
  gameName?: string;
  imageUrl?: string;
  title: string;
  description: string;
  content: string;
  videoUrl: string | null;
  status: string;
  upvotesCount: number;
  currentUserVoted?: boolean;
  createdAt: string;
}

export interface SubmitGuideRequest {
  title: string;
  description: string;
  content: string;
  videoUrl?: string;
}

export interface VoteResponse {
  voted: boolean;
  upvotesCount: number;
  message: string;
}

export interface AdminUser {
  id: string;
  username: string;
  email: string;
  roles: string[];
  avatarUrl?: string | null;
  createdAt?: string;
}

export interface AdminDashboardStats {
  newUsersCount: number;
  newUsersTrend: number;
  newUsersTrendText: string;
  
  pendingGuidesCount: number;
  pendingGuidesTrend: number;
  pendingGuidesTrendText: string;
  
  syncsCount: number;
  syncsTrendText: string;
  syncsTrendPositive: boolean;
  
  reportsCount: number;
  reportsTrend: number;
  reportsTrendText: string;
}

export interface GlobalSettings {
  siteName: string;
  logoUrl: string | null;
  heroImageUrl: string | null;
  contactEmail: string;
  discordUrl: string;
  twitterUrl: string;
  youtubeUrl: string;
  instagramUrl: string;
  heroTitle: string;
  heroSubtitle: string;
  globalAlertEnabled: boolean;
  globalAlertText: string;
  footerText: string;
  requireGuideApproval: boolean;
  forbiddenWords: string;
  metaTitle: string;
  metaDescription: string;
}
