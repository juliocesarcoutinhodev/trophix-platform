import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, filter, switchMap, take, throwError, BehaviorSubject } from 'rxjs';
import { AuthService } from '../services/auth.service';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<boolean | null>(null);

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const request = req.url.startsWith('/api/') ? req.clone({ withCredentials: true }) : req;

  return next(request).pipe(
    catchError((error) => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        // Prevent infinite loops if the refresh or login calls fail with 401
        if (req.url.includes('/api/auth/refresh') || req.url.includes('/api/auth/login')) {
          return throwError(() => error);
        }

        if (!isRefreshing) {
          isRefreshing = true;
          refreshTokenSubject.next(null);

          return authService.refreshToken().pipe(
            switchMap(() => {
              isRefreshing = false;
              refreshTokenSubject.next(true);
              return next(request);
            }),
            catchError((err) => {
              isRefreshing = false;
              authService.logoutLocally();
              return throwError(() => err);
            })
          );
        } else {
          // If another request is currently refreshing the token, wait for it
          return refreshTokenSubject.pipe(
            filter(result => result !== null),
            take(1),
            switchMap(() => next(request))
          );
        }
      }
      return throwError(() => error);
    })
  );
};
