export interface Cliente {
  id: number;
  nombre: string;
  apellido: string;
  nombreCompleto: string;
  email: string;
  telefono: string;
  direccion?: string;
  totalTickets: number;
  createdAt: string;
  updatedAt: string;
}

export interface CrearClienteDTO {
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  direccion?: string;
}

export interface ActualizarClienteDTO {
  nombre?: string;
  apellido?: string;
  email?: string;
  telefono?: string;
  direccion?: string;
}
