import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { UserProfile } from '../models/api.models';
import { ApiService } from './api.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);

  private readonly user = signal<UserProfile | null>(null);
  readonly userSignal = this.user.asReadonly();
  readonly isAuthenticated = computed(() => this.user() !== null);
  readonly isAdmin = computed(() => this.user()?.roles?.includes('ROLE_ADMIN') ?? false);

  /** Resolves the session from the HttpOnly cookie (me/profile). */
  async initialize(): Promise<void> {
    try {
      this.user.set(await firstValueFrom(this.api.getMyProfile()));
    } catch {
      this.user.set(null);
    }
  }

  async login(email: string, password: string): Promise<void> {
    await firstValueFrom(this.api.login(email, password));
    await this.initialize();
  }

  async register(psnId: string, email: string, password: string): Promise<void> {
    await firstValueFrom(this.api.registerCompletion(psnId, email, password));
  }

  async logout(): Promise<void> {
    try {
      await firstValueFrom(this.api.logout());
    } catch {
      // Ignore errors on logout
    } finally {
      this.logoutLocally();
    }
  }

  refreshToken() {
    return this.api.refreshToken();
  }

  logoutLocally(): void {
    this.user.set(null);
    this.router.navigate(['/login']);
  }
}
