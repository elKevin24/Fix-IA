package com.tesig.util;

import com.tesig.model.ConfiguracionEmpresa;
import com.tesig.repository.ConfiguracionEmpresaRepository;
import com.tesig.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para NumeroTicketGenerator.
 */
@ExtendWith(MockitoExtension.class)
class NumeroTicketGeneratorTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ConfiguracionEmpresaRepository configuracionRepository;

    @InjectMocks
    private NumeroTicketGenerator generator;

    private ConfiguracionEmpresa configuracion;

    @BeforeEach
    void setUp() {
        configuracion = ConfiguracionEmpresa.builder()
                .codigoEmpresa("TES")
                .codigoSucursal("MAT")
                .longitudSecuencia(4)
                .build();
    }

    @Test
    @DisplayName("Debe generar número de ticket con formato correcto usando configuración")
    void generate_ConConfiguracion_FormatoCorrecto() {
        // Arrange
        when(configuracionRepository.findFirstActiveConfiguration())
                .thenReturn(Optional.of(configuracion));
        when(ticketRepository.existsByNumeroTicket(anyString()))
                .thenReturn(false);

        // Act
        String numeroTicket = generator.generate();

        // Assert
        assertNotNull(numeroTicket);
        assertTrue(numeroTicket.startsWith("TES-MAT-"));

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertTrue(numeroTicket.contains(fecha));
        assertTrue(numeroTicket.matches("TES-MAT-\\d{8}-\\d{4}"));
    }

    @Test
    @DisplayName("Debe usar valores por defecto cuando no hay configuración")
    void generate_SinConfiguracion_UsaDefaults() {
        // Arrange
        when(configuracionRepository.findFirstActiveConfiguration())
                .thenReturn(Optional.empty());
        when(ticketRepository.existsByNumeroTicket(anyString()))
                .thenReturn(false);

        // Act
        String numeroTicket = generator.generate();

        // Assert
        assertNotNull(numeroTicket);
        assertTrue(numeroTicket.startsWith("TES-MAT-"));
    }

    @Test
    @DisplayName("Debe incrementar secuencia cuando existe duplicado")
    void generate_ConDuplicado_IncrementaSecuencia() {
        // Arrange
        when(configuracionRepository.findFirstActiveConfiguration())
                .thenReturn(Optional.of(configuracion));
        when(ticketRepository.existsByNumeroTicket(anyString()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        // Act
        String numeroTicket = generator.generate();

        // Assert
        assertNotNull(numeroTicket);
        assertTrue(numeroTicket.endsWith("-0003"));
    }

    @Test
    @DisplayName("Debe validar formato correcto de ticket antiguo")
    void isValidFormat_FormatoAntiguo_True() {
        // Act & Assert
        assertTrue(generator.isValidFormat("TKT-20251118-0001"));
    }

    @Test
    @DisplayName("Debe validar formato correcto de ticket nuevo")
    void isValidFormat_FormatoNuevo_True() {
        // Act & Assert
        assertTrue(generator.isValidFormat("TES-MAT-20251118-0001"));
        assertTrue(generator.isValidFormat("ABC-XY-20251118-0001"));
    }

    @Test
    @DisplayName("Debe rechazar formato inválido")
    void isValidFormat_FormatoInvalido_False() {
        // Act & Assert
        assertFalse(generator.isValidFormat(null));
        assertFalse(generator.isValidFormat(""));
        assertFalse(generator.isValidFormat("INVALID"));
        assertFalse(generator.isValidFormat("TKT-2025-0001"));
        assertFalse(generator.isValidFormat("TKT-20251118"));
    }

    @Test
    @DisplayName("Debe extraer fecha correctamente del número de ticket")
    void extractDate_FormatoCorrecto_ExtraeFecha() {
        // Act
        String fecha = generator.extractDate("TES-MAT-20251118-0001");

        // Assert
        assertEquals("20251118", fecha);
    }

    @Test
    @DisplayName("Debe extraer fecha de formato antiguo")
    void extractDate_FormatoAntiguo_ExtraeFecha() {
        // Act
        String fecha = generator.extractDate("TKT-20251118-0001");

        // Assert
        assertEquals("20251118", fecha);
    }

    @Test
    @DisplayName("Debe lanzar excepción con formato inválido al extraer fecha")
    void extractDate_FormatoInvalido_LanzaExcepcion() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            generator.extractDate("INVALID");
        });
    }

    @Test
    @DisplayName("Debe extraer código de empresa correctamente")
    void extractCodigoEmpresa_FormatoCorrecto_ExtraeCodigo() {
        // Act
        String codigo = generator.extractCodigoEmpresa("TES-MAT-20251118-0001");

        // Assert
        assertEquals("TES", codigo);
    }

    @Test
    @DisplayName("Debe extraer código de sucursal correctamente")
    void extractCodigoSucursal_FormatoCorrecto_ExtraeCodigo() {
        // Act
        String codigo = generator.extractCodigoSucursal("TES-MAT-20251118-0001");

        // Assert
        assertEquals("MAT", codigo);
    }

    @Test
    @DisplayName("Debe formatear para visualización correctamente")
    void formatForDisplay_NumeroTicket_FormateaConEspacios() {
        // Act
        String formatted = generator.formatForDisplay("TES-MAT-20251118-0001");

        // Assert
        assertEquals("TES MAT 20251118 0001", formatted);
    }

    @Test
    @DisplayName("Debe manejar null en formatForDisplay")
    void formatForDisplay_Null_RetornaVacio() {
        // Act
        String formatted = generator.formatForDisplay(null);

        // Assert
        assertEquals("", formatted);
    }
}
