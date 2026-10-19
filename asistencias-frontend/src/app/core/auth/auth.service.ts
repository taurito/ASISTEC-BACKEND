import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse } from './auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http = inject(HttpClient);
  private readonly platformId = inject(PLATFORM_ID);

  private readonly apiUrl = environment.apiUrl;
  private readonly tokenKey = 'asistencias_access_token';
  private readonly userKey = 'asistencias_user';

  login(request: LoginRequest): Observable<LoginResponse> {

    return this.http.post<LoginResponse>(
      `${this.apiUrl}/api/auth/login`,
      request
    ).pipe(
      tap(response => {
        this.guardarSesion(response);
      })
    );
  }

  private guardarSesion(response: LoginResponse): void {

    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    sessionStorage.setItem(
      this.tokenKey,
      response.accessToken
    );

    sessionStorage.setItem(
      this.userKey,
      JSON.stringify({
        usuarioId: response.usuarioId,
        username: response.username,
        empleadoId: response.empleadoId,
        roles: response.roles
      })
    );
  }

  obtenerToken(): string | null {

    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }

    return sessionStorage.getItem(this.tokenKey);
  }

  obtenerUsuario(): Omit<LoginResponse, 'accessToken' | 'tokenType' | 'expiresIn'> | null {

    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }

    const usuario = sessionStorage.getItem(this.userKey);

    if (!usuario) {
      return null;
    }

    try {
      return JSON.parse(usuario);
    } catch {
      this.cerrarSesion();
      return null;
    }
  }

  estaAutenticado(): boolean {
    return this.obtenerToken() !== null;
  }

  cerrarSesion(): void {

    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    sessionStorage.removeItem(this.tokenKey);
    sessionStorage.removeItem(this.userKey);
  }

  tieneRol(...rolesPermitidos: string[]): boolean {

  const usuario = this.obtenerUsuario();

  if (!usuario) {
    return false;
  }

  return rolesPermitidos.some(rol => {
    const autoridad = rol.startsWith('ROLE_')
      ? rol
      : `ROLE_${rol}`;

    return usuario.roles.includes(autoridad);
  });
}
}