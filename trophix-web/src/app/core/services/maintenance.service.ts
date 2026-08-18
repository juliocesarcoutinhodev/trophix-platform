import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class MaintenanceService {
  private readonly _isMaintenanceMode = signal(false);
  readonly isMaintenanceMode = this._isMaintenanceMode.asReadonly();
  
  private pollingInterval: any = null;

  setMaintenanceMode(active: boolean) {
    if (active && !this._isMaintenanceMode()) {
      this._isMaintenanceMode.set(true);
      this.startHealthCheck();
    } else if (!active && this._isMaintenanceMode()) {
      this._isMaintenanceMode.set(false);
      this.stopHealthCheck();
    }
  }

  private startHealthCheck() {
    if (this.pollingInterval) return;
    
    // Poll every 5 seconds to check if API is back
    this.pollingInterval = setInterval(async () => {
      try {
        // Use native fetch to bypass Angular HTTP Interceptors
        // Using an endpoint like /api/public/health is ideal here
        const response = await fetch('/api/public/health', { method: 'GET' });
        if (response.ok || response.status === 404 || response.status === 401) {
          // If we get a valid HTTP response (even 404 or 401 means the API is responding)
          // it means the backend is up again. We only trigger maintenance on 0, 502, 503, 504.
          this.setMaintenanceMode(false);
          window.location.reload(); // Reload the page to ensure fresh state
        }
      } catch (e) {
        // Still down, keep polling
      }
    }, 5000);
  }

  private stopHealthCheck() {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
      this.pollingInterval = null;
    }
  }
}
