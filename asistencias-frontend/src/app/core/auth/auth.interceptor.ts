import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AuthService);

  const token = authService.obtenerToken();

  const urlLogin = `${environment.apiUrl}/api/auth/login`;

  const esPeticionApi = req.url.startsWith(environment.apiUrl);
  const esLogin = req.url === urlLogin;

  if (token && esPeticionApi && !esLogin) {

    const requestAutorizada = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    return next(requestAutorizada);
  }

  return next(req);
};