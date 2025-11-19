package com.tesig.controller;

import com.tesig.dto.*;
import com.tesig.service.IPiezaService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de piezas e inventario.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo maneja HTTP requests para piezas
 * - Dependency Inversion: Depende de la abstracción IPiezaService
 *
 * @author TESIG System
 */
@RestController
@RequestMapping("/api/piezas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Piezas", description = "Endpoints para gestión de piezas e inventario")
@SecurityRequirement(name = "bearer-token")
public class PiezaController {

    private final IPiezaService piezaService;

    // ==================== CRUD BÁSICO ====================

    @Operation(
        summary = "Crear nueva pieza",
        description = "Crea una nueva pieza en el inventario del taller. " +
                     "Solo accesible para ADMINISTRADOR y RECEPCIONISTA."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Pieza creada exitosamente",
            content = @Content(schema = @Schema(implementation = PiezaResponseDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o código duplicado"
        )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<PiezaResponseDTO>> crear(
            @Valid @RequestBody CrearPiezaDTO dto
    ) {
        log.info("POST /api/piezas - Crear pieza: {}", dto.getCodigo());

        PiezaResponseDTO pieza = piezaService.crear(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pieza creada exitosamente", pieza));
    }

    @Operation(
        summary = "Actualizar pieza",
        description = "Actualiza una pieza existente. Solo accesible para ADMINISTRADOR y RECEPCIONISTA."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<PiezaResponseDTO>> actualizar(
            @Parameter(description = "ID de la pieza")
            @PathVariable Long id,
            @Valid @RequestBody ActualizarPiezaDTO dto
    ) {
        log.info("PUT /api/piezas/{} - Actualizar pieza", id);

        PiezaResponseDTO pieza = piezaService.actualizar(id, dto);

        return ResponseEntity.ok(
                ApiResponse.success("Pieza actualizada exitosamente", pieza)
        );
    }

    @Operation(
        summary = "Eliminar pieza",
        description = "Elimina una pieza (soft delete). Solo accesible para ADMINISTRADOR."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @Parameter(description = "ID de la pieza")
            @PathVariable Long id
    ) {
        log.info("DELETE /api/piezas/{} - Eliminar pieza", id);

        piezaService.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.success("Pieza eliminada exitosamente", null)
        );
    }

    @Operation(
        summary = "Obtener pieza por ID",
        description = "Obtiene los detalles de una pieza específica"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    public ResponseEntity<ApiResponse<PiezaResponseDTO>> obtenerPorId(
            @Parameter(description = "ID de la pieza")
            @PathVariable Long id
    ) {
        log.info("GET /api/piezas/{} - Obtener pieza", id);

        PiezaResponseDTO pieza = piezaService.obtenerPorId(id);

        return ResponseEntity.ok(
                ApiResponse.success("Pieza encontrada", pieza)
        );
    }

    @Operation(
        summary = "Obtener pieza por código",
        description = "Busca una pieza por su código único (SKU)"
    )
    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    public ResponseEntity<ApiResponse<PiezaResponseDTO>> obtenerPorCodigo(
            @Parameter(description = "Código único de la pieza", example = "LCD-SAM-15.6")
            @PathVariable String codigo
    ) {
        log.info("GET /api/piezas/codigo/{} - Obtener pieza por código", codigo);

        PiezaResponseDTO pieza = piezaService.obtenerPorCodigo(codigo);

        return ResponseEntity.ok(
                ApiResponse.success("Pieza encontrada", pieza)
        );
    }

    @Operation(
        summary = "Listar todas las piezas",
        description = "Lista todas las piezas activas con paginación"
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    public ResponseEntity<ApiResponse<Page<PiezaResponseDTO>>> listarTodas(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable
    ) {
        log.info("GET /api/piezas - Listar todas las piezas - Página: {}", pageable.getPageNumber());

        Page<PiezaResponseDTO> piezas = piezaService.listarTodas(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Piezas obtenidas exitosamente", piezas)
        );
    }

    // ==================== BÚSQUEDAS ====================

    @Operation(
        summary = "Buscar piezas por nombre",
        description = "Busca piezas que contengan el texto especificado en su nombre"
    )
    @GetMapping("/buscar/nombre")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    public ResponseEntity<ApiResponse<Page<PiezaResponseDTO>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre")
            @RequestParam String nombre,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("GET /api/piezas/buscar/nombre?nombre={}", nombre);

        Page<PiezaResponseDTO> piezas = piezaService.buscarPorNombre(nombre, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Búsqueda completada", piezas)
        );
    }

    @Operation(
        summary = "Buscar piezas por categoría",
        description = "Lista todas las piezas de una categoría específica"
    )
    @GetMapping("/buscar/categoria")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    public ResponseEntity<ApiResponse<Page<PiezaResponseDTO>>> buscarPorCategoria(
            @Parameter(description = "Categoría de la pieza")
            @RequestParam String categoria,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("GET /api/piezas/buscar/categoria?categoria={}", categoria);

        Page<PiezaResponseDTO> piezas = piezaService.buscarPorCategoria(categoria, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Búsqueda completada", piezas)
        );
    }

    @Operation(
        summary = "Buscar piezas por marca",
        description = "Lista todas las piezas de una marca específica"
    )
    @GetMapping("/buscar/marca")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    public ResponseEntity<ApiResponse<Page<PiezaResponseDTO>>> buscarPorMarca(
            @Parameter(description = "Marca de la pieza")
            @RequestParam String marca,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("GET /api/piezas/buscar/marca?marca={}", marca);

        Page<PiezaResponseDTO> piezas = piezaService.buscarPorMarca(marca, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Búsqueda completada", piezas)
        );
    }

    @Operation(
        summary = "Búsqueda global de piezas",
        description = "Busca piezas por código, nombre, descripción, marca o modelo"
    )
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    public ResponseEntity<ApiResponse<Page<PiezaResponseDTO>>> buscarGlobal(
            @Parameter(description = "Texto a buscar")
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("GET /api/piezas/buscar?q={}", q);

        Page<PiezaResponseDTO> piezas = piezaService.buscarGlobal(q, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Búsqueda completada", piezas)
        );
    }

    @Operation(
        summary = "Listar piezas disponibles",
        description = "Lista piezas activas que tienen stock disponible"
    )
    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    public ResponseEntity<ApiResponse<Page<PiezaResponseDTO>>> listarDisponibles(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("GET /api/piezas/disponibles");

        Page<PiezaResponseDTO> piezas = piezaService.listarDisponibles(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Piezas disponibles obtenidas", piezas)
        );
    }

    // ==================== GESTIÓN DE STOCK ====================

    @Operation(
        summary = "Ajustar stock de pieza",
        description = "Registra entrada o salida manual de stock. Solo ADMINISTRADOR y RECEPCIONISTA."
    )
    @PostMapping("/{id}/ajustar-stock")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<PiezaResponseDTO>> ajustarStock(
            @Parameter(description = "ID de la pieza")
            @PathVariable Long id,
            @Valid @RequestBody AjustarStockDTO dto
    ) {
        log.info("POST /api/piezas/{}/ajustar-stock - Tipo: {}, Cantidad: {}",
                 id, dto.getTipoMovimiento(), dto.getCantidad());

        PiezaResponseDTO pieza = piezaService.ajustarStock(id, dto);

        return ResponseEntity.ok(
                ApiResponse.success("Stock ajustado exitosamente", pieza)
        );
    }

    @Operation(
        summary = "Obtener piezas con stock bajo",
        description = "Lista piezas cuyo stock es menor o igual al stock mínimo configurado"
    )
    @GetMapping("/stock-bajo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<List<PiezaResponseDTO>>> obtenerPiezasConStockBajo() {
        log.info("GET /api/piezas/stock-bajo");

        List<PiezaResponseDTO> piezas = piezaService.obtenerPiezasConStockBajo();

        return ResponseEntity.ok(
                ApiResponse.success("Piezas con stock bajo obtenidas", piezas)
        );
    }

    @Operation(
        summary = "Obtener piezas sin stock",
        description = "Lista piezas que no tienen stock disponible"
    )
    @GetMapping("/sin-stock")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<List<PiezaResponseDTO>>> obtenerPiezasSinStock() {
        log.info("GET /api/piezas/sin-stock");

        List<PiezaResponseDTO> piezas = piezaService.obtenerPiezasSinStock();

        return ResponseEntity.ok(
                ApiResponse.success("Piezas sin stock obtenidas", piezas)
        );
    }

    // ==================== CATEGORÍAS ====================

    @Operation(
        summary = "Obtener categorías",
        description = "Lista todas las categorías de piezas disponibles"
    )
    @GetMapping("/categorias")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    public ResponseEntity<ApiResponse<List<String>>> obtenerCategorias() {
        log.info("GET /api/piezas/categorias");

        List<String> categorias = piezaService.obtenerCategorias();

        return ResponseEntity.ok(
                ApiResponse.success("Categorías obtenidas", categorias)
        );
    }
}
