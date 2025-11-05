package com.tesig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con información básica del cliente para consultas públicas.
 * MapStruct se encarga del mapeo desde la entidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteBasicoDTO {
    private String nombre;
    private String apellido;
    private String nombreCompleto;
}
