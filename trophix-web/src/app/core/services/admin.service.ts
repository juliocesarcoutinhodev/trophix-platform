import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AdminUser, Page } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly http = inject(HttpClient);

  getUsers(page = 0, size = 20, search?: string, role?: string) {
    let url = `/api/admin/users?page=${page}&size=${size}`;
    if (search) url += `&search=${encodeURIComponent(search)}`;
    if (role) url += `&role=${encodeURIComponent(role)}`;
    return this.http.get<Page<AdminUser>>(url);
  }

  updateUserRoles(userId: string, roles: string[]) {
    return this.http.put<void>(`/api/admin/users/${userId}/roles`, { roles });
  }
}
