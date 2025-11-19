export interface Usuario {
  id: number;
  username: string;
  nombre: string;
  apellido: string;
  email: string;
  rol: Rol;
  activo: boolean;
  createdAt: string;
  updatedAt: string;
}

export enum Rol {
  ADMINISTRADOR = 'ADMINISTRADOR',
  RECEPCIONISTA = 'RECEPCIONISTA',
  TECNICO = 'TECNICO'
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tipo: string;
  usuario: Usuario;
}

export interface CrearUsuarioDTO {
  username: string;
  password: string;
  nombre: string;
  apellido: string;
  email: string;
  rol: Rol;
}

export interface ActualizarUsuarioDTO {
  nombre?: string;
  apellido?: string;
  email?: string;
  rol?: Rol;
  activo?: boolean;
}
