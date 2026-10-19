export interface EmpleadoRequest {
  codigoEmpleado: string;
  nombres: string;
  apellidos: string;
  documento: string;
  cargo: string | null;
  telefono: string | null;
  correo: string | null;
  unidadId: number;
}

export interface EmpleadoResponse {
  id: number;
  codigoEmpleado: string;
  nombres: string;
  apellidos: string;
  documento: string;
  cargo: string | null;
  telefono: string | null;
  correo: string | null;
  activo: boolean;
  unidadId: number;
  unidadNombre: string;
}

export interface UnidadOption {
  id: number;
  codigo: string;
  nombre: string;
  descripcion?: string | null;
  activo: boolean;
}