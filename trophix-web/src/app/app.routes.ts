import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { ComingSoonComponent } from './pages/coming-soon/coming-soon.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { HomeComponent } from './pages/home/home';
import { GameDetailComponent } from './pages/game-detail/game-detail.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { CreateGuide } from './pages/create-guide/create-guide';
import { Guides } from './pages/guides/guides';
import { GuideDetailComponent } from './pages/guide-detail/guide-detail';
import { RankingComponent } from './pages/ranking/ranking';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { MaintenanceComponent } from './pages/maintenance/maintenance.component';
import { AdminLayoutComponent } from './layout/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from './pages/admin/dashboard/admin-dashboard.component';
import { AdminUsersComponent } from './pages/admin/users/admin-users.component';
import { AdminGuidesComponent } from './pages/admin/guides/admin-guides.component';
import { AdminAllGuidesComponent } from './pages/admin/all-guides/admin-all-guides.component';
import { AdminSettingsComponent } from './pages/admin/settings/admin-settings.component';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [adminGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: AdminDashboardComponent },
      { path: 'users', component: AdminUsersComponent },
      { path: 'guides', component: AdminGuidesComponent },
      { path: 'all-guides', component: AdminAllGuidesComponent },
      { path: 'guides/create', component: CreateGuide },
      { path: 'settings', component: AdminSettingsComponent },
      { path: 'ofertas', loadComponent: () => import('./pages/admin/offers/offers-admin.component').then(m => m.OffersAdminComponent) },
    ]
  },
  { path: '', component: HomeComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'games', loadComponent: () => import('./pages/games/games.component').then(m => m.GamesComponent), data: { title: 'Games' } },
  { path: 'trophies', loadComponent: () => import('./pages/trophies/trophies.component').then(m => m.TrophiesComponent), data: { title: 'Trophies' } },
  { path: 'news', loadComponent: () => import('./pages/news/news.component').then(m => m.NewsComponent), data: { title: 'Notícias' } },
  { path: 'ofertas', loadComponent: () => import('./pages/offers/offers.component').then(m => m.OffersComponent), data: { title: 'LootBox' } },
  { path: 'guides', component: Guides },
  { path: 'ranking', component: RankingComponent, data: { title: 'Ranking Global' } },
  { path: 'guides/:id', component: GuideDetailComponent },
  { path: 'create-guide', component: CreateGuide, canActivate: [authGuard] },
  { path: 'games/:id', component: GameDetailComponent, canActivate: [authGuard] },
  { path: 'forums', loadComponent: () => import('./pages/public/forums/forum-home/forum-home').then(m => m.ForumHomeComponent) },
  { path: 'forums/category/:id', loadComponent: () => import('./pages/public/forums/forum-category/forum-category').then(m => m.ForumCategoryComponent) },
  { path: 'forums/topic/:id', loadComponent: () => import('./pages/public/forums/forum-topic/forum-topic').then(m => m.ForumTopicComponent) },
  { path: 'maintenance', component: MaintenanceComponent, data: { title: 'Manutenção' } },
  { path: '**', redirectTo: '' },
];
