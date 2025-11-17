import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  PaginatedResponse,
  Ticket,
  CrearTicketDTO,
  AsignarTecnicoDTO,
  RegistrarDiagnosticoDTO,
  RechazarPresupuestoDTO,
  IniciarReparacionDTO,
  RegistrarPruebasDTO,
  RegistrarEntregaDTO,
  CancelarTicketDTO,
  PageRequest,
  EstadoTicket,
  AgregarPiezaTicketDTO,
  TicketPieza
} from '../../shared/models';

/**
 * Servicio para gestión de tickets de reparación.
 * Gestiona todo el flujo de vida de un ticket.
 */
@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/tickets`;
  private apiUrlPublic = `${environment.apiUrlPublic}/tickets`;

  // ==================== CRUD BÁSICO ====================

  /**
   * Obtiene todos los tickets con paginación
   */
  getAll(pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Ticket>>> {
    let params = new HttpParams();
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
      if (pageRequest.sort) params = params.set('sort', pageRequest.sort);
    }
    return this.http.get<ApiResponse<PaginatedResponse<Ticket>>>(this.apiUrl, { params });
  }

  /**
   * Obtiene un ticket por ID
   */
  getById(id: number): Observable<ApiResponse<Ticket>> {
    return this.http.get<ApiResponse<Ticket>>(`${this.apiUrl}/${id}`);
  }

  /**
   * Obtiene un ticket por número
   */
  getByNumero(numeroTicket: string): Observable<ApiResponse<Ticket>> {
    return this.http.get<ApiResponse<Ticket>>(`${this.apiUrl}/numero/${numeroTicket}`);
  }

  /**
   * Consulta pública de ticket (sin autenticación)
   */
  consultaPublica(numeroTicket: string): Observable<ApiResponse<Ticket>> {
    return this.http.get<ApiResponse<Ticket>>(`${this.apiUrlPublic}/${numeroTicket}`);
  }

  /**
   * Obtiene tickets de un cliente
   */
  getByCliente(clienteId: number, pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Ticket>>> {
    let params = new HttpParams();
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
    }
    return this.http.get<ApiResponse<PaginatedResponse<Ticket>>>(`${this.apiUrl}/cliente/${clienteId}`, { params });
  }

  /**
   * Obtiene tickets de un técnico
   */
  getByTecnico(tecnicoId: number, pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Ticket>>> {
    let params = new HttpParams();
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
    }
    return this.http.get<ApiResponse<PaginatedResponse<Ticket>>>(`${this.apiUrl}/tecnico/${tecnicoId}`, { params });
  }

  /**
   * Obtiene tickets por estado
   */
  getByEstado(estado: EstadoTicket, pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Ticket>>> {
    let params = new HttpParams();
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
    }
    return this.http.get<ApiResponse<PaginatedResponse<Ticket>>>(`${this.apiUrl}/estado/${estado}`, { params });
  }

  /**
   * Crea un nuevo ticket
   */
  create(ticket: CrearTicketDTO): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(this.apiUrl, ticket);
  }

  // ==================== FLUJO DEL TICKET ====================

  /**
   * Asigna un técnico al ticket
   */
  asignarTecnico(id: number, dto: AsignarTecnicoDTO): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.apiUrl}/${id}/asignar-tecnico`, dto);
  }

  /**
   * Registra el diagnóstico y presupuesto
   */
  registrarDiagnostico(id: number, dto: RegistrarDiagnosticoDTO): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.apiUrl}/${id}/diagnostico`, dto);
  }

  /**
   * Aprueba el presupuesto (cliente acepta)
   */
  aprobarPresupuesto(id: number): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.apiUrl}/${id}/aprobar-presupuesto`, {});
  }

  /**
   * Rechaza el presupuesto (cliente rechaza)
   */
  rechazarPresupuesto(id: number, dto: RechazarPresupuestoDTO): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.apiUrl}/${id}/rechazar-presupuesto`, dto);
  }

  /**
   * Inicia la reparación
   */
  iniciarReparacion(id: number, dto: IniciarReparacionDTO): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.apiUrl}/${id}/iniciar-reparacion`, dto);
  }

  /**
   * Finaliza la reparación
   */
  finalizarReparacion(id: number): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.apiUrl}/${id}/finalizar-reparacion`, {});
  }

  /**
   * Registra las pruebas
   */
  registrarPruebas(id: number, dto: RegistrarPruebasDTO): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.apiUrl}/${id}/pruebas`, dto);
  }

  /**
   * Marca el ticket como listo para entrega
   */
  marcarListoEntrega(id: number): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.apiUrl}/${id}/listo-entrega`, {});
  }

  /**
   * Registra la entrega del equipo
   */
  registrarEntrega(id: number, dto: RegistrarEntregaDTO): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.apiUrl}/${id}/entregar`, dto);
  }

  /**
   * Cancela el ticket
   */
  cancelar(id: number, dto: CancelarTicketDTO): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.apiUrl}/${id}/cancelar`, dto);
  }

  // ==================== GESTIÓN DE PIEZAS ====================

  /**
   * Agrega una pieza al ticket
   */
  agregarPieza(ticketId: number, dto: AgregarPiezaTicketDTO): Observable<ApiResponse<TicketPieza>> {
    return this.http.post<ApiResponse<TicketPieza>>(`${this.apiUrl}/${ticketId}/piezas`, dto);
  }

  /**
   * Obtiene las piezas de un ticket
   */
  getPiezas(ticketId: number): Observable<ApiResponse<TicketPieza[]>> {
    return this.http.get<ApiResponse<TicketPieza[]>>(`${this.apiUrl}/${ticketId}/piezas`);
  }

  /**
   * Remueve una pieza del ticket
   */
  removerPieza(ticketId: number, ticketPiezaId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${ticketId}/piezas/${ticketPiezaId}`);
  }

  /**
   * Actualiza la cantidad de una pieza en el ticket
   */
  actualizarCantidadPieza(ticketId: number, ticketPiezaId: number, cantidad: number): Observable<ApiResponse<TicketPieza>> {
    const params = new HttpParams().set('cantidad', cantidad.toString());
    return this.http.put<ApiResponse<TicketPieza>>(
      `${this.apiUrl}/${ticketId}/piezas/${ticketPiezaId}/cantidad`,
      null,
      { params }
    );
  }

  // ==================== DESCARGAS ====================

  /**
   * Descarga el PDF del ticket
   */
  descargarPDF(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, {
      responseType: 'blob'
    });
  }

  /**
   * Descarga el PDF del presupuesto
   */
  descargarPresupuestoPDF(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/presupuesto-pdf`, {
      responseType: 'blob'
    });
  }

  /**
   * Descarga pública del PDF del ticket
   */
  descargarPDFPublico(numeroTicket: string): Observable<Blob> {
    return this.http.get(`${this.apiUrlPublic}/${numeroTicket}/pdf`, {
      responseType: 'blob'
    });
  }
}
