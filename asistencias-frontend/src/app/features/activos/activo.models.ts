export interface ActivoRequest{
    nombre: string;
    modelo: string | null;
    serie: string | null;
    codigoActivo: string;
    tipoActivoId: number;
    marcaId: number;
    estadoActivoId: number;
    activo: boolean;
}

export interface ActivoResponse{
    id: number;
    nombre: string;
    modelo: string | null;
    serie: string | null;
    codigoActivo: string;
    activo: boolean;

    tipoActivoId: number;
    tipoActivoNombre: string;

    marcaId: number;
    marcaNombre: string;

    estadoActivoId: number;
    estadoActivoNombre: string;
}

export interface CatalogoOption {
  id: number;
  nombre: string;
  descripcion?: string;
  activo?: boolean;
}