import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { ApiService } from '../../core/services/api.service';
import { apiErrorMessage } from '../../core/utils/api-error';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
})
export class ForgotPasswordComponent {
  private readonly api = inject(ApiService);

  protected readonly email = signal('');
  protected readonly error = signal<string | null>(null);
  protected readonly success = signal(false);
  protected readonly loading = signal(false);

  async submit(): Promise<void> {
    if (!this.email()) return;
    this.loading.set(true);
    this.error.set(null);
    try {
      await firstValueFrom(this.api.forgotPassword(this.email()));
      this.success.set(true);
    } catch (error) {
      this.error.set(apiErrorMessage(error, 'Não foi possível processar a solicitação. Verifique o e-mail inserido.'));
    } finally {
      this.loading.set(false);
    }
  }
}
