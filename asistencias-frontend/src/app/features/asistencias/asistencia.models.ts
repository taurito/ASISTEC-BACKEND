export interface AsistenciaRequest {
  numeroAsistencia: string;
  activoId: number;
  empleadoId: number;
  tecnicoId: number;
  tipoAsistenciaId: number;
  problemaReportado: string | null;
  diagnostico: string | null;
  trabajoRealizado: string | null;
  observaciones: string | null;
}

export interface AsistenciaUpdateRequest {
  tecnicoId: number;
  tipoAsistenciaId: number;
  problemaReportado: string | null;
  diagnostico: string | null;
  trabajoRealizado: string | null;
  observaciones: string | null;
}

export interface CambioEstadoAsistenciaRequest {
  estadoAsistenciaId: number;
  comentario: string | null;
}

export interface AsistenciaResponse {
  id: number;
  numeroAsistencia: string;

  activoId: number;
  activoNombre: string;
  codigoActivo: string;

  empleadoId: number;
  empleadoNombre: string;

  tecnicoId: number;
  tecnicoNombre: string;

  tipoAsistenciaId: number;
  tipoAsistenciaNombre: string;

  estadoAsistenciaId: number;
  estadoAsistenciaNombre: string;

  fechaIngreso: string;
  fechaCierre: string | null;

  problemaReportado: string | null;
  diagnostico: string | null;
  trabajoRealizado: string | null;
  observaciones: string | null;

  createdAt: string;
  updatedAt: string;
}

export interface HistorialEstadoAsistenciaResponse {
  id: number;
  asistenciaId: number;
  numeroAsistencia: string;
  estadoAsistenciaId: number;
  estadoAsistenciaNombre: string;
  fechaCambio: string;
  comentario: string | null;
  createdAt: string;
}

export interface ActividadAsistenciaRequest {
  asistenciaId: number;
  descripcion: string;
  resultado: string | null;
  observacion: string | null;
}

export interface ActividadAsistenciaResponse {
  id: number;
  asistenciaId: number;
  numeroAsistencia: string;
  descripcion: string;
  resultado: string | null;
  fechaActividad: string;
  observacion: string | null;
  createdAt: string;
}

export interface AsistenciaCatalogoOption {
  id: number;
  nombre: string;
  descripcion?: string | null;
  activo?: boolean;
}