import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  AsignacionActivoRequest,
  AsignacionActivoResponse
} from './asignacion.models';

@Injectable({
  providedIn: 'root'
})
export class AsignacionActivoService {

  private readonly http = inject(HttpClient);

  private readonly url =
    `${environment.apiUrl}/api/asignaciones-activo`;

  listar(): Observable<AsignacionActivoResponse[]> {
    return this.http.get<AsignacionActivoResponse[]>(
      this.url
    );
  }

  buscarActual(
    activoId: number
  ): Observable<AsignacionActivoResponse> {
    return this.http.get<AsignacionActivoResponse>(
      `${this.url}/activo/${activoId}/actual`
    );
  }

  listarHistorial(
    activoId: number
  ): Observable<AsignacionActivoResponse[]> {
    return this.http.get<AsignacionActivoResponse[]>(
      `${this.url}/activo/${activoId}/historial`
    );
  }

  asignar(
    request: AsignacionActivoRequest
  ): Observable<AsignacionActivoResponse> {
    return this.http.post<AsignacionActivoResponse>(
      this.url,
      request
    );
  }

  devolver(
    id: number
  ): Observable<AsignacionActivoResponse> {
    return this.http.patch<AsignacionActivoResponse>(
      `${this.url}/${id}/devolver`,
      {}
    );
  }
}