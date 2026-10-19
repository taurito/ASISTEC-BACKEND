import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AsistenciaCatalogoOption } from './asistencia.models';

@Injectable({
  providedIn: 'root'
})
export class AsistenciaCatalogosService {

  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/api`;

  listarTipos(): Observable<AsistenciaCatalogoOption[]> {
    return this.http.get<AsistenciaCatalogoOption[]>(
      `${this.url}/tipos-asistencia`
    );
  }

  listarEstados(): Observable<AsistenciaCatalogoOption[]> {
    return this.http.get<AsistenciaCatalogoOption[]>(
      `${this.url}/estados-asistencia`
    );
  }
}