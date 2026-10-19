import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  AsistenciaRequest,
  AsistenciaResponse,
  AsistenciaUpdateRequest,
  CambioEstadoAsistenciaRequest
} from './asistencia.models';

@Injectable({
  providedIn: 'root'
})
export class AsistenciaService {

  private readonly http = inject(HttpClient);

  private readonly url =
    `${environment.apiUrl}/api/asistencias`;

  listar(): Observable<AsistenciaResponse[]> {
    return this.http.get<AsistenciaResponse[]>(this.url);
  }

  buscarPorId(id: number): Observable<AsistenciaResponse> {
    return this.http.get<AsistenciaResponse>(
      `${this.url}/${id}`
    );
  }

  crear(
    request: AsistenciaRequest
  ): Observable<AsistenciaResponse> {
    return this.http.post<AsistenciaResponse>(
      this.url,
      request
    );
  }

  actualizar(
    id: number,
    request: AsistenciaUpdateRequest
  ): Observable<AsistenciaResponse> {
    return this.http.put<AsistenciaResponse>(
      `${this.url}/${id}`,
      request
    );
  }

  cambiarEstado(
    id: number,
    request: CambioEstadoAsistenciaRequest
  ): Observable<AsistenciaResponse> {
    return this.http.patch<AsistenciaResponse>(
      `${this.url}/${id}/estado`,
      request
    );
  }
}