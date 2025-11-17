export interface Pieza {
  id: number;
  codigo: string;
  nombre: string;
  descripcion?: string;
  categoria: string;
  marca?: string;
  modelo?: string;
  compatibilidad?: string;
  precioCosto: number;
  precioVenta: number;
  stock: number;
  stockMinimo: number;
  ubicacion?: string;
  proveedor?: string;
  proveedorTelefono?: string;
  proveedorEmail?: string;
  notas?: string;
  activo: boolean;
  necesitaReabastecimiento: boolean;
  margenGanancia: number;
  estadoStock: 'SIN_STOCK' | 'STOCK_BAJO' | 'STOCK_NORMAL';
  createdAt: string;
  updatedAt: string;
}

export interface CrearPiezaDTO {
  codigo: string;
  nombre: string;
  descripcion?: string;
  categoria: string;
  marca?: string;
  modelo?: string;
  compatibilidad?: string;
  precioCosto: number;
  precioVenta: number;
  stock: number;
  stockMinimo: number;
  ubicacion?: string;
  proveedor?: string;
  proveedorTelefono?: string;
  proveedorEmail?: string;
  notas?: string;
  activo?: boolean;
}

export interface ActualizarPiezaDTO {
  codigo?: string;
  nombre?: string;
  descripcion?: string;
  categoria?: string;
  marca?: string;
  modelo?: string;
  compatibilidad?: string;
  precioCosto?: number;
  precioVenta?: number;
  stock?: number;
  stockMinimo?: number;
  ubicacion?: string;
  proveedor?: string;
  proveedorTelefono?: string;
  proveedorEmail?: string;
  notas?: string;
  activo?: boolean;
}

export interface AjustarStockDTO {
  tipoMovimiento: 'ENTRADA' | 'SALIDA';
  cantidad: number;
  motivo: string;
  referencia?: string;
}

export interface AgregarPiezaTicketDTO {
  piezaId: number;
  cantidad: number;
  precioUnitario?: number;
  notas?: string;
}
