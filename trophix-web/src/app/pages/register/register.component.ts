import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import { ApiService } from '../../core/services/api.service';
import { apiErrorMessage } from '../../core/utils/api-error';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  private readonly auth = inject(AuthService);
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);

  protected readonly step = signal<1 | 2 | 3>(1);
  protected readonly psnId = signal('');
  protected readonly email = signal('');
  protected readonly password = signal('');
  
  protected readonly linkToken = signal('');
  protected readonly error = signal<string | null>(null);
  protected readonly loading = signal(false);

  async requestLink(): Promise<void> {
    if (!this.psnId().trim()) {
      this.error.set('Informe sua PSN ID.');
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    try {
      const response = await firstValueFrom(this.api.requestAccountLink(this.psnId().trim()));
      this.linkToken.set(response.token);
      this.step.set(2);
    } catch (error) {
      this.error.set(apiErrorMessage(error, 'Não foi possível gerar o código. Verifique se a PSN ID está correta.'));
    } finally {
      this.loading.set(false);
    }
  }

  async validateLink(): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    try {
      const response = await firstValueFrom(this.api.validateAccountLink(this.psnId().trim()));
      if (response.isValid) {
        this.step.set(3);
      } else {
        this.error.set('Código não encontrado ou inválido. Certifique-se de salvar o código na seção Sobre Mim do seu perfil PSN.');
      }
    } catch (error) {
      this.error.set(apiErrorMessage(error, 'Falha ao validar. O código pode ter expirado ou não foi encontrado.'));
    } finally {
      this.loading.set(false);
    }
  }

  async submit(): Promise<void> {
    if (!this.email().trim() || this.password().length < 8) {
      this.error.set('Preencha os dados corretamente.');
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    try {
      await this.auth.register(this.psnId().trim(), this.email().trim(), this.password());
      await this.router.navigate(['/login']);
    } catch (error) {
      this.error.set(apiErrorMessage(error, 'Não foi possível finalizar o cadastro.'));
    } finally {
      this.loading.set(false);
    }
  }

  resetStep(): void {
    this.step.set(1);
    this.linkToken.set('');
    this.error.set(null);
  }
}
