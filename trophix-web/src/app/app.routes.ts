import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { ComingSoonComponent } from './pages/coming-soon/coming-soon.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { GameDetailComponent } from './pages/game-detail/game-detail.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { CreateGuide } from './pages/create-guide/create-guide';
import { Guides } from './pages/guides/guides';
import { GuideDetailComponent } from './pages/guide-detail/guide-detail';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'games', component: ComingSoonComponent, data: { title: 'Games' } },
  { path: 'trophies', component: ComingSoonComponent, data: { title: 'Trophies' } },
  { path: 'guides', component: Guides },
  { path: 'guides/:id', component: GuideDetailComponent },
  { path: 'create-guide', component: CreateGuide, canActivate: [authGuard] },
  { path: 'games/:id', component: GameDetailComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' },
];
