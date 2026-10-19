import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { UnidadOption } from './empleado.models';

@Injectable({
  providedIn: 'root'
})
export class UnidadService {

  private readonly http = inject(HttpClient);

  private readonly url =
    `${environment.apiUrl}/api/unidades`;

  listar(): Observable<UnidadOption[]> {
    return this.http.get<UnidadOption[]>(this.url);
  }
}