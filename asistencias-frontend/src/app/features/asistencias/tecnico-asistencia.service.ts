import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { TecnicoOption } from './asistencia.models';

@Injectable({
  providedIn: 'root'
})
export class TecnicoAsistenciaService {

  private readonly http = inject(HttpClient);

  private readonly url =
    `${environment.apiUrl}/api/usuarios/tecnicos`;

  listar(): Observable<TecnicoOption[]> {
    return this.http.get<TecnicoOption[]>(this.url);
  }
}