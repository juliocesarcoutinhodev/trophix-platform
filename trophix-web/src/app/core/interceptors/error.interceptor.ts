import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MaintenanceService } from '../services/maintenance.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const maintenanceService = inject(MaintenanceService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // 0 = ERR_CONNECTION_REFUSED (Backend off)
      // 502/503/504 = Server Gateway/Unavailable Errors
      if (error.status === 0 || error.status === 502 || error.status === 503 || error.status === 504) {
        maintenanceService.setMaintenanceMode(true);
      }
      return throwError(() => error);
    })
  );
};
