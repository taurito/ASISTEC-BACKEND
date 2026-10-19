import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { ActivoService } from '../../activos/activo.service';
import { ActivoResponse } from '../../activos/activo.models';
import { EmpleadoService } from '../../empleados/empleado.service';
import { EmpleadoResponse } from '../../empleados/empleado.models';
import { AsignacionActivoService } from '../asignacion-activo.service';
import { AsignacionActivoResponse } from '../asignacion.models';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-asignaciones',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './asignaciones.html',
  styleUrl: './asignaciones.css'
})
export class AsignacionesComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly activoService = inject(ActivoService);
  private readonly empleadoService = inject(EmpleadoService);
  private readonly asignacionService = inject(
    AsignacionActivoService
  );
  private readonly cdr = inject(ChangeDetectorRef);

  asignaciones: AsignacionActivoResponse[] = [];
  activosDisponibles: ActivoResponse[] = [];
  empleadosDisponibles: EmpleadoResponse[] = [];
  historialSeleccionado: AsignacionActivoResponse[] = [];

  filtro = '';
  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  mostrarHistorial = false;

  mensaje = '';
  error = '';

  readonly form = this.fb.group({
    activoId: [
      null as number | null,
      Validators.required
    ],
    empleadoId: [
      null as number | null,
      Validators.required
    ],
    observacion: ['', Validators.maxLength(255)]
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  get esAdmin(): boolean {
    return this.authService.tieneRol('ADMIN');
  }

  get asignacionesVigentes(): number {
    return this.asignaciones.filter(
      asignacion => asignacion.vigente
    ).length;
  }

  get asignacionesCerradas(): number {
    return this.asignaciones.filter(
      asignacion => !asignacion.vigente
    ).length;
  }

  get asignacionesFiltradas(): AsignacionActivoResponse[] {
    const termino = this.filtro.trim().toLowerCase();

    if (!termino) {
      return this.asignaciones;
    }

    return this.asignaciones.filter(asignacion =>
      [
        asignacion.codigoActivo,
        asignacion.activoNombre,
        asignacion.empleadoNombre,
        asignacion.observacion,
        asignacion.vigente ? 'vigente actual' : 'devuelto cerrado'
      ].some(valor =>
        (valor ?? '').toLowerCase().includes(termino)
      )
    );
  }

  cargarDatos(): void {
    this.cargarAsignaciones();

    // Consulta de empleados solo para ADMIN.
    // CONSULTA no tiene acceso a /api/empleados.
    if (this.esAdmin) {
      this.cargarOpciones();
    }
  }

  private cargarAsignaciones(): void {
    this.cargando = true;
    this.error = '';

    this.asignacionService.listar().subscribe({
      next: resultado => {
        this.asignaciones = [...resultado].sort(
          (a, b) =>
            b.fechaAsignacion.localeCompare(a.fechaAsignacion)
        );

        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: (error: HttpErrorResponse) => {
        console.error(
          'Error al cargar las asignaciones:',
          error
        );

        this.error =
          'No se pudieron cargar las asignaciones. '
          + 'Verifica tu conexión y tus permisos.';

        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  private cargarOpciones(): void {
    forkJoin({
      activos: this.activoService.listar(),
      empleados: this.empleadoService.listar()
    }).subscribe({
      next: resultado => {

        // Excluimos registros deshabilitados y equipos
        // que no deberían asignarse a un empleado.
        this.activosDisponibles =
          resultado.activos.filter(activo =>
            activo.activo
            && ['ACTIVO', 'MANTENIMIENTO'].includes(
              activo.estadoActivoNombre
            )
          );

        this.empleadosDisponibles =
          resultado.empleados.filter(
            empleado => empleado.activo
          );

        this.cdr.markForCheck();
      },
      error: error => {
        console.error(
          'Error al cargar las opciones de asignación:',
          error
        );

        this.error =
          'No se pudieron cargar los equipos o empleados disponibles.';

        this.cdr.markForCheck();
      }
    });
  }

  cambiarFiltro(event: Event): void {
    this.filtro =
      (event.target as HTMLInputElement).value;
  }

  abrirFormulario(): void {
    if (!this.esAdmin) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    this.form.reset({
      activoId: null,
      empleadoId: null,
      observacion: ''
    });

    this.mostrarFormulario = true;
  }

  cancelarFormulario(): void {
    this.mostrarFormulario = false;
    this.form.reset();
    this.guardando = false;
  }

  guardar(): void {
    if (!this.esAdmin || this.guardando) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const valor = this.form.getRawValue();

    if (
      valor.activoId === null ||
      valor.empleadoId === null
    ) {
      this.error = 'Selecciona un equipo y un empleado.';
      return;
    }

    const request = {
      activoId: valor.activoId,
      empleadoId: valor.empleadoId,
      observacion: valor.observacion?.trim() || null
    };

    this.guardando = true;

    this.asignacionService.asignar(request).subscribe({
      next: resultado => {
        this.guardando = false;
        this.mostrarFormulario = false;
        this.form.reset();

        this.mensaje =
          `El equipo ${resultado.codigoActivo} fue asignado correctamente.`;

        this.cargarAsignaciones();
        this.cdr.markForCheck();
      },
      error: (error: HttpErrorResponse) => {
        this.guardando = false;

        if (error.status === 400) {
          this.error =
            error.error?.message
            ?? 'Verifica los datos de la asignación.';
        } else if (error.status === 401) {
          this.error =
            'Tu sesión expiró. Inicia sesión nuevamente.';
        } else if (error.status === 403) {
          this.error =
            'No tienes permiso para asignar equipos.';
        } else if (error.status === 404) {
          this.error =
            error.error?.message
            ?? 'No se encontró el activo o empleado seleccionado.';
        } else if (error.status === 409) {
          this.error =
            error.error?.message
            ?? 'No se puede realizar esta asignación.';
        } else {
          this.error =
            'No se pudo registrar la asignación.';
        }

        this.cdr.markForCheck();
      }
    });
  }

  devolver(asignacion: AsignacionActivoResponse): void {
    if (!this.esAdmin || !asignacion.vigente) {
      return;
    }

    const confirmar = globalThis.confirm(
      `¿Deseas registrar la devolución de ${asignacion.codigoActivo} `
      + `por parte de ${asignacion.empleadoNombre}?`
    );

    if (!confirmar) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    this.asignacionService.devolver(asignacion.id).subscribe({
      next: () => {
        this.mensaje =
          `Se registró la devolución de ${asignacion.codigoActivo}.`;

        this.cargarAsignaciones();
        this.cdr.markForCheck();
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 403) {
          this.error =
            'No tienes permiso para registrar devoluciones.';
        } else if (error.status === 409) {
          this.error =
            error.error?.message
            ?? 'La asignación ya está cerrada.';
        } else if (error.status === 404) {
          this.error =
            'No se encontró la asignación.';
        } else {
          this.error =
            'No se pudo registrar la devolución.';
        }

        this.cdr.markForCheck();
      }
    });
  }

  verHistorial(activoId: number): void {
    this.error = '';

    this.asignacionService.listarHistorial(activoId).subscribe({
      next: historial => {
        this.historialSeleccionado = [...historial].sort(
          (a, b) =>
            b.fechaAsignacion.localeCompare(a.fechaAsignacion)
        );

        this.mostrarHistorial = true;
        this.cdr.markForCheck();
      },
      error: error => {
        console.error('Error al consultar historial:', error);
        this.error =
          'No se pudo consultar el historial de este equipo.';

        this.cdr.markForCheck();
      }
    });
  }

  cerrarHistorial(): void {
    this.mostrarHistorial = false;
    this.historialSeleccionado = [];
  }
}