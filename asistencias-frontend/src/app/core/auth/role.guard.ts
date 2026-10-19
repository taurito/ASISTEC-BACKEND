import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = route => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const roles = route.data['roles'] as string[] | undefined;

  if (roles && authService.tieneRol(...roles)) {
    return true;
  }

  return router.createUrlTree(['/dashboard']);
};