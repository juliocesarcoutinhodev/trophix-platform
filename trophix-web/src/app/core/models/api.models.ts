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

// Forum Models
export interface CategoryListItem {
  id: string;
  name: string;
  description: string;
  orderIndex: number;
  topicsCount: number;
  lastTopicTitle?: string | null;
  lastTopicId?: string | null;
  lastTopicAuthor?: string | null;
  lastTopicUpdatedAt?: string | null;
}

export interface TopicListItem {
  id: string;
  categoryId: string;
  title: string;
  authorId: string;
  authorName: string;
  authorAvatarUrl?: string | null;
  authorRoles?: string[];
  viewsCount: number;
  repliesCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface ReplyListItem {
  id: string;
  topicId: string;
  authorId: string;
  authorName: string;
  authorAvatarUrl?: string | null;
  authorRoles?: string[];
  content: string;
  createdAt: string;
}

export interface TopicDetails {
  id: string;
  categoryId: string;
  categoryName: string;
  title: string;
  content: string;
  authorId: string;
  authorName: string;
  authorAvatarUrl?: string | null;
  authorRoles?: string[];
  viewsCount: number;
  repliesCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTopicRequest {
  categoryId: string;
  title: string;
  content: string;
}

export interface CreateReplyRequest {
  topicId: string;
  content: string;
}

export interface NewsArticleResponse {
  id: string;
  title: string;
  link: string;
  imageUrl: string | null;
  source: string;
  publishedAt: string;
  isFeatured: boolean;
}

export interface TrendingGameResponse {
  id: string;
  name: string;
  imageUrl: string;
  guidesCount: number;
}
