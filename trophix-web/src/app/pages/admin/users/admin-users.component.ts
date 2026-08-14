import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';

import { AdminService } from '../../../core/services/admin.service';
import { AdminUser } from '../../../core/models/api.models';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [DatePipe, FormsModule],
  templateUrl: './admin-users.component.html',
})
export class AdminUsersComponent implements OnInit {
  private readonly adminApi = inject(AdminService);

  protected readonly users = signal<AdminUser[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  
  // Filters
  protected readonly searchQuery = signal('');
  protected readonly roleFilter = signal('ALL');
  
  // For editing roles
  protected readonly editingUserId = signal<string | null>(null);
  protected editRoles = [] as string[];

  async ngOnInit(): Promise<void> {
    await this.loadUsers();
  }

  async loadUsers(): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    try {
      const role = this.roleFilter() === 'ALL' ? undefined : this.roleFilter();
      const page = await firstValueFrom(this.adminApi.getUsers(0, 20, this.searchQuery(), role));
      this.users.set(page.content);
    } catch (e) {
      this.error.set('Falha ao carregar usuários. Verifique se a API /api/admin/users já foi implementada e está retornando dados.');
    } finally {
      this.loading.set(false);
    }
  }

  startEditing(user: AdminUser): void {
    this.editingUserId.set(user.id);
    this.editRoles = [...user.roles];
  }

  cancelEditing(): void {
    this.editingUserId.set(null);
  }

  toggleEditRole(role: string): void {
    if (this.editRoles.includes(role)) {
      this.editRoles = this.editRoles.filter(r => r !== role);
    } else {
      this.editRoles.push(role);
    }
  }

  async saveRoles(userId: string): Promise<void> {
    this.error.set(null);
    this.successMessage.set(null);
    try {
      await firstValueFrom(this.adminApi.updateUserRoles(userId, this.editRoles));
      // update local signal without needing to reload the whole page
      this.users.update(users => users.map(u => u.id === userId ? { ...u, roles: [...this.editRoles] } : u));
      this.editingUserId.set(null);
      this.successMessage.set('Permissões atualizadas com sucesso!');
      setTimeout(() => this.successMessage.set(null), 3500);
    } catch (e) {
      this.error.set('Erro ao salvar as permissões. Verifique se a API está funcionando.');
    }
  }
}
