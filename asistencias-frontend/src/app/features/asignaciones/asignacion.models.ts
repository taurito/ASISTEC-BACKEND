export interface AsignacionActivoRequest {
  activoId: number;
  empleadoId: number;
  observacion: string | null;
}

export interface AsignacionActivoResponse {
  id: number;

  activoId: number;
  activoNombre: string;
  codigoActivo: string;

  empleadoId: number;
  empleadoNombre: string;

  fechaAsignacion: string;
  fechaDevolucion: string | null;

  observacion: string | null;
  vigente: boolean;

  createdAt: string;
  updatedAt: string;
}