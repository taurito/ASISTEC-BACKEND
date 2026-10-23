import { DatePipe } from '@angular/common';
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
import { forkJoin, of, Subject } from 'rxjs';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  finalize,
  switchMap
} from 'rxjs/operators';

import { AuthService } from '../../../core/auth/auth.service';
import { ActivoService } from '../../activos/activo.service';
import { ActivoResponse } from '../../activos/activo.models';
import { EmpleadoService } from '../../empleados/empleado.service';
import { EmpleadoResponse } from '../../empleados/empleado.models';

import { AsistenciaService } from '../asistencia.service';
import { AsistenciaCatalogosService } from '../asistencia-catalogos.service';
import { HistorialAsistenciaService } from '../historial-asistencia.service';
import { ActividadAsistenciaService } from '../actividad-asistencia.service';
import { TecnicoAsistenciaService } from '../tecnico-asistencia.service';


import {
  ActividadAsistenciaResponse,
  AsistenciaCatalogoOption,
  AsistenciaRequest,
  AsistenciaResponse,
  AsistenciaUpdateRequest,
  CambioEstadoAsistenciaRequest,
  HistorialEstadoAsistenciaResponse,
  TecnicoOption
} from '../asistencia.models';

@Component({
  selector: 'app-asistencias',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './asistencias.html',
  styleUrl: './asistencias.css'
})
export class AsistenciasComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly asistenciaService = inject(AsistenciaService);
  private readonly catalogosService = inject(AsistenciaCatalogosService);
  private readonly historialService = inject(HistorialAsistenciaService);
  private readonly actividadService = inject(ActividadAsistenciaService);
  private readonly activoService = inject(ActivoService);
  private readonly empleadoService = inject(EmpleadoService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly tecnicoService = inject(TecnicoAsistenciaService);
  private readonly busquedaEmpleado$ = new Subject<string>();
  private readonly busquedaActivo$ = new Subject<string>();


  tecnicosDisponibles: TecnicoOption[] = [];

  asistencias: AsistenciaResponse[] = [];
  seleccionada: AsistenciaResponse | null = null;

  activosDisponibles: ActivoResponse[] = [];
  empleadosDisponibles: EmpleadoResponse[] = [];
  tipos: AsistenciaCatalogoOption[] = [];
  estados: AsistenciaCatalogoOption[] = [];

  historial: HistorialEstadoAsistenciaResponse[] = [];
  actividades: ActividadAsistenciaResponse[] = [];

  filtro = '';
  cargando = false;
  cargandoDetalle = false;
  guardando = false;

  mostrarCrear = false;
  mostrarEditar = false;
  actividadEditandoId: number | null = null;

  mensaje = '';
  error = '';

  empleadoSeleccionado: EmpleadoResponse | null = null;
  busquedaEmpleado = '';
  mostrarOpcionesEmpleado = false;
  buscandoEmpleados = false;

  activoSeleccionado: ActivoResponse | null = null;
  busquedaActivo = '';
  mostrarOpcionesActivo = false;
  buscandoActivos = false;

  private readonly transiciones: Record<string, string[]> = {
    'RECIBIDO': ['EN DIAGNOSTICO'],
    'EN DIAGNOSTICO': [
      'EN MANTENIMIENTO',
      'ESPERANDO REPUESTO'
    ],
    'EN MANTENIMIENTO': [
      'ESPERANDO REPUESTO',
      'PARA ENTREGAR'
    ],
    'ESPERANDO REPUESTO': ['EN MANTENIMIENTO'],
    'PARA ENTREGAR': ['ENTREGADO'],
    'ENTREGADO': []
  };

  readonly crearForm = this.fb.group({
    numeroAsistencia: [
      '',
      [Validators.required, Validators.maxLength(30)]
    ],
    activoId: [
      null as number | null,
      Validators.required
    ],
    empleadoId: [
      null as number | null,
      Validators.required
    ],
    tecnicoId: [
      null as number | null,
      Validators.required
    ],
    tipoAsistenciaId: [
      null as number | null,
      Validators.required
    ],
    problemaReportado: [''],
    observaciones: ['']
  });

  readonly editarForm = this.fb.group({
    tecnicoId: [
      null as number | null,
      Validators.required
    ],
    tipoAsistenciaId: [
      null as number | null,
      Validators.required
    ],
    problemaReportado: [''],
    diagnostico: [''],
    trabajoRealizado: [''],
    observaciones: ['']
  });

  readonly estadoForm = this.fb.group({
    estadoAsistenciaId: [
      null as number | null,
      Validators.required
    ],
    comentario: ['']
  });

  readonly actividadForm = this.fb.group({
    descripcion: [
      '',
      [Validators.required, Validators.maxLength(255)]
    ],
    resultado: ['', Validators.maxLength(255)],
    observacion: ['', Validators.maxLength(255)]
  });

  ngOnInit(): void {
    this.configurarBusquedaEmpleados();
    this.configurarBusquedaActivos();
    this.cargarDatos();
  }

  get esAdmin(): boolean {
    return this.authService.tieneRol('ADMIN');
  }

  get puedeTrabajar(): boolean {
    return this.authService.tieneRol('ADMIN', 'TECNICO');
  }

  get asistenciasFiltradas(): AsistenciaResponse[] {
    const termino = this.filtro.trim().toLowerCase();

    if (!termino) {
      return this.asistencias;
    }

    return this.asistencias.filter(asistencia =>
      [
        asistencia.numeroAsistencia,
        asistencia.codigoActivo,
        asistencia.activoNombre,
        asistencia.empleadoNombre,
        asistencia.tecnicoNombre,
        asistencia.tipoAsistenciaNombre,
        asistencia.estadoAsistenciaNombre
      ].some(valor =>
        (valor ?? '').toLowerCase().includes(termino)
      )
    );
  }

  get estadosSiguientes(): AsistenciaCatalogoOption[] {
    if (!this.seleccionada) {
      return [];
    }

    const permitidos =
      this.transiciones[
      this.seleccionada.estadoAsistenciaNombre
      ] ?? [];

    return this.estados.filter(estado =>
      permitidos.includes(estado.nombre)
    );
  }

  get puedeCambiarEstado(): boolean {
    return this.puedeTrabajar
      && this.seleccionada !== null
      && this.seleccionada.fechaCierre === null;
  }

  get puedeEditarAsistencia(): boolean {
    return this.esAdmin
      && this.seleccionada !== null
      && this.seleccionada.fechaCierre === null;
  }

  get puedeRegistrarActividad(): boolean {
    return this.puedeTrabajar
      && this.seleccionada !== null
      && this.seleccionada.fechaCierre === null;
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = '';

    forkJoin({
      asistencias: this.asistenciaService.listar(),
      tipos: this.catalogosService.listarTipos(),
      estados: this.catalogosService.listarEstados()
    }).subscribe({
      next: resultado => {
        this.asistencias = [...resultado.asistencias].sort(
          (a, b) =>
            new Date(b.fechaIngreso).getTime()
            - new Date(a.fechaIngreso).getTime()
        );

        this.tipos = resultado.tipos.filter(
          item => item.activo !== false
        );

        this.estados = resultado.estados.filter(
          item => item.activo !== false
        );

        this.cargando = false;
        this.cdr.markForCheck();

        if (this.esAdmin) {
          this.cargarOpcionesAdmin();
        }
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error al cargar asistencias:', error);

        this.error =
          'No se pudieron cargar las asistencias o sus catálogos.';

        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  private cargarOpcionesAdmin(): void {

    forkJoin({
      activos: this.activoService.listar(),
      empleados: this.empleadoService.listarRecientes(),
      tecnicos: this.tecnicoService.listar()
    }).subscribe({
      next: resultado => {

        this.activosDisponibles = resultado.activos.filter(
          activo =>
            activo.activo &&
            activo.estadoActivoNombre === 'ACTIVO'
        );

        this.empleadosDisponibles = resultado.empleados.filter(
          empleado => empleado.activo
        );

        this.tecnicosDisponibles = resultado.tecnicos;

        this.cdr.markForCheck();
      },

      error: error => {
        console.error(
          'Error al cargar los datos para la asistencia:',
          error
        );

        this.error =
          'No se pudieron cargar los equipos, empleados o técnicos disponibles.';

        this.cdr.markForCheck();
      }
    });
  }

  cambiarFiltro(event: Event): void {
    this.filtro = (event.target as HTMLInputElement).value;
  }

  abrirCrear(): void {
    if (!this.esAdmin) {
      return;
    }

    this.error = '';
    this.mensaje = '';

    this.crearForm.reset({
      numeroAsistencia: '',
      activoId: null,
      empleadoId: null,
      tecnicoId: null,
      tipoAsistenciaId: null,
      problemaReportado: '',
      observaciones: ''
    });

    this.mostrarCrear = true;
    this.mostrarEditar = false;

    this.empleadoSeleccionado = null;
    this.busquedaEmpleado = '';
    this.mostrarOpcionesEmpleado = false;
    this.busquedaEmpleado$.next('');

    this.activoSeleccionado = null;
    this.busquedaActivo = '';
    this.mostrarOpcionesActivo = false;
    this.busquedaActivo$.next('');
  }

  cancelarCrear(): void {
    this.mostrarCrear = false;
    this.crearForm.reset();

    this.empleadoSeleccionado = null;
    this.busquedaEmpleado = '';
    this.mostrarOpcionesEmpleado = false;
    this.busquedaEmpleado$.next('');

    this.activoSeleccionado = null;
    this.busquedaActivo = '';
    this.mostrarOpcionesActivo = false;
    this.busquedaActivo$.next('');
  }

  crearAsistencia(): void {
    if (!this.esAdmin || this.guardando) {
      return;
    }

    this.error = '';
    this.mensaje = '';

    if (this.crearForm.invalid) {
      this.crearForm.markAllAsTouched();
      return;
    }

    const valor = this.crearForm.getRawValue();

    if (
      valor.activoId === null
      || valor.empleadoId === null
      || valor.tecnicoId === null
      || valor.tipoAsistenciaId === null
    ) {
      this.error = 'Completa todos los campos obligatorios.';
      return;
    }

    const request: AsistenciaRequest = {
      numeroAsistencia:
        valor.numeroAsistencia?.trim() ?? '',
      activoId: valor.activoId,
      empleadoId: valor.empleadoId,
      tecnicoId: valor.tecnicoId,
      tipoAsistenciaId: valor.tipoAsistenciaId,
      problemaReportado:
        valor.problemaReportado?.trim() || null,
      diagnostico: null,
      trabajoRealizado: null,
      observaciones:
        valor.observaciones?.trim() || null
    };

    this.guardando = true;

    this.asistenciaService.crear(request).subscribe({
      next: creada => {
        this.guardando = false;
        this.cancelarCrear();

        this.cargarLista();
        this.verDetalle(creada);

        this.mensaje =
          `La asistencia ${creada.numeroAsistencia} fue registrada.`;

        this.cdr.markForCheck();
      },
      error: error => {
        this.guardando = false;
        this.error = this.mensajeError(error);

        this.cdr.markForCheck();
      }
    });
  }

  verDetalle(asistencia: AsistenciaResponse): void {
    this.seleccionada = asistencia;
    this.historial = [];
    this.actividades = [];
    this.mostrarEditar = false;
    this.actividadEditandoId = null;
    this.error = '';
    this.mensaje = '';
    this.estadoForm.reset({
      estadoAsistenciaId: null,
      comentario: ''
    });
    this.actividadForm.reset({
      descripcion: '',
      resultado: '',
      observacion: ''
    });

    this.cargandoDetalle = true;

    forkJoin({
      historial: this.historialService.listarPorAsistencia(
        asistencia.id
      ),
      actividades: this.actividadService.listarPorAsistencia(
        asistencia.id
      )
    }).subscribe({
      next: resultado => {
        this.historial = [...resultado.historial].sort(
          (a, b) =>
            new Date(b.fechaCambio).getTime()
            - new Date(a.fechaCambio).getTime()
        );

        this.actividades = [...resultado.actividades].sort(
          (a, b) =>
            new Date(b.fechaActividad).getTime()
            - new Date(a.fechaActividad).getTime()
        );

        this.cargandoDetalle = false;
        this.cdr.markForCheck();
      },
      error: error => {
        console.error('Error al cargar detalle:', error);

        this.error =
          'No se pudo cargar el historial o las actividades de esta asistencia.';

        this.cargandoDetalle = false;
        this.cdr.markForCheck();
      }
    });
  }

  abrirEditar(): void {
    if (!this.puedeEditarAsistencia || !this.seleccionada) {
      return;
    }

    const asistencia = this.seleccionada;

    this.editarForm.reset({
      tecnicoId: asistencia.tecnicoId,
      tipoAsistenciaId: asistencia.tipoAsistenciaId,
      problemaReportado: asistencia.problemaReportado ?? '',
      diagnostico: asistencia.diagnostico ?? '',
      trabajoRealizado: asistencia.trabajoRealizado ?? '',
      observaciones: asistencia.observaciones ?? ''
    });

    this.mostrarEditar = true;
    this.error = '';
    this.mensaje = '';
  }

  guardarEdicion(): void {
    if (
      !this.puedeEditarAsistencia
      || !this.seleccionada
      || this.guardando
    ) {
      return;
    }

    this.error = '';

    if (this.editarForm.invalid) {
      this.editarForm.markAllAsTouched();
      return;
    }

    const valor = this.editarForm.getRawValue();

    if (
      valor.tecnicoId === null
      || valor.tipoAsistenciaId === null
    ) {
      this.error =
        'Selecciona el técnico y el tipo de asistencia.';
      return;
    }

    const request: AsistenciaUpdateRequest = {
      tecnicoId: valor.tecnicoId,
      tipoAsistenciaId: valor.tipoAsistenciaId,
      problemaReportado:
        valor.problemaReportado?.trim() || null,
      diagnostico: valor.diagnostico?.trim() || null,
      trabajoRealizado:
        valor.trabajoRealizado?.trim() || null,
      observaciones: valor.observaciones?.trim() || null
    };

    const id = this.seleccionada.id;
    this.guardando = true;

    this.asistenciaService.actualizar(id, request).subscribe({
      next: actualizada => {
        this.guardando = false;
        this.mostrarEditar = false;

        this.cargarLista();
        this.verDetalle(actualizada);

        this.mensaje =
          'La asistencia se actualizó correctamente.';

        this.cdr.markForCheck();
      },
      error: error => {
        this.guardando = false;
        this.error = this.mensajeError(error);
        this.cdr.markForCheck();
      }
    });
  }

  cancelarEdicion(): void {
    this.mostrarEditar = false;
    this.editarForm.reset();
  }

  guardarCambioEstado(): void {
    if (
      !this.puedeCambiarEstado
      || !this.seleccionada
      || this.guardando
    ) {
      return;
    }

    if (this.estadoForm.invalid) {
      this.estadoForm.markAllAsTouched();
      return;
    }

    const valor = this.estadoForm.getRawValue();

    if (valor.estadoAsistenciaId === null) {
      return;
    }

    const request: CambioEstadoAsistenciaRequest = {
      estadoAsistenciaId: valor.estadoAsistenciaId,
      comentario: valor.comentario?.trim() || null
    };

    const id = this.seleccionada.id;
    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    this.asistenciaService.cambiarEstado(id, request).subscribe({
      next: actualizada => {
        this.guardando = false;

        this.cargarLista();
        this.verDetalle(actualizada);

        this.mensaje =
          `La asistencia cambió al estado ${actualizada.estadoAsistenciaNombre}.`;

        this.cdr.markForCheck();
      },
      error: error => {
        this.guardando = false;
        this.error = this.mensajeError(error);
        this.cdr.markForCheck();
      }
    });
  }

  editarActividad(actividad: ActividadAsistenciaResponse): void {
    if (!this.puedeRegistrarActividad) {
      return;
    }

    this.actividadEditandoId = actividad.id;

    this.actividadForm.reset({
      descripcion: actividad.descripcion,
      resultado: actividad.resultado ?? '',
      observacion: actividad.observacion ?? ''
    });

    this.error = '';
    this.mensaje = '';
  }

  cancelarEdicionActividad(): void {
    this.actividadEditandoId = null;

    this.actividadForm.reset({
      descripcion: '',
      resultado: '',
      observacion: ''
    });
  }

  guardarActividad(): void {
    if (
      !this.puedeRegistrarActividad ||
      !this.seleccionada ||
      this.guardando
    ) {
      return;
    }

    this.error = '';
    this.mensaje = '';

    if (this.actividadForm.invalid) {
      this.actividadForm.markAllAsTouched();
      return;
    }

    // Guardar estos valores antes de limpiar el formulario.
    const asistenciaActual = this.seleccionada;
    const actividadId = this.actividadEditandoId;
    const estabaEditando = actividadId !== null;

    const valor = this.actividadForm.getRawValue();

    const request = {
      asistenciaId: asistenciaActual.id,
      descripcion: valor.descripcion?.trim() ?? '',
      resultado: valor.resultado?.trim() || null,
      observacion: valor.observacion?.trim() || null
    };

    this.guardando = true;

    const operacion = actividadId !== null
      ? this.actividadService.actualizar(actividadId, request)
      : this.actividadService.crear(request);

    operacion.subscribe({
      next: () => {
        this.guardando = false;

        // Limpiar el formulario de actividad.
        this.cancelarEdicionActividad();

        // Actualizar la lista y recargar el detalle.
        this.cargarLista();
        this.verDetalle(asistenciaActual);

        // Determinar el mensaje con el valor original.
        this.mensaje = estabaEditando
          ? 'La actividad se actualizó correctamente.'
          : 'La actividad se registró correctamente.';

        this.cdr.markForCheck();
      },

      error: error => {
        this.guardando = false;
        this.error = this.mensajeError(error);
        this.cdr.markForCheck();
      }
    });
  }

  private cargarLista(): void {
    this.asistenciaService.listar().subscribe({
      next: resultado => {
        this.asistencias = [...resultado].sort(
          (a, b) =>
            new Date(b.fechaIngreso).getTime()
            - new Date(a.fechaIngreso).getTime()
        );

        this.cdr.markForCheck();
      },
      error: error => {
        console.error('Error al actualizar la lista:', error);

        this.error =
          'La operación se completó, pero no se pudo actualizar la lista.';

        this.cdr.markForCheck();
      }
    });
  }

  private configurarBusquedaEmpleados(): void {

    this.busquedaEmpleado$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),

        switchMap(termino => {
          const texto = termino.trim();

          this.buscandoEmpleados = true;
          this.cdr.markForCheck();

          const peticion = texto.length === 0
            ? this.empleadoService.listarRecientes()
            : texto.length < 2
              ? of([] as EmpleadoResponse[])
              : this.empleadoService.buscarActivos(texto);

          return peticion.pipe(
            catchError(error => {
              console.error(
                'Error al buscar empleados:',
                error
              );

              this.error =
                'No se pudo realizar la búsqueda de empleados.';

              return of([] as EmpleadoResponse[]);
            }),

            finalize(() => {
              this.buscandoEmpleados = false;
              this.cdr.markForCheck();
            })
          );
        })
      )
      .subscribe(empleados => {
        this.empleadosDisponibles = empleados;
        this.cdr.markForCheck();
      });
  }

  buscarEmpleado(event: Event): void {

    const input = event.target as HTMLInputElement;

    this.busquedaEmpleado = input.value;

    // Al empezar una búsqueda, es necesario volver a seleccionar
    // un empleado de los resultados.
    this.empleadoSeleccionado = null;
    this.crearForm.controls.empleadoId.setValue(null);

    this.mostrarOpcionesEmpleado = true;

    this.busquedaEmpleado$.next(this.busquedaEmpleado);
  }

  abrirOpcionesEmpleado(): void {

    this.mostrarOpcionesEmpleado = true;

    // Al abrir el campo se muestran los cuatro más recientes.
    // Si ya hay una selección, el usuario todavía puede cambiarla.
    this.busquedaEmpleado$.next(
      this.empleadoSeleccionado
        ? ''
        : this.busquedaEmpleado
    );
  }

  private configurarBusquedaActivos(): void {
    this.busquedaActivo$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(termino => {
          const texto = termino.trim();

          this.buscandoActivos = true;
          this.cdr.markForCheck();

          const peticion = texto.length === 0
            ? this.activoService.listarRecientes()
            : texto.length < 2
              ? of([] as ActivoResponse[])
              : this.activoService.buscarActivos(texto);

          return peticion.pipe(
            catchError(error => {
              console.error('Error buscando equipos:', error);
              this.error = 'No se pudo realizar la búsqueda de equipos.';
              return of([] as ActivoResponse[]);
            }),
            finalize(() => {
              this.buscandoActivos = false;
              this.cdr.markForCheck();
            })
          );
        })
      )
      .subscribe(activos => {
        this.activosDisponibles = activos;
        this.cdr.markForCheck();
      });
  }

  buscarActivo(event: Event): void {
    const input = event.target as HTMLInputElement;

    this.busquedaActivo = input.value;
    this.activoSeleccionado = null;
    this.crearForm.controls.activoId.setValue(null);
    this.mostrarOpcionesActivo = true;

    this.busquedaActivo$.next(this.busquedaActivo);
  }

  abrirOpcionesActivo(): void {
    this.mostrarOpcionesActivo = true;

    this.busquedaActivo$.next(
      this.activoSeleccionado ? '' : this.busquedaActivo
    );
  }

  cerrarOpcionesActivo(): void {
    this.mostrarOpcionesActivo = false;
  }

  seleccionarActivo(activo: ActivoResponse): void {
    this.activoSeleccionado = activo;
    this.busquedaActivo =
      `${activo.nombre} · ${activo.codigoActivo}`;

    this.crearForm.controls.activoId.setValue(activo.id);
    this.crearForm.controls.activoId.markAsTouched();

    this.mostrarOpcionesActivo = false;
    this.error = '';

    this.cdr.markForCheck();
  }

  cerrarOpcionesEmpleado(): void {
    this.mostrarOpcionesEmpleado = false;
  }

  seleccionarEmpleado(empleado: EmpleadoResponse): void {

    this.empleadoSeleccionado = empleado;

    this.busquedaEmpleado =
      `${empleado.nombres} ${empleado.apellidos}`;

    this.crearForm.controls.empleadoId.setValue(
      empleado.id
    );

    this.crearForm.controls.empleadoId.markAsTouched();

    this.mostrarOpcionesEmpleado = false;
    this.error = '';

    this.cdr.markForCheck();
  }

  private mensajeError(error: HttpErrorResponse): string {
    if (error.status === 400) {
      return error.error?.message
        ?? 'Verifica los datos ingresados.';
    }

    if (error.status === 401) {
      return 'Tu sesión expiró. Inicia sesión nuevamente.';
    }

    if (error.status === 403) {
      return 'No tienes permisos para realizar esta operación.';
    }

    if (error.status === 404) {
      return error.error?.message
        ?? 'No se encontró el registro solicitado.';
    }

    if (error.status === 409) {
      return error.error?.message
        ?? 'La operación entra en conflicto con las reglas del sistema.';
    }

    return 'Ocurrió un error. Comprueba la conexión con el servidor.';
  }
}