import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  ActividadAsistenciaRequest,
  ActividadAsistenciaResponse
} from './asistencia.models';

@Injectable({
  providedIn: 'root'
})
export class ActividadAsistenciaService {

  private readonly http = inject(HttpClient);

  private readonly url =
    `${environment.apiUrl}/api/actividades-asistencia`;

  listarPorAsistencia(
    asistenciaId: number
  ): Observable<ActividadAsistenciaResponse[]> {
    return this.http.get<ActividadAsistenciaResponse[]>(
      `${this.url}/asistencia/${asistenciaId}`
    );
  }

  crear(
    request: ActividadAsistenciaRequest
  ): Observable<ActividadAsistenciaResponse> {
    return this.http.post<ActividadAsistenciaResponse>(
      this.url,
      request
    );
  }

  actualizar(
    id: number,
    request: ActividadAsistenciaRequest
  ): Observable<ActividadAsistenciaResponse> {
    return this.http.put<ActividadAsistenciaResponse>(
      `${this.url}/${id}`,
      request
    );
  }
}