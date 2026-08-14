import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  
  // Note: For this to work, the backend MUST return the 'roles' array in the profile endpoint.
  // We check if the user has 'ROLE_ADMIN'.
  return auth.isAdmin() ? true : router.createUrlTree(['/']);
};
