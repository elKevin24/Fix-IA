import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  PaginatedResponse,
  Cliente,
  CrearClienteDTO,
  ActualizarClienteDTO,
  PageRequest
} from '../../shared/models';

/**
 * Servicio para gestión de clientes.
 * Proporciona CRUD completo de clientes.
 */
@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/clientes`;

  /**
   * Obtiene todos los clientes con paginación
   */
  getAll(pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Cliente>>> {
    let params = new HttpParams();
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
      if (pageRequest.sort) params = params.set('sort', pageRequest.sort);
    }
    return this.http.get<ApiResponse<PaginatedResponse<Cliente>>>(this.apiUrl, { params });
  }

  /**
   * Obtiene un cliente por ID
   */
  getById(id: number): Observable<ApiResponse<Cliente>> {
    return this.http.get<ApiResponse<Cliente>>(`${this.apiUrl}/${id}`);
  }

  /**
   * Busca clientes por nombre
   */
  buscarPorNombre(nombre: string, pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Cliente>>> {
    let params = new HttpParams().set('nombre', nombre);
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
    }
    return this.http.get<ApiResponse<PaginatedResponse<Cliente>>>(`${this.apiUrl}/buscar/nombre`, { params });
  }

  /**
   * Busca clientes por email
   */
  buscarPorEmail(email: string): Observable<ApiResponse<Cliente>> {
    return this.http.get<ApiResponse<Cliente>>(`${this.apiUrl}/buscar/email/${email}`);
  }

  /**
   * Crea un nuevo cliente
   */
  create(cliente: CrearClienteDTO): Observable<ApiResponse<Cliente>> {
    return this.http.post<ApiResponse<Cliente>>(this.apiUrl, cliente);
  }

  /**
   * Actualiza un cliente existente
   */
  update(id: number, cliente: ActualizarClienteDTO): Observable<ApiResponse<Cliente>> {
    return this.http.put<ApiResponse<Cliente>>(`${this.apiUrl}/${id}`, cliente);
  }

  /**
   * Elimina un cliente (soft delete)
   */
  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}
