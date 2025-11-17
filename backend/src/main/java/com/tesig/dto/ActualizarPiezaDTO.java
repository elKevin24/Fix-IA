package com.tesig.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para actualizar una pieza existente en el inventario.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 *
 * Aplicación de principios:
 * - Single Responsibility: Solo para transferir datos de actualización de pieza
 * - Validation: Valida los datos de entrada cuando están presentes
 *
 * @author TESIG System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarPiezaDTO {

    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    @Pattern(regexp = "^[A-Z0-9-]+$",
             message = "El código solo puede contener letras mayúsculas, números y guiones")
    private String codigo;

    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria;

    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String marca;

    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    private String modelo;

    @Size(max = 500, message = "La compatibilidad no puede exceder 500 caracteres")
    private String compatibilidad;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de costo debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio de costo debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal precioCosto;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio de venta debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal precioVenta;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    private String ubicacion;

    @Size(max = 200, message = "El proveedor no puede exceder 200 caracteres")
    private String proveedor;

    @Size(max = 20, message = "El teléfono del proveedor no puede exceder 20 caracteres")
    private String proveedorTelefono;

    @Email(message = "El email del proveedor debe ser válido")
    @Size(max = 100, message = "El email del proveedor no puede exceder 100 caracteres")
    private String proveedorEmail;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;

    private Boolean activo;
}
