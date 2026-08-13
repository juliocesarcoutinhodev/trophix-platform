import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { ComingSoonComponent } from './pages/coming-soon/coming-soon.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { GameDetailComponent } from './pages/game-detail/game-detail.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { CreateGuide } from './pages/create-guide/create-guide';

export const routes: Routes = [
  { path: '', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'games', component: ComingSoonComponent, data: { title: 'Games' } },
  { path: 'trophies', component: ComingSoonComponent, data: { title: 'Trophies' } },
  { path: 'guides', component: ComingSoonComponent, data: { title: 'Guides' } },
  { path: 'create-guide', component: CreateGuide, canActivate: [authGuard] },
  { path: 'jogos/:id', component: GameDetailComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' },
];
