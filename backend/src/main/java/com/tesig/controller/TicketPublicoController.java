package com.tesig.controller;

import com.tesig.dto.ApiResponse;
import com.tesig.dto.TicketConsultaPublicaDTO;
import com.tesig.service.ITicketPublicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador público para consulta de tickets.
 * Este endpoint NO requiere autenticación para permitir
 * que los clientes consulten el estado de sus equipos.
 *
 * Principios SOLID aplicados:
 * - Dependency Inversion: Depende de la abstracción ITicketPublicoService
 * - Single Responsibility: Solo maneja HTTP requests para consultas públicas
 */
@RestController
@RequestMapping("/publico/tickets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Consulta Pública", description = "Endpoints públicos para consulta de tickets sin autenticación")
public class TicketPublicoController {

    private final ITicketPublicoService ticketPublicoService;

    @Operation(
        summary = "Consultar estado de ticket",
        description = "Permite consultar el estado de un ticket usando su número único. " +
                     "No requiere autenticación. Ideal para que clientes consulten el estado " +
                     "de sus equipos sin necesidad de crear una cuenta."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Ticket encontrado exitosamente",
            content = @Content(schema = @Schema(implementation = TicketConsultaPublicaDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Ticket no encontrado"
        )
    })
    @GetMapping("/{numeroTicket}")
    public ResponseEntity<ApiResponse<TicketConsultaPublicaDTO>> consultarTicket(
            @Parameter(description = "Número único del ticket", example = "TKT-2024-00001")
            @PathVariable String numeroTicket
    ) {
        log.info("GET /publico/tickets/{} - Consulta pública de ticket", numeroTicket);

        TicketConsultaPublicaDTO ticket = ticketPublicoService.consultarTicket(numeroTicket);

        return ResponseEntity.ok(
                ApiResponse.success("Ticket encontrado", ticket)
        );
    }

    @Operation(
        summary = "Verificar existencia de ticket",
        description = "Verifica si un número de ticket existe en el sistema sin retornar información detallada"
    )
    @GetMapping("/{numeroTicket}/existe")
    public ResponseEntity<ApiResponse<Boolean>> existeTicket(
            @Parameter(description = "Número único del ticket", example = "TKT-2024-00001")
            @PathVariable String numeroTicket
    ) {
        log.info("GET /publico/tickets/{}/existe - Verificación de existencia", numeroTicket);

        boolean existe = ticketPublicoService.existeTicket(numeroTicket);

        return ResponseEntity.ok(
                ApiResponse.success(
                        existe ? "El ticket existe" : "El ticket no existe",
                        existe
                )
        );
    }
}
