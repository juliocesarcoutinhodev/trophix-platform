import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AdminUser, GuideResponse, Page, GlobalSettings } from '../models/api.models';

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

  getPendingGuides(page = 0, size = 20) {
    return this.http.get<Page<GuideResponse>>(`/api/admin/guides/pending?page=${page}&size=${size}`);
  }

  getAllGuides(page = 0, size = 20) {
    return this.http.get<Page<GuideResponse>>(`/api/admin/guides?page=${page}&size=${size}`);
  }

  approveGuide(guideId: string) {
    return this.http.post<void>(`/api/admin/guides/${guideId}/approve`, {});
  }

  rejectGuide(guideId: string) {
    return this.http.post<void>(`/api/admin/guides/${guideId}/reject`, {});
  }

  deleteGuide(guideId: string) {
    return this.http.delete<void>(`/api/admin/guides/${guideId}`);
  }

  updateGuide(guideId: string, data: any) {
    return this.http.put<void>(`/api/admin/guides/${guideId}`, data);
  }

  getGlobalSettings() {
    return this.http.get<GlobalSettings>('/api/admin/settings');
  }

  updateGlobalSettings(settings: GlobalSettings) {
    return this.http.put<void>('/api/admin/settings', settings);
  }

  uploadMedia(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string }>('/api/admin/media/upload', formData);
  }
}
