import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Attaches credentials (HttpOnly JWT cookie) to every request against the
 * Trophix API. Required because the JWT travels only via cookie.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.startsWith('/api/')) {
    return next(req.clone({ withCredentials: true }));
  }
  return next(req);
};
