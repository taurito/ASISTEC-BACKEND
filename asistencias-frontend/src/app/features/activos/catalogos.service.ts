import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { CatalogoOption } from './activo.models';

@Injectable({
  providedIn: 'root'
})
export class CatalogosService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/api`;

  listarTiposActivo(): Observable<CatalogoOption[]> {
    return this.http.get<CatalogoOption[]>(
      `${this.apiUrl}/tipos-activo`
    );
  }

  listarMarcas(): Observable<CatalogoOption[]> {
    return this.http.get<CatalogoOption[]>(
      `${this.apiUrl}/marcas`
    );
  }

  listarEstadosActivo(): Observable<CatalogoOption[]> {
    return this.http.get<CatalogoOption[]>(
      `${this.apiUrl}/estados-activo`
    );
  }
}