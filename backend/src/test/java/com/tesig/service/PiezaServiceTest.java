package com.tesig.service;

import com.tesig.dto.*;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.mapper.PiezaMapper;
import com.tesig.model.Pieza;
import com.tesig.repository.PiezaRepository;
import com.tesig.service.impl.PiezaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PiezaService.
 */
@ExtendWith(MockitoExtension.class)
class PiezaServiceTest {

    @Mock
    private PiezaRepository piezaRepository;

    @Mock
    private PiezaMapper piezaMapper;

    @InjectMocks
    private PiezaServiceImpl piezaService;

    private Pieza pieza;
    private PiezaResponseDTO piezaDTO;
    private CrearPiezaDTO crearDTO;

    @BeforeEach
    void setUp() {
        pieza = new Pieza();
        pieza.setId(1L);
        pieza.setCodigo("LCD-SAM-15.6");
        pieza.setNombre("Pantalla LCD 15.6\"");
        pieza.setDescripcion("Pantalla LCD Samsung");
        pieza.setCategoria("Pantallas");
        pieza.setMarca("Samsung");
        pieza.setPrecioCosto(new BigDecimal("85.00"));
        pieza.setPrecioVenta(new BigDecimal("150.00"));
        pieza.setStock(10);
        pieza.setStockMinimo(2);
        pieza.setActivo(true);
        pieza.setCreatedAt(LocalDateTime.now());

        piezaDTO = new PiezaResponseDTO();
        piezaDTO.setId(1L);
        piezaDTO.setCodigo("LCD-SAM-15.6");
        piezaDTO.setNombre("Pantalla LCD 15.6\"");
        piezaDTO.setStock(10);
        piezaDTO.setStockMinimo(2);
        piezaDTO.setPrecioVenta(new BigDecimal("150.00"));

        crearDTO = new CrearPiezaDTO();
        crearDTO.setCodigo("LCD-SAM-15.6");
        crearDTO.setNombre("Pantalla LCD 15.6\"");
        crearDTO.setCategoria("Pantallas");
        crearDTO.setPrecioCosto(new BigDecimal("85.00"));
        crearDTO.setPrecioVenta(new BigDecimal("150.00"));
        crearDTO.setStock(10);
    }

    @Test
    @DisplayName("Debe crear una pieza exitosamente")
    void crear_DatosValidos_CreaPieza() {
        // Arrange
        when(piezaRepository.existsByCodigo(anyString())).thenReturn(false);
        when(piezaMapper.toEntity(any(CrearPiezaDTO.class))).thenReturn(pieza);
        when(piezaRepository.save(any(Pieza.class))).thenReturn(pieza);
        when(piezaMapper.toDTO(any(Pieza.class))).thenReturn(piezaDTO);

        // Act
        PiezaResponseDTO result = piezaService.crear(crearDTO);

        // Assert
        assertNotNull(result);
        assertEquals("LCD-SAM-15.6", result.getCodigo());
        verify(piezaRepository).save(any(Pieza.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si código ya existe")
    void crear_CodigoDuplicado_LanzaExcepcion() {
        // Arrange
        when(piezaRepository.existsByCodigo(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            piezaService.crear(crearDTO);
        });
        verify(piezaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe obtener pieza por ID")
    void obtenerPorId_IdExiste_RetornaPieza() {
        // Arrange
        when(piezaRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(pieza));
        when(piezaMapper.toDTO(pieza)).thenReturn(piezaDTO);

        // Act
        PiezaResponseDTO result = piezaService.obtenerPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción si pieza no existe")
    void obtenerPorId_IdNoExiste_LanzaExcepcion() {
        // Arrange
        when(piezaRepository.findByIdAndNotDeleted(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            piezaService.obtenerPorId(99L);
        });
    }

    @Test
    @DisplayName("Debe obtener pieza por código")
    void obtenerPorCodigo_Existe_RetornaPieza() {
        // Arrange
        when(piezaRepository.findByCodigoAndNotDeleted(anyString()))
                .thenReturn(Optional.of(pieza));
        when(piezaMapper.toDTO(pieza)).thenReturn(piezaDTO);

        // Act
        PiezaResponseDTO result = piezaService.obtenerPorCodigo("LCD-SAM-15.6");

        // Assert
        assertNotNull(result);
        assertEquals("LCD-SAM-15.6", result.getCodigo());
    }

    @Test
    @DisplayName("Debe listar piezas con stock bajo")
    void obtenerPiezasConStockBajo_Existe_RetornaLista() {
        // Arrange
        pieza.setStock(1); // Menor que stockMinimo (2)
        when(piezaRepository.findByStockLessThanEqualStockMinimo())
                .thenReturn(List.of(pieza));
        when(piezaMapper.toDTO(any(Pieza.class))).thenReturn(piezaDTO);

        // Act
        List<PiezaResponseDTO> result = piezaService.obtenerPiezasConStockBajo();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Debe listar piezas sin stock")
    void obtenerPiezasSinStock_Existe_RetornaLista() {
        // Arrange
        pieza.setStock(0);
        when(piezaRepository.findByStockEqualsZero())
                .thenReturn(List.of(pieza));
        when(piezaMapper.toDTO(any(Pieza.class))).thenReturn(piezaDTO);

        // Act
        List<PiezaResponseDTO> result = piezaService.obtenerPiezasSinStock();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Debe ajustar stock correctamente - Entrada")
    void ajustarStock_Entrada_AumentaStock() {
        // Arrange
        AjustarStockDTO ajusteDTO = new AjustarStockDTO();
        ajusteDTO.setCantidad(5);
        ajusteDTO.setTipoMovimiento("ENTRADA");
        ajusteDTO.setMotivo("Ajuste manual");

        when(piezaRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(pieza));
        when(piezaRepository.save(any(Pieza.class))).thenReturn(pieza);
        when(piezaMapper.toDTO(any(Pieza.class))).thenReturn(piezaDTO);

        // Act
        PiezaResponseDTO result = piezaService.ajustarStock(1L, ajusteDTO);

        // Assert
        assertNotNull(result);
        assertEquals(15, pieza.getStock()); // 10 + 5
        verify(piezaRepository).save(pieza);
    }

    @Test
    @DisplayName("Debe ajustar stock correctamente - Salida")
    void ajustarStock_Salida_DisminuyeStock() {
        // Arrange
        AjustarStockDTO ajusteDTO = new AjustarStockDTO();
        ajusteDTO.setCantidad(3);
        ajusteDTO.setTipoMovimiento("SALIDA");
        ajusteDTO.setMotivo("Uso en reparación");

        when(piezaRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(pieza));
        when(piezaRepository.save(any(Pieza.class))).thenReturn(pieza);
        when(piezaMapper.toDTO(any(Pieza.class))).thenReturn(piezaDTO);

        // Act
        PiezaResponseDTO result = piezaService.ajustarStock(1L, ajusteDTO);

        // Assert
        assertNotNull(result);
        assertEquals(7, pieza.getStock()); // 10 - 3
    }

    @Test
    @DisplayName("Debe lanzar excepción si stock insuficiente")
    void ajustarStock_StockInsuficiente_LanzaExcepcion() {
        // Arrange
        AjustarStockDTO ajusteDTO = new AjustarStockDTO();
        ajusteDTO.setCantidad(20);
        ajusteDTO.setTipoMovimiento("SALIDA");

        when(piezaRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(pieza));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            piezaService.ajustarStock(1L, ajusteDTO);
        });
    }

    @Test
    @DisplayName("Debe eliminar pieza (soft delete)")
    void eliminar_IdExiste_EliminaPieza() {
        // Arrange
        when(piezaRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(pieza));

        // Act
        piezaService.eliminar(1L);

        // Assert
        assertNotNull(pieza.getDeletedAt());
        verify(piezaRepository).save(pieza);
    }

    @Test
    @DisplayName("Debe listar piezas disponibles")
    void listarDisponibles_ConStock_RetornaLista() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pieza> piezasPage = new PageImpl<>(List.of(pieza));

        when(piezaRepository.findByStockGreaterThanZeroAndActive(pageable))
                .thenReturn(piezasPage);
        when(piezaMapper.toDTO(any(Pieza.class))).thenReturn(piezaDTO);

        // Act
        Page<PiezaResponseDTO> result = piezaService.listarDisponibles(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Debe buscar piezas globalmente")
    void buscarGlobal_Termino_RetornaResultados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pieza> piezasPage = new PageImpl<>(List.of(pieza));

        when(piezaRepository.buscarGlobal(anyString(), any(Pageable.class)))
                .thenReturn(piezasPage);
        when(piezaMapper.toDTO(any(Pieza.class))).thenReturn(piezaDTO);

        // Act
        Page<PiezaResponseDTO> result = piezaService.buscarGlobal("LCD", pageable);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Debe obtener categorías disponibles")
    void obtenerCategorias_Existen_RetornaLista() {
        // Arrange
        when(piezaRepository.findDistinctCategorias())
                .thenReturn(List.of("Pantallas", "Baterías", "Teclados"));

        // Act
        List<String> result = piezaService.obtenerCategorias();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("Pantallas"));
    }
}
