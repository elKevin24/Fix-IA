package com.tesig.service;

import com.tesig.dto.compra.CompraDetalleDTO;
import com.tesig.dto.compra.CompraResponseDTO;
import com.tesig.dto.compra.CrearCompraDTO;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.mapper.CompraMapper;
import com.tesig.model.Compra;
import com.tesig.model.CompraDetalle;
import com.tesig.model.MovimientoInventario;
import com.tesig.model.Pieza;
import com.tesig.repository.CompraRepository;
import com.tesig.repository.MovimientoInventarioRepository;
import com.tesig.repository.PiezaRepository;
import com.tesig.service.impl.CompraServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CompraService.
 */
@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private PiezaRepository piezaRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private CompraMapper compraMapper;

    @InjectMocks
    private CompraServiceImpl compraService;

    private Compra compra;
    private CompraResponseDTO compraDTO;
    private CrearCompraDTO crearDTO;
    private Pieza pieza;

    @BeforeEach
    void setUp() {
        // Pieza
        pieza = new Pieza();
        pieza.setId(1L);
        pieza.setCodigo("LCD-SAM-15.6");
        pieza.setNombre("Pantalla LCD");
        pieza.setStock(10);
        pieza.setPrecioCosto(new BigDecimal("85.00"));

        // Compra
        compra = new Compra();
        compra.setId(1L);
        compra.setCodigoCompra("TES-CMP-20251118-0001");
        compra.setProveedor("Proveedor ABC");
        compra.setFechaCompra(LocalDate.now());
        compra.setEstado(Compra.EstadoCompra.PENDIENTE);
        compra.setTotal(new BigDecimal("850.00"));
        compra.setCreatedAt(LocalDateTime.now());

        // Detalle de compra
        CompraDetalle detalle = new CompraDetalle();
        detalle.setId(1L);
        detalle.setCompra(compra);
        detalle.setPieza(pieza);
        detalle.setCantidad(10);
        detalle.setPrecioUnitario(new BigDecimal("85.00"));
        detalle.setSubtotal(new BigDecimal("850.00"));

        List<CompraDetalle> detalles = new ArrayList<>();
        detalles.add(detalle);
        compra.setDetalles(detalles);

        // DTO Response
        compraDTO = new CompraResponseDTO();
        compraDTO.setId(1L);
        compraDTO.setCodigoCompra("TES-CMP-20251118-0001");
        compraDTO.setProveedor("Proveedor ABC");
        compraDTO.setEstado("PENDIENTE");
        compraDTO.setTotal(new BigDecimal("850.00"));

        // DTO Crear
        crearDTO = new CrearCompraDTO();
        crearDTO.setProveedor("Proveedor ABC");
        crearDTO.setFechaCompra(LocalDate.now());

        CompraDetalleDTO detalleDTO = new CompraDetalleDTO();
        detalleDTO.setPiezaId(1L);
        detalleDTO.setCantidad(10);
        detalleDTO.setPrecioUnitario(new BigDecimal("85.00"));
        crearDTO.setDetalles(List.of(detalleDTO));
    }

    @Test
    @DisplayName("Debe crear una compra exitosamente")
    void crear_DatosValidos_CreaCompra() {
        // Arrange
        when(piezaRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(pieza));
        when(compraRepository.save(any(Compra.class))).thenReturn(compra);
        when(compraMapper.toDTO(any(Compra.class))).thenReturn(compraDTO);

        // Act
        CompraResponseDTO result = compraService.crear(crearDTO);

        // Assert
        assertNotNull(result);
        assertEquals("TES-CMP-20251118-0001", result.getCodigoCompra());
        verify(compraRepository).save(any(Compra.class));
    }

    @Test
    @DisplayName("Debe obtener compra por ID")
    void getById_IdExiste_RetornaCompra() {
        // Arrange
        when(compraRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(compra));
        when(compraMapper.toDTO(compra)).thenReturn(compraDTO);

        // Act
        CompraResponseDTO result = compraService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción si compra no existe")
    void getById_IdNoExiste_LanzaExcepcion() {
        // Arrange
        when(compraRepository.findByIdAndNotDeleted(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            compraService.getById(99L);
        });
    }

    @Test
    @DisplayName("Debe recibir compra y actualizar inventario")
    void recibirCompra_CompraPendiente_ActualizaInventario() {
        // Arrange
        when(compraRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(compra));
        when(piezaRepository.save(any(Pieza.class))).thenReturn(pieza);
        when(movimientoRepository.save(any(MovimientoInventario.class)))
                .thenReturn(new MovimientoInventario());
        when(compraRepository.save(any(Compra.class))).thenReturn(compra);
        when(compraMapper.toDTO(any(Compra.class))).thenReturn(compraDTO);

        // Act
        CompraResponseDTO result = compraService.recibirCompra(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Compra.EstadoCompra.RECIBIDA, compra.getEstado());
        assertEquals(20, pieza.getStock()); // 10 + 10
        verify(movimientoRepository).save(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("No debe recibir compra ya recibida")
    void recibirCompra_YaRecibida_LanzaExcepcion() {
        // Arrange
        compra.setEstado(Compra.EstadoCompra.RECIBIDA);
        when(compraRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(compra));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            compraService.recibirCompra(1L);
        });
    }

    @Test
    @DisplayName("Debe cancelar compra pendiente")
    void cancelarCompra_Pendiente_CancelaCompra() {
        // Arrange
        when(compraRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(compra));
        when(compraRepository.save(any(Compra.class))).thenReturn(compra);
        when(compraMapper.toDTO(any(Compra.class))).thenReturn(compraDTO);

        // Act
        CompraResponseDTO result = compraService.cancelarCompra(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Compra.EstadoCompra.CANCELADA, compra.getEstado());
    }

    @Test
    @DisplayName("No debe cancelar compra ya recibida")
    void cancelarCompra_YaRecibida_LanzaExcepcion() {
        // Arrange
        compra.setEstado(Compra.EstadoCompra.RECIBIDA);
        when(compraRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(compra));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            compraService.cancelarCompra(1L);
        });
    }

    @Test
    @DisplayName("Debe eliminar compra pendiente")
    void eliminar_CompraPendiente_EliminaCompra() {
        // Arrange
        when(compraRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(compra));

        // Act
        compraService.eliminar(1L);

        // Assert
        assertTrue(compra.getDeleted());
        verify(compraRepository).save(compra);
    }

    @Test
    @DisplayName("No debe eliminar compra recibida")
    void eliminar_CompraRecibida_LanzaExcepcion() {
        // Arrange
        compra.setEstado(Compra.EstadoCompra.RECIBIDA);
        when(compraRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(compra));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            compraService.eliminar(1L);
        });
    }
}
