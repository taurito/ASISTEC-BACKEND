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
import { EmpleadoService } from '../empleado.service';
import { UnidadService } from '../unidad.service';
import {
  EmpleadoRequest,
  EmpleadoResponse,
  UnidadOption
} from '../empleado.models';

@Component({
  selector: 'app-empleados',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './empleados.html',
  styleUrl: './empleados.css'
})
export class EmpleadosComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly empleadoService = inject(EmpleadoService);
  private readonly unidadService = inject(UnidadService);
  private readonly authService = inject(AuthService);
  private readonly cdr = inject(ChangeDetectorRef);

  empleados: EmpleadoResponse[] = [];
  unidades: UnidadOption[] = [];

  filtro = '';
  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  editando = false;
  idEditando: number | null = null;

  mensaje = '';
  error = '';

  readonly form = this.fb.group({
    codigoEmpleado: [
      '',
      [Validators.required, Validators.maxLength(30)]
    ],
    nombres: [
      '',
      [Validators.required, Validators.maxLength(100)]
    ],
    apellidos: [
      '',
      [Validators.required, Validators.maxLength(100)]
    ],
    documento: [
      '',
      [Validators.required, Validators.maxLength(30)]
    ],
    cargo: ['', Validators.maxLength(100)],
    telefono: ['', Validators.maxLength(30)],
    correo: ['', [
      Validators.email,
      Validators.maxLength(150)
    ]],
    unidadId: [
      null as number | null,
      Validators.required
    ]
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  get esAdmin(): boolean {
    return this.authService.tieneRol('ADMIN');
  }

  get empleadosFiltrados(): EmpleadoResponse[] {
    const termino = this.filtro.trim().toLowerCase();

    if (!termino) {
      return this.empleados;
    }

    return this.empleados.filter(empleado =>
      [
        empleado.codigoEmpleado,
        empleado.nombres,
        empleado.apellidos,
        empleado.documento,
        empleado.cargo,
        empleado.telefono,
        empleado.correo,
        empleado.unidadNombre
      ].some(valor =>
        (valor ?? '').toLowerCase().includes(termino)
      )
    );
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = '';

    forkJoin({
      empleados: this.empleadoService.listar(),
      unidades: this.unidadService.listar()
    }).subscribe({
      next: resultado => {
        this.empleados = resultado.empleados;

        this.unidades = resultado.unidades.filter(
          unidad => unidad.activo !== false
        );

        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: error => {
        console.error('Error al cargar empleados:', error);

        this.error =
          'No se pudieron cargar los empleados o las unidades. '
          + 'Comprueba tu conexión con el servidor.';

        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  cambiarFiltro(event: Event): void {
    this.filtro =
      (event.target as HTMLInputElement).value;
  }

  abrirCrear(): void {
    if (!this.esAdmin) {
      return;
    }

    this.mensaje = '';
    this.error = '';
    this.editando = false;
    this.idEditando = null;

    this.form.enable();

    this.form.reset({
      codigoEmpleado: '',
      nombres: '',
      apellidos: '',
      documento: '',
      cargo: '',
      telefono: '',
      correo: '',
      unidadId: null
    });

    this.mostrarFormulario = true;
  }

  editar(empleado: EmpleadoResponse): void {
    if (!this.esAdmin) {
      return;
    }

    this.mensaje = '';
    this.error = '';
    this.editando = true;
    this.idEditando = empleado.id;

    this.form.enable();

    this.form.reset({
      codigoEmpleado: empleado.codigoEmpleado,
      nombres: empleado.nombres,
      apellidos: empleado.apellidos,
      documento: empleado.documento,
      cargo: empleado.cargo ?? '',
      telefono: empleado.telefono ?? '',
      correo: empleado.correo ?? '',
      unidadId: empleado.unidadId
    });

    this.mostrarFormulario = true;
  }

  cancelarFormulario(): void {
    this.mostrarFormulario = false;
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

    if (valor.unidadId === null) {
      this.error = 'Debes seleccionar una unidad.';
      return;
    }

    const request: EmpleadoRequest = {
      codigoEmpleado: valor.codigoEmpleado?.trim() ?? '',
      nombres: valor.nombres?.trim() ?? '',
      apellidos: valor.apellidos?.trim() ?? '',
      documento: valor.documento?.trim() ?? '',
      cargo: valor.cargo?.trim() || null,
      telefono: valor.telefono?.trim() || null,
      correo: valor.correo?.trim() || null,
      unidadId: valor.unidadId
    };

    this.guardando = true;

    const operacion =
      this.editando && this.idEditando !== null
        ? this.empleadoService.actualizar(
            this.idEditando,
            request
          )
        : this.empleadoService.crear(request);

    operacion.subscribe({
      next: () => {
        this.guardando = false;

        this.mensaje = this.editando
          ? 'Empleado actualizado correctamente.'
          : 'Empleado registrado correctamente.';

        this.mostrarFormulario = false;
        this.form.reset();
        this.editando = false;
        this.idEditando = null;

        this.cargarLista();
        this.cdr.markForCheck();
      },
      error: (error: HttpErrorResponse) => {
        this.guardando = false;

        if (error.status === 400) {
          this.error =
            error.error?.message
            ?? 'Verifica los datos ingresados.';
        } else if (error.status === 401) {
          this.error =
            'Tu sesión no es válida. Inicia sesión nuevamente.';
        } else if (error.status === 403) {
          this.error =
            'No tienes permiso para realizar esta operación.';
        } else if (error.status === 409) {
          this.error =
            error.error?.message
            ?? 'El empleado entra en conflicto con un registro existente.';
        } else {
          this.error =
            'No se pudo guardar el empleado. Comprueba los datos.';
        }

        this.cdr.markForCheck();
      }
    });
  }

  private cargarLista(): void {
    this.empleadoService.listar().subscribe({
      next: empleados => {
        this.empleados = empleados;
        this.cdr.markForCheck();
      },
      error: error => {
        console.error('Error al actualizar empleados:', error);

        this.error =
          'El empleado se guardó, pero no se pudo actualizar la lista.';

        this.cdr.markForCheck();
      }
    });
  }
}