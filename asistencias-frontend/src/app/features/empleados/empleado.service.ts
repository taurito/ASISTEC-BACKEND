import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  EmpleadoRequest,
  EmpleadoResponse
} from './empleado.models';

@Injectable({
  providedIn: 'root'
})
export class EmpleadoService {

  private readonly http = inject(HttpClient);

  private readonly url =
    `${environment.apiUrl}/api/empleados`;

  listar(): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(this.url);
  }

  buscarPorId(id: number): Observable<EmpleadoResponse> {
    return this.http.get<EmpleadoResponse>(
      `${this.url}/${id}`
    );
  }

  crear(request: EmpleadoRequest): Observable<EmpleadoResponse> {
    return this.http.post<EmpleadoResponse>(
      this.url,
      request
    );
  }

  actualizar(
    id: number,
    request: EmpleadoRequest
  ): Observable<EmpleadoResponse> {
    return this.http.put<EmpleadoResponse>(
      `${this.url}/${id}`,
      request
    );
  }
}