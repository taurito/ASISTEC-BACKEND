import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { HistorialEstadoAsistenciaResponse } from './asistencia.models';

@Injectable({
  providedIn: 'root'
})
export class HistorialAsistenciaService {

  private readonly http = inject(HttpClient);

  private readonly url =
    `${environment.apiUrl}/api/historial-estados-asistencia`;

  listarPorAsistencia(
    asistenciaId: number
  ): Observable<HistorialEstadoAsistenciaResponse[]> {
    return this.http.get<HistorialEstadoAsistenciaResponse[]>(
      `${this.url}/asistencia/${asistenciaId}`
    );
  }
}