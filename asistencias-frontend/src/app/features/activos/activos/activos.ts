import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { ActivoService } from '../activo.service';
import { CatalogosService } from '../catalogos.service';
import {
  ActivoRequest,
  ActivoResponse,
  CatalogoOption
} from '../activo.models';

@Component({
  selector: 'app-activos',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './activos.html',
  styleUrl: './activos.css'
})
export class Activos implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly activoService = inject(ActivoService);
  private readonly catalogosService = inject(CatalogosService);
  private readonly authService = inject(AuthService);
  private readonly cdr = inject(ChangeDetectorRef);

  activos: ActivoResponse[] = [];
  tipos: CatalogoOption[] = [];
  marcas: CatalogoOption[] = [];
  estados: CatalogoOption[] = [];

  filtro = '';
  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  editando = false;
  idEditando: number | null = null;

  mensaje = '';
  error = '';

  readonly form = this.fb.group({
    nombre: [
      '',
      [Validators.required, Validators.maxLength(100)]
    ],
    modelo: ['', Validators.maxLength(100)],
    serie: ['', Validators.maxLength(100)],
    codigoActivo: [
      '',
      [Validators.required, Validators.maxLength(50)]
    ],
    tipoActivoId: [
      null as number | null,
      Validators.required
    ],
    marcaId: [
      null as number | null,
      Validators.required
    ],
    estadoActivoId: [
      null as number | null,
      Validators.required
    ],
    activo: [true, Validators.required]
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  get esAdmin(): boolean {
    return this.authService.tieneRol('ADMIN');
  }

  get activosFiltrados(): ActivoResponse[] {
    const termino = this.filtro.trim().toLowerCase();

    if (!termino) {
      return this.activos;
    }

    return this.activos.filter(activo =>
      [
        activo.nombre,
        activo.codigoActivo,
        activo.serie,
        activo.modelo,
        activo.tipoActivoNombre,
        activo.marcaNombre,
        activo.estadoActivoNombre
      ].some(valor =>
        (valor ?? '').toLowerCase().includes(termino)
      )
    );
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = '';

    forkJoin({
      activos: this.activoService.listar(),
      tipos: this.catalogosService.listarTiposActivo(),
      marcas: this.catalogosService.listarMarcas(),
      estados: this.catalogosService.listarEstadosActivo()
    }).subscribe({
      next: resultado => {
        this.activos = resultado.activos;
        this.tipos = resultado.tipos.filter(
          item => item.activo !== false
        );
        this.marcas = resultado.marcas.filter(
          item => item.activo !== false
        );
        this.estados = resultado.estados.filter(
          item => item.activo !== false
        );
        this.cargando = false;

        // Notificar a Angular que debe actualizar la vista.
        this.cdr.markForCheck();
      },
      error: error => {
      console.error('Error al cargar el inventario:', error);

      this.error =
        'No se pudieron cargar los activos o sus catálogos. '
        + 'Comprueba la conexión con el servidor.';

      this.cargando = false;

      this.cdr.markForCheck();
    }
    });
  }

  abrirCrear(): void {
    if (!this.esAdmin) {
      return;
    }

    const estadoActivo = this.estados.find(
      estado => estado.nombre === 'ACTIVO'
    );

    if (!estadoActivo) {
      this.error =
        'No se encontró el estado ACTIVO en el catálogo.';
      return;
    }

    this.error = '';
    this.mensaje = '';
    this.editando = false;
    this.idEditando = null;

    this.form.enable();
    this.form.reset({
      nombre: '',
      modelo: '',
      serie: '',
      codigoActivo: '',
      tipoActivoId: null,
      marcaId: null,
      estadoActivoId: estadoActivo.id,
      activo: true
    });

    this.mostrarFormulario = true;
  }

  editar(activo: ActivoResponse): void {
    if (!this.esAdmin) {
      return;
    }

    this.error = '';
    this.mensaje = '';
    this.editando = true;
    this.idEditando = activo.id;

    this.form.enable();
    this.form.reset({
      nombre: activo.nombre,
      modelo: activo.modelo ?? '',
      serie: activo.serie ?? '',
      codigoActivo: activo.codigoActivo,
      tipoActivoId: activo.tipoActivoId,
      marcaId: activo.marcaId,
      estadoActivoId: activo.estadoActivoId,
      activo: activo.activo
    });

    // El estado del equipo se gestiona a través de
    // las reglas de asistencia técnica.
    this.form.controls.estadoActivoId.disable();

    this.mostrarFormulario = true;
  }

  cancelarFormulario(): void {
    this.mostrarFormulario = false;
    this.form.enable();
    this.form.reset();
    this.editando = false;
    this.idEditando = null;
    this.error = '';
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
      valor.tipoActivoId === null ||
      valor.marcaId === null ||
      valor.estadoActivoId === null
    ) {
      this.error = 'Completa todos los catálogos obligatorios.';
      return;
    }

    const request: ActivoRequest = {
      nombre: valor.nombre?.trim() ?? '',
      modelo: valor.modelo?.trim() || null,
      serie: valor.serie?.trim() || null,
      codigoActivo: valor.codigoActivo?.trim() ?? '',
      tipoActivoId: valor.tipoActivoId,
      marcaId: valor.marcaId,
      estadoActivoId: valor.estadoActivoId,
      activo: valor.activo ?? true
    };

    this.guardando = true;

    const operacion = this.editando && this.idEditando !== null
      ? this.activoService.actualizar(this.idEditando, request)
      : this.activoService.crear(request);

    operacion.subscribe({
      next: () => {
        this.guardando = false;
        this.mensaje = this.editando
          ? 'Activo actualizado correctamente.'
          : 'Activo registrado correctamente.';

        this.cancelarFormulario();
        this.cargarListaActivos();
      },
      error: (error: HttpErrorResponse) => {
        this.guardando = false;

        if (error.status === 409) {
          this.error =
            error.error?.message
            ?? 'El registro entra en conflicto con los datos existentes.';
        } else if (error.status === 400) {
          this.error =
            error.error?.message
            ?? 'Verifica los datos ingresados.';
        } else if (error.status === 401) {
          this.error = 'Tu sesión no es válida. Inicia sesión nuevamente.';
        } else if (error.status === 403) {
          this.error = 'No tienes permiso para realizar esta operación.';
        } else {
          this.error =
            'No se pudo guardar el activo. Comprueba los datos e inténtalo nuevamente.';
        }
      }
    });
  }

  cambiarFiltro(event: Event): void {
  this.filtro = (event.target as HTMLInputElement).value;
}

  private cargarListaActivos(): void {
  this.activoService.listar().subscribe({
    next: activos => {
      this.activos = activos;
      this.cdr.markForCheck();
    },

    error: error => {
      console.error('Error al actualizar el inventario:', error);

      this.error =
        'El activo se guardó, pero no se pudo actualizar la lista.';

      this.cdr.markForCheck();
    }
  });
}
}