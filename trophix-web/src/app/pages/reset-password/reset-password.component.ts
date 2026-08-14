import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { ApiService } from '../../core/services/api.service';
import { apiErrorMessage } from '../../core/utils/api-error';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './reset-password.component.html',
})
export class ResetPasswordComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly api = inject(ApiService);

  protected readonly newPassword = signal('');
  protected readonly confirmPassword = signal('');
  protected readonly token = signal<string | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly success = signal(false);
  protected readonly loading = signal(false);
  protected readonly showPassword = signal(false);

  ngOnInit(): void {
    // The link in email uses a query param: ?token=...
    this.route.queryParamMap.subscribe(params => {
      const t = params.get('token');
      if (t) {
        this.token.set(t);
      } else {
        this.error.set('Link de recuperação inválido ou ausente.');
      }
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword.update(v => !v);
  }

  async submit(): Promise<void> {
    if (!this.token()) {
      this.error.set('Token ausente. Use o link enviado por e-mail.');
      return;
    }
    if (this.newPassword() !== this.confirmPassword()) {
      this.error.set('As senhas não coincidem.');
      return;
    }
    if (this.newPassword().length < 8) {
      this.error.set('A nova senha deve ter no mínimo 8 caracteres.');
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    try {
      await firstValueFrom(this.api.resetPassword(this.token()!, this.newPassword()));
      this.success.set(true);
    } catch (error) {
      this.error.set(apiErrorMessage(error, 'Falha ao redefinir a senha. O link pode ter expirado.'));
    } finally {
      this.loading.set(false);
    }
  }
}
