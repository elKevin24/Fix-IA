package com.tesig.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registrar resultado de pruebas.
 * Single Responsibility: Solo resultados de pruebas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PruebasDTO {

    @NotBlank(message = "El resultado de las pruebas es obligatorio")
    @Size(max = 1000, message = "El resultado no puede exceder 1000 caracteres")
    private String resultadoPruebas;

    @NotNull(message = "Debe indicar si las pruebas fueron exitosas")
    private Boolean exitoso;
}
