import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { MaintenanceService } from '../services/maintenance.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const maintenanceService = inject(MaintenanceService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // 0 = ERR_CONNECTION_REFUSED (Backend off)
      // 502/503/504 = Server Gateway/Unavailable Errors
      if (error.status === 0 || error.status === 502 || error.status === 503 || error.status === 504) {
        maintenanceService.setMaintenanceMode(true);
      }
      
      // 403 = Forbidden (User is authenticated but lacks roles, e.g. not an ADMIN)
      if (error.status === 403) {
        console.warn('Acesso negado: Redirecionando para a home.');
        router.navigate(['/']);
      }

      return throwError(() => error);
    })
  );
};
