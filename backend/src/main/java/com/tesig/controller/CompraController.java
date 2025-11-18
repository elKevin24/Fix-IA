package com.tesig.controller;

import com.tesig.dto.ApiResponse;
import com.tesig.dto.compra.CompraResponseDTO;
import com.tesig.dto.compra.CrearCompraDTO;
import com.tesig.service.ICompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para gestión de compras.
 *
 * @author TESIG System
 */
@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Compras", description = "Endpoints para gestión de órdenes de compra")
@SecurityRequirement(name = "bearer-token")
public class CompraController {

    private final ICompraService compraService;

    // ==================== CRUD BÁSICO ====================

    @Operation(
        summary = "Crear nueva compra",
        description = "Crea una nueva orden de compra con sus detalles. " +
                     "Solo accesible para ADMINISTRADOR."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Compra creada exitosamente",
            content = @Content(schema = @Schema(implementation = CompraResponseDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos inválidos"
        )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<CompraResponseDTO>> crear(
            @Valid @RequestBody CrearCompraDTO dto
    ) {
        log.info("POST /api/compras - Crear compra para proveedor: {}", dto.getProveedor());

        CompraResponseDTO compra = compraService.crear(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Compra creada exitosamente", compra));
    }

    @Operation(
        summary = "Obtener compra por ID",
        description = "Obtiene los detalles de una compra específica"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<CompraResponseDTO>> obtenerPorId(
            @Parameter(description = "ID de la compra")
            @PathVariable Long id
    ) {
        log.info("GET /api/compras/{} - Obtener compra", id);

        CompraResponseDTO compra = compraService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Compra encontrada", compra)
        );
    }

    @Operation(
        summary = "Listar todas las compras",
        description = "Lista todas las compras con paginación"
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<Page<CompraResponseDTO>>> listarTodas(
            @PageableDefault(size = 20, sort = "fechaCompra") Pageable pageable
    ) {
        log.info("GET /api/compras - Listar todas las compras - Página: {}", pageable.getPageNumber());

        Page<CompraResponseDTO> compras = compraService.getAll(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Compras obtenidas exitosamente", compras)
        );
    }

    @Operation(
        summary = "Eliminar compra",
        description = "Elimina una compra (soft delete). Solo compras pendientes pueden eliminarse."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @Parameter(description = "ID de la compra")
            @PathVariable Long id
    ) {
        log.info("DELETE /api/compras/{} - Eliminar compra", id);

        compraService.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.success("Compra eliminada exitosamente", null)
        );
    }

    // ==================== BÚSQUEDAS ====================

    @Operation(
        summary = "Buscar compras por proveedor",
        description = "Busca compras que contengan el texto especificado en el proveedor"
    )
    @GetMapping("/buscar/proveedor")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<Page<CompraResponseDTO>>> buscarPorProveedor(
            @Parameter(description = "Nombre del proveedor")
            @RequestParam String proveedor,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("GET /api/compras/buscar/proveedor?proveedor={}", proveedor);

        Page<CompraResponseDTO> compras = compraService.buscarPorProveedor(proveedor, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Búsqueda completada", compras)
        );
    }

    @Operation(
        summary = "Obtener compras por estado",
        description = "Lista compras filtradas por estado (PENDIENTE, RECIBIDA, CANCELADA)"
    )
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<Page<CompraResponseDTO>>> obtenerPorEstado(
            @Parameter(description = "Estado de la compra")
            @PathVariable String estado,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("GET /api/compras/estado/{}", estado);

        Page<CompraResponseDTO> compras = compraService.getByEstado(estado, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Compras obtenidas", compras)
        );
    }

    @Operation(
        summary = "Obtener compras por rango de fechas",
        description = "Lista compras dentro del rango de fechas especificado"
    )
    @GetMapping("/fechas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<List<CompraResponseDTO>>> obtenerPorFechas(
            @Parameter(description = "Fecha de inicio")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        log.info("GET /api/compras/fechas?fechaInicio={}&fechaFin={}", fechaInicio, fechaFin);

        List<CompraResponseDTO> compras = compraService.getByFechas(fechaInicio, fechaFin);

        return ResponseEntity.ok(
                ApiResponse.success("Compras obtenidas", compras)
        );
    }

    // ==================== ACCIONES DE ESTADO ====================

    @Operation(
        summary = "Recibir compra",
        description = "Marca la compra como recibida y actualiza el inventario automáticamente"
    )
    @PostMapping("/{id}/recibir")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<CompraResponseDTO>> recibirCompra(
            @Parameter(description = "ID de la compra")
            @PathVariable Long id
    ) {
        log.info("POST /api/compras/{}/recibir - Recibir compra", id);

        CompraResponseDTO compra = compraService.recibirCompra(id);

        return ResponseEntity.ok(
                ApiResponse.success("Compra recibida e inventario actualizado", compra)
        );
    }

    @Operation(
        summary = "Cancelar compra",
        description = "Cancela una compra pendiente"
    )
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<CompraResponseDTO>> cancelarCompra(
            @Parameter(description = "ID de la compra")
            @PathVariable Long id
    ) {
        log.info("POST /api/compras/{}/cancelar - Cancelar compra", id);

        CompraResponseDTO compra = compraService.cancelarCompra(id);

        return ResponseEntity.ok(
                ApiResponse.success("Compra cancelada exitosamente", compra)
        );
    }
}
