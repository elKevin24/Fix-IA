export interface Ticket {
  id: number;
  numeroTicket: string;
  tipoEquipo: string;
  marca: string;
  modelo?: string;
  numeroSerie?: string;
  fallaReportada: string;
  accesorios?: string;
  estado: EstadoTicket;
  diagnostico?: string;
  presupuestoManoObra?: number;
  presupuestoPiezas?: number;
  presupuestoTotal?: number;
  tiempoEstimadoDias?: number;
  fechaPresupuesto?: string;
  fechaRespuestaCliente?: string;
  motivoRechazo?: string;
  motivoCancelacion?: string;
  observacionesReparacion?: string;
  fechaInicioReparacion?: string;
  fechaFinReparacion?: string;
  resultadoPruebas?: string;
  fechaEntrega?: string;
  observacionesEntrega?: string;
  cliente: ClienteBasico;
  tecnicoAsignado?: UsuarioBasico;
  usuarioIngreso?: UsuarioBasico;
  piezasUtilizadas?: TicketPieza[];
  createdAt: string;
  updatedAt: string;
}

export enum EstadoTicket {
  INGRESADO = 'INGRESADO',
  ASIGNADO = 'ASIGNADO',
  EN_DIAGNOSTICO = 'EN_DIAGNOSTICO',
  PRESUPUESTADO = 'PRESUPUESTADO',
  APROBADO = 'APROBADO',
  RECHAZADO = 'RECHAZADO',
  EN_REPARACION = 'EN_REPARACION',
  EN_PRUEBAS = 'EN_PRUEBAS',
  LISTO_ENTREGA = 'LISTO_ENTREGA',
  ENTREGADO = 'ENTREGADO',
  CANCELADO = 'CANCELADO'
}

export interface ClienteBasico {
  id: number;
  nombreCompleto: string;
  email: string;
  telefono: string;
}

export interface UsuarioBasico {
  id: number;
  nombreCompleto: string;
  rol: string;
}

export interface TicketPieza {
  id: number;
  piezaId: number;
  piezaCodigo: string;
  piezaNombre: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  stockDescontado: boolean;
}

export interface CrearTicketDTO {
  clienteId: number;
  tipoEquipo: string;
  marca: string;
  modelo?: string;
  numeroSerie?: string;
  fallaReportada: string;
  accesorios?: string;
}

export interface AsignarTecnicoDTO {
  tecnicoId: number;
}

export interface RegistrarDiagnosticoDTO {
  diagnostico: string;
  presupuestoManoObra: number;
  presupuestoPiezas?: number;
  tiempoEstimadoDias: number;
}

export interface RechazarPresupuestoDTO {
  motivoRechazo: string;
}

export interface IniciarReparacionDTO {
  observaciones?: string;
}

export interface RegistrarPruebasDTO {
  resultadoPruebas: string;
  exitoso: boolean;
}

export interface RegistrarEntregaDTO {
  observacionesEntrega?: string;
}

export interface CancelarTicketDTO {
  motivoCancelacion: string;
}
