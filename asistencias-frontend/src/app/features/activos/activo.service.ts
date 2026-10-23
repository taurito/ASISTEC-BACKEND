import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ActivoRequest, ActivoResponse } from './activo.models';

@Injectable({
  providedIn: 'root'
})
export class ActivoService {

  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/api/activos`;

  listar(): Observable<ActivoResponse[]> {
    return this.http.get<ActivoResponse[]>(this.url);
  }

  buscarPorId(id: number): Observable<ActivoResponse> {
    return this.http.get<ActivoResponse>(`${this.url}/${id}`);
  }

  crear(request: ActivoRequest): Observable<ActivoResponse> {
    return this.http.post<ActivoResponse>(this.url, request);
  }

  actualizar(
    id: number,
    request: ActivoRequest
  ): Observable<ActivoResponse> {
    return this.http.put<ActivoResponse>(
      `${this.url}/${id}`,
      request
    );
  }

  listarRecientes(): Observable<ActivoResponse[]> {
    return this.http.get<ActivoResponse[]>(
      `${this.url}/api/activos/recientes`
    );
  }

  buscarActivos(termino: string): Observable<ActivoResponse[]> {
    const params = new HttpParams().set('termino', termino);

    return this.http.get<ActivoResponse[]>(
      `${this.url}/api/activos/buscar`,
      { params }
    );
  }
}