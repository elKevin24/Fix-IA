import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  PaginatedResponse,
  Pieza,
  CrearPiezaDTO,
  ActualizarPiezaDTO,
  AjustarStockDTO,
  PageRequest
} from '../../shared/models';

/**
 * Servicio para gestión de piezas e inventario.
 * Proporciona CRUD completo, búsquedas y control de stock.
 */
@Injectable({
  providedIn: 'root'
})
export class PiezaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/piezas`;

  // ==================== CRUD BÁSICO ====================

  /**
   * Obtiene todas las piezas con paginación
   */
  getAll(pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Pieza>>> {
    let params = new HttpParams();
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
      if (pageRequest.sort) params = params.set('sort', pageRequest.sort);
    }
    return this.http.get<ApiResponse<PaginatedResponse<Pieza>>>(this.apiUrl, { params });
  }

  /**
   * Obtiene una pieza por ID
   */
  getById(id: number): Observable<ApiResponse<Pieza>> {
    return this.http.get<ApiResponse<Pieza>>(`${this.apiUrl}/${id}`);
  }

  /**
   * Obtiene una pieza por código (SKU)
   */
  getByCodigo(codigo: string): Observable<ApiResponse<Pieza>> {
    return this.http.get<ApiResponse<Pieza>>(`${this.apiUrl}/codigo/${codigo}`);
  }

  /**
   * Crea una nueva pieza
   */
  create(pieza: CrearPiezaDTO): Observable<ApiResponse<Pieza>> {
    return this.http.post<ApiResponse<Pieza>>(this.apiUrl, pieza);
  }

  /**
   * Actualiza una pieza existente
   */
  update(id: number, pieza: ActualizarPiezaDTO): Observable<ApiResponse<Pieza>> {
    return this.http.put<ApiResponse<Pieza>>(`${this.apiUrl}/${id}`, pieza);
  }

  /**
   * Elimina una pieza (soft delete)
   */
  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  // ==================== BÚSQUEDAS ====================

  /**
   * Busca piezas por nombre
   */
  buscarPorNombre(nombre: string, pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Pieza>>> {
    let params = new HttpParams().set('nombre', nombre);
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
    }
    return this.http.get<ApiResponse<PaginatedResponse<Pieza>>>(`${this.apiUrl}/buscar/nombre`, { params });
  }

  /**
   * Busca piezas por categoría
   */
  buscarPorCategoria(categoria: string, pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Pieza>>> {
    let params = new HttpParams().set('categoria', categoria);
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
    }
    return this.http.get<ApiResponse<PaginatedResponse<Pieza>>>(`${this.apiUrl}/buscar/categoria`, { params });
  }

  /**
   * Busca piezas por marca
   */
  buscarPorMarca(marca: string, pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Pieza>>> {
    let params = new HttpParams().set('marca', marca);
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
    }
    return this.http.get<ApiResponse<PaginatedResponse<Pieza>>>(`${this.apiUrl}/buscar/marca`, { params });
  }

  /**
   * Búsqueda global por múltiples criterios
   */
  buscarGlobal(query: string, pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Pieza>>> {
    let params = new HttpParams().set('q', query);
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
    }
    return this.http.get<ApiResponse<PaginatedResponse<Pieza>>>(`${this.apiUrl}/buscar`, { params });
  }

  /**
   * Lista solo piezas disponibles (activas y con stock)
   */
  getDisponibles(pageRequest?: PageRequest): Observable<ApiResponse<PaginatedResponse<Pieza>>> {
    let params = new HttpParams();
    if (pageRequest) {
      if (pageRequest.page !== undefined) params = params.set('page', pageRequest.page.toString());
      if (pageRequest.size !== undefined) params = params.set('size', pageRequest.size.toString());
    }
    return this.http.get<ApiResponse<PaginatedResponse<Pieza>>>(`${this.apiUrl}/disponibles`, { params });
  }

  // ==================== GESTIÓN DE STOCK ====================

  /**
   * Ajusta el stock de una pieza (entrada o salida manual)
   */
  ajustarStock(id: number, dto: AjustarStockDTO): Observable<ApiResponse<Pieza>> {
    return this.http.post<ApiResponse<Pieza>>(`${this.apiUrl}/${id}/ajustar-stock`, dto);
  }

  /**
   * Obtiene piezas con stock bajo
   */
  getPiezasConStockBajo(): Observable<ApiResponse<Pieza[]>> {
    return this.http.get<ApiResponse<Pieza[]>>(`${this.apiUrl}/stock-bajo`);
  }

  /**
   * Obtiene piezas sin stock
   */
  getPiezasSinStock(): Observable<ApiResponse<Pieza[]>> {
    return this.http.get<ApiResponse<Pieza[]>>(`${this.apiUrl}/sin-stock`);
  }

  // ==================== CATEGORÍAS ====================

  /**
   * Obtiene todas las categorías disponibles
   */
  getCategorias(): Observable<ApiResponse<string[]>> {
    return this.http.get<ApiResponse<string[]>>(`${this.apiUrl}/categorias`);
  }
}
