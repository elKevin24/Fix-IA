package com.tesig.controller;

import com.tesig.dto.ApiResponse;
import com.tesig.dto.PaginatedResponseDTO;
import com.tesig.dto.TicketConsultaPublicaDTO;
import com.tesig.dto.cliente.ClienteCreateDTO;
import com.tesig.dto.cliente.ClienteDTO;
import com.tesig.dto.cliente.ClienteUpdateDTO;
import com.tesig.service.IClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de clientes.
 * Todos los endpoints requieren autenticación JWT.
 */
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Clientes", description = "Gestión de clientes del taller")
@SecurityRequirement(name = "Bearer Authentication")
public class ClienteController {

    private final IClienteService clienteService;

    @Operation(
        summary = "Listar todos los clientes",
        description = "Obtiene una lista paginada de todos los clientes. " +
                     "Accesible por todos los roles autenticados."
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<PaginatedResponseDTO<ClienteDTO>>> findAll(
            @Parameter(description = "Número de página (inicia en 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo por el cual ordenar")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Dirección del ordenamiento (ASC o DESC)")
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        log.info("GET /api/clientes - page: {}, size: {}, sortBy: {}, direction: {}",
                page, size, sortBy, direction);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        PaginatedResponseDTO<ClienteDTO> clientes = clienteService.findAll(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Clientes obtenidos exitosamente", clientes)
        );
    }

    @Operation(
        summary = "Obtener cliente por ID",
        description = "Obtiene los detalles completos de un cliente por su ID"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<ClienteDTO>> findById(
            @Parameter(description = "ID del cliente")
            @PathVariable Long id
    ) {
        log.info("GET /api/clientes/{}", id);

        ClienteDTO cliente = clienteService.findById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Cliente encontrado", cliente)
        );
    }

    @Operation(
        summary = "Crear nuevo cliente",
        description = "Crea un nuevo cliente en el sistema. " +
                     "Solo accesible por ADMINISTRADOR y RECEPCIONISTA."
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<ClienteDTO>> create(
            @Valid @RequestBody ClienteCreateDTO createDTO
    ) {
        log.info("POST /api/clientes - Crear cliente: {} {}",
                createDTO.getNombre(), createDTO.getApellido());

        ClienteDTO cliente = clienteService.create(createDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cliente creado exitosamente", cliente));
    }

    @Operation(
        summary = "Actualizar cliente completo",
        description = "Actualiza todos los campos de un cliente existente. " +
                     "Solo accesible por ADMINISTRADOR y RECEPCIONISTA."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<ClienteDTO>> update(
            @Parameter(description = "ID del cliente")
            @PathVariable Long id,
            @Valid @RequestBody ClienteUpdateDTO updateDTO
    ) {
        log.info("PUT /api/clientes/{}", id);

        ClienteDTO cliente = clienteService.update(id, updateDTO);

        return ResponseEntity.ok(
                ApiResponse.success("Cliente actualizado exitosamente", cliente)
        );
    }

    @Operation(
        summary = "Actualizar cliente parcialmente",
        description = "Actualiza solo los campos especificados de un cliente. " +
                     "Los campos no enviados no se modifican. " +
                     "Solo accesible por ADMINISTRADOR y RECEPCIONISTA."
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<ClienteDTO>> partialUpdate(
            @Parameter(description = "ID del cliente")
            @PathVariable Long id,
            @Valid @RequestBody ClienteUpdateDTO updateDTO
    ) {
        log.info("PATCH /api/clientes/{}", id);

        ClienteDTO cliente = clienteService.partialUpdate(id, updateDTO);

        return ResponseEntity.ok(
                ApiResponse.success("Cliente actualizado exitosamente", cliente)
        );
    }

    @Operation(
        summary = "Eliminar cliente",
        description = "Elimina un cliente del sistema (soft delete). " +
                     "No se puede eliminar si tiene tickets activos. " +
                     "Solo accesible por ADMINISTRADOR."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID del cliente")
            @PathVariable Long id
    ) {
        log.info("DELETE /api/clientes/{}", id);

        clienteService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success("Cliente eliminado exitosamente", null)
        );
    }

    @Operation(
        summary = "Obtener historial de tickets del cliente",
        description = "Obtiene todos los tickets asociados a un cliente"
    )
    @GetMapping("/{id}/tickets")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<List<TicketConsultaPublicaDTO>>> getTickets(
            @Parameter(description = "ID del cliente")
            @PathVariable Long id
    ) {
        log.info("GET /api/clientes/{}/tickets", id);

        List<TicketConsultaPublicaDTO> tickets = clienteService.getTicketsByCliente(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Tickets del cliente obtenidos exitosamente",
                        tickets
                )
        );
    }

    @Operation(
        summary = "Buscar clientes",
        description = "Busca clientes por nombre, apellido o teléfono"
    )
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> search(
            @Parameter(description = "Texto a buscar en nombre, apellido o teléfono")
            @RequestParam String q
    ) {
        log.info("GET /api/clientes/buscar?q={}", q);

        List<ClienteDTO> clientes = clienteService.search(q);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Búsqueda completada - " + clientes.size() + " resultado(s)",
                        clientes
                )
        );
    }
}
