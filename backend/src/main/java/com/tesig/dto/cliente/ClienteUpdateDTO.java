package com.tesig.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un cliente existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteUpdateDTO {

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;

    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener exactamente 10 dígitos")
    private String telefono;

    @Email(message = "Email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;

    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String notas;
}
