package com.tesig.service;

import com.tesig.dto.gasto.CrearGastoDTO;
import com.tesig.dto.gasto.GastoResponseDTO;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.mapper.GastoMapper;
import com.tesig.model.Gasto;
import com.tesig.repository.GastoRepository;
import com.tesig.service.impl.GastoServiceImpl;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GastoService.
 */
@ExtendWith(MockitoExtension.class)
class GastoServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    @Mock
    private GastoMapper gastoMapper;

    @InjectMocks
    private GastoServiceImpl gastoService;

    private Gasto gasto;
    private GastoResponseDTO gastoDTO;
    private CrearGastoDTO crearDTO;

    @BeforeEach
    void setUp() {
        gasto = new Gasto();
        gasto.setId(1L);
        gasto.setConcepto("Pago de arriendo");
        gasto.setMonto(new BigDecimal("500.00"));
        gasto.setFecha(LocalDate.now());
        gasto.setCategoria(Gasto.CategoriaGasto.ARRIENDO);
        gasto.setMetodoPago(Gasto.MetodoPago.TRANSFERENCIA);
        gasto.setCreatedAt(LocalDateTime.now());

        gastoDTO = new GastoResponseDTO();
        gastoDTO.setId(1L);
        gastoDTO.setConcepto("Pago de arriendo");
        gastoDTO.setMonto(new BigDecimal("500.00"));
        gastoDTO.setCategoria("ARRIENDO");

        crearDTO = new CrearGastoDTO();
        crearDTO.setConcepto("Pago de arriendo");
        crearDTO.setMonto(new BigDecimal("500.00"));
        crearDTO.setFecha(LocalDate.now());
        crearDTO.setCategoria("ARRIENDO");
        crearDTO.setMetodoPago("TRANSFERENCIA");
    }

    @Test
    @DisplayName("Debe crear un gasto exitosamente")
    void crear_DatosValidos_CreaGasto() {
        // Arrange
        when(gastoMapper.toEntity(any(CrearGastoDTO.class))).thenReturn(gasto);
        when(gastoRepository.save(any(Gasto.class))).thenReturn(gasto);
        when(gastoMapper.toDTO(any(Gasto.class))).thenReturn(gastoDTO);

        // Act
        GastoResponseDTO result = gastoService.crear(crearDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Pago de arriendo", result.getConcepto());
        verify(gastoRepository).save(any(Gasto.class));
    }

    @Test
    @DisplayName("Debe obtener gasto por ID")
    void getById_IdExiste_RetornaGasto() {
        // Arrange
        when(gastoRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(gasto));
        when(gastoMapper.toDTO(gasto)).thenReturn(gastoDTO);

        // Act
        GastoResponseDTO result = gastoService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción si gasto no existe")
    void getById_IdNoExiste_LanzaExcepcion() {
        // Arrange
        when(gastoRepository.findByIdAndNotDeleted(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            gastoService.getById(99L);
        });
    }

    @Test
    @DisplayName("Debe actualizar gasto exitosamente")
    void actualizar_DatosValidos_ActualizaGasto() {
        // Arrange
        when(gastoRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(gasto));
        when(gastoRepository.save(any(Gasto.class))).thenReturn(gasto);
        when(gastoMapper.toDTO(any(Gasto.class))).thenReturn(gastoDTO);

        // Act
        GastoResponseDTO result = gastoService.actualizar(1L, crearDTO);

        // Assert
        assertNotNull(result);
        verify(gastoRepository).save(any(Gasto.class));
    }

    @Test
    @DisplayName("Debe eliminar gasto (soft delete)")
    void eliminar_IdExiste_EliminaGasto() {
        // Arrange
        when(gastoRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(gasto));

        // Act
        gastoService.eliminar(1L);

        // Assert
        assertTrue(gasto.getDeleted());
        verify(gastoRepository).save(gasto);
    }

    @Test
    @DisplayName("Debe listar gastos con paginación")
    void getAll_ConPaginacion_RetornaLista() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Gasto> gastosPage = new PageImpl<>(List.of(gasto));

        when(gastoRepository.findAllNotDeleted(pageable)).thenReturn(gastosPage);
        when(gastoMapper.toDTO(any(Gasto.class))).thenReturn(gastoDTO);

        // Act
        Page<GastoResponseDTO> result = gastoService.getAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Debe obtener gastos por categoría")
    void getByCategoria_Existe_RetornaLista() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Gasto> gastosPage = new PageImpl<>(List.of(gasto));

        when(gastoRepository.findByCategoria(any(), any(Pageable.class)))
                .thenReturn(gastosPage);
        when(gastoMapper.toDTO(any(Gasto.class))).thenReturn(gastoDTO);

        // Act
        Page<GastoResponseDTO> result = gastoService.getByCategoria("ARRIENDO", pageable);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Debe obtener gastos por rango de fechas")
    void getByFechas_RangoValido_RetornaLista() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fin = LocalDate.now();

        when(gastoRepository.findByFechaBetweenAndNotDeleted(inicio, fin))
                .thenReturn(List.of(gasto));
        when(gastoMapper.toDTO(any(Gasto.class))).thenReturn(gastoDTO);

        // Act
        List<GastoResponseDTO> result = gastoService.getByFechas(inicio, fin);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Debe calcular total de gastos en período")
    void getTotalGastos_PeriodoValido_RetornaTotal() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fin = LocalDate.now();

        when(gastoRepository.sumMontoByFechaBetween(inicio, fin))
                .thenReturn(new BigDecimal("1500.00"));

        // Act
        BigDecimal result = gastoService.getTotalGastos(inicio, fin);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1500.00"), result);
    }

    @Test
    @DisplayName("Debe obtener categorías disponibles")
    void getCategorias_Existen_RetornaLista() {
        // Act
        List<String> result = gastoService.getCategorias();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("ARRIENDO"));
        assertTrue(result.contains("SERVICIOS_PUBLICOS"));
    }
}
