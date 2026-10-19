export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  tokenType: string;
  accessToken: string;
  expiresIn: number;

  usuarioId: number;
  username: string;
  empleadoId: number | null;

  roles: string[];
}