import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../core/auth/auth.service';
import { ActivoService } from '../activos/activo.service';
import { ActivoResponse } from '../activos/activo.models';
import { AsistenciaService } from '../asistencias/asistencia.service';
import { AsistenciaResponse } from '../asistencias/asistencia.models';

interface ReincidenciaActivo {
  activoId: number;
  codigoActivo: string;
  activoNombre: string;
  totalAsistencias: number;
}

interface CargaTecnico {
  tecnicoId: number;
  tecnicoNombre: string;
  totalAsistencias: number;
  abiertas: number;
}

interface EstadoResumen {
  estado: string;
  total: number;
}

@Component({
  imports: [RouterLink, DatePipe],
  selector: 'app-dashboard',
  standalone: true,
  styleUrl: './dashboard.css',
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {

  private readonly authService = inject(AuthService);
   private readonly activoService = inject(ActivoService);
  private readonly asistenciaService = inject(AsistenciaService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);


  activos: ActivoResponse[] = [];
  asistencias: AsistenciaResponse[] = [];

  cargando = false;
  error = '';

  ngOnInit(): void {
    this.cargarDatos();
  }

  get usuario() {
    return this.authService.obtenerUsuario();
  }

  get nombreUsuario(): string {
    return this.usuario?.username ?? 'Usuario';
  }

  get rolPrincipal(): string {
    return this.usuario?.roles
      .map(rol => rol.replace('ROLE_', ''))
      .join(', ') ?? 'Sin rol';
  }

   get esAdmin(): boolean {
    return this.authService.tieneRol('ADMIN');
  }

  get puedeConsultarEmpleados(): boolean {
  return this.authService.tieneRol('ADMIN', 'TECNICO');
  }

   get totalActivos(): number {
    return this.activos.filter(activo => activo.activo).length;
  }

  get asistenciasAbiertas(): AsistenciaResponse[] {
    return this.asistencias.filter(
      asistencia => asistencia.fechaCierre === null
    );
  }

  get totalAbiertas(): number {
    return this.asistenciasAbiertas.length;
  }

  get totalPendientesEntrega(): number {
    return this.asistenciasAbiertas.filter(
      asistencia =>
        asistencia.estadoAsistenciaNombre === 'PARA ENTREGAR'
    ).length;
  }

  get totalCerradas(): number {
    return this.asistencias.filter(
      asistencia => asistencia.fechaCierre !== null
    ).length;
  }

  get promedioCierreHoras(): number | null {
    const cerradas = this.asistencias.filter(
      asistencia => asistencia.fechaCierre !== null
    );

    if (cerradas.length === 0) {
      return null;
    }

    const totalMilisegundos = cerradas.reduce(
      (total, asistencia) => {
        const ingreso = new Date(asistencia.fechaIngreso).getTime();
        const cierre = new Date(asistencia.fechaCierre!).getTime();

        if (
          !Number.isFinite(ingreso) ||
          !Number.isFinite(cierre) ||
          cierre < ingreso
        ) {
          return total;
        }

        return total + (cierre - ingreso);
      },
      0
    );

    const cierresValidos = cerradas.filter(asistencia => {
      const ingreso = new Date(asistencia.fechaIngreso).getTime();
      const cierre = new Date(asistencia.fechaCierre!).getTime();

      return Number.isFinite(ingreso)
        && Number.isFinite(cierre)
        && cierre >= ingreso;
    }).length;

    if (cierresValidos === 0) {
      return null;
    }

    return totalMilisegundos / cierresValidos / 3_600_000;
  }

  get promedioCierreTexto(): string {
    const horas = this.promedioCierreHoras;

    if (horas === null) {
      return 'Sin datos';
    }

    if (horas < 24) {
      return `${Math.round(horas)} horas`;
    }

    return `${(horas / 24).toFixed(1)} días`;
  }

  get reincidenciaActivos(): ReincidenciaActivo[] {
    const grupos = new Map<number, ReincidenciaActivo>();

    for (const asistencia of this.asistencias) {
      const existente = grupos.get(asistencia.activoId);

      if (existente) {
        existente.totalAsistencias++;
      } else {
        grupos.set(asistencia.activoId, {
          activoId: asistencia.activoId,
          codigoActivo: asistencia.codigoActivo,
          activoNombre: asistencia.activoNombre,
          totalAsistencias: 1
        });
      }
    }

    return [...grupos.values()]
      .filter(item => item.totalAsistencias > 1)
      .sort(
        (a, b) => b.totalAsistencias - a.totalAsistencias
      )
      .slice(0, 5);
  }

  get cargaTecnicos(): CargaTecnico[] {
    const grupos = new Map<number, CargaTecnico>();

    for (const asistencia of this.asistencias) {
      const existente = grupos.get(asistencia.tecnicoId);
      const abierta = asistencia.fechaCierre === null;

      if (existente) {
        existente.totalAsistencias++;

        if (abierta) {
          existente.abiertas++;
        }
      } else {
        grupos.set(asistencia.tecnicoId, {
          tecnicoId: asistencia.tecnicoId,
          tecnicoNombre: asistencia.tecnicoNombre,
          totalAsistencias: 1,
          abiertas: abierta ? 1 : 0
        });
      }
    }

    return [...grupos.values()]
      .sort(
        (a, b) => b.totalAsistencias - a.totalAsistencias
      )
      .slice(0, 5);
  }

  get resumenEstados(): EstadoResumen[] {
    const grupos = new Map<string, number>();

    for (const asistencia of this.asistencias) {
      const estado = asistencia.estadoAsistenciaNombre;

      grupos.set(
        estado,
        (grupos.get(estado) ?? 0) + 1
      );
    }

    return [...grupos.entries()]
      .map(([estado, total]) => ({ estado, total }))
      .sort((a, b) => b.total - a.total);
  }

  get ultimasAsistencias(): AsistenciaResponse[] {
    return [...this.asistencias]
      .sort(
        (a, b) =>
          new Date(b.fechaIngreso).getTime()
          - new Date(a.fechaIngreso).getTime()
      )
      .slice(0, 6);
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = '';

    forkJoin({
      activos: this.activoService.listar(),
      asistencias: this.asistenciaService.listar()
    }).subscribe({
      next: resultado => {
        this.activos = resultado.activos;
        this.asistencias = resultado.asistencias;
        this.cargando = false;

        this.cdr.markForCheck();
      },
      error: (error: HttpErrorResponse) => {
        console.error(
          'Error al cargar el dashboard:',
          error
        );

        if (error.status === 401) {
          this.error =
            'Tu sesión expiró. Inicia sesión nuevamente.';
        } else if (error.status === 403) {
          this.error =
            'No tienes permiso para consultar los indicadores.';
        } else {
          this.error =
            'No se pudieron cargar los indicadores del sistema.';
        }

        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  cerrarSesion(): void {
    this.authService.cerrarSesion();
    void this.router.navigateByUrl('/login');
  }
}
