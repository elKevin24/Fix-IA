package com.tesig.service;

import com.tesig.dto.ticket.*;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.mapper.TicketMapper;
import com.tesig.model.Cliente;
import com.tesig.model.Ticket;
import com.tesig.model.Usuario;
import com.tesig.repository.ClienteRepository;
import com.tesig.repository.TicketRepository;
import com.tesig.repository.UsuarioRepository;
import com.tesig.service.impl.TicketServiceImpl;
import com.tesig.util.NumeroTicketGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para TicketService.
 */
@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private NumeroTicketGenerator numeroTicketGenerator;

    @Mock
    private IEmailService emailService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket ticket;
    private TicketDTO ticketDTO;
    private TicketCreateDTO createDTO;
    private Cliente cliente;
    private Usuario tecnico;
    private Usuario recepcionista;

    @BeforeEach
    void setUp() {
        // Cliente
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setTelefono("5512345678");
        cliente.setEmail("juan@email.com");

        // Técnico
        tecnico = new Usuario();
        tecnico.setId(2L);
        tecnico.setNombre("Carlos");
        tecnico.setApellido("Técnico");
        tecnico.setRol(Usuario.Rol.TECNICO);

        // Recepcionista
        recepcionista = new Usuario();
        recepcionista.setId(3L);
        recepcionista.setNombre("Laura");
        recepcionista.setRol(Usuario.Rol.RECEPCIONISTA);

        // Ticket
        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setNumeroTicket("TES-MAT-20251118-0001");
        ticket.setTipoEquipo("Laptop");
        ticket.setMarca("HP");
        ticket.setModelo("Pavilion 15");
        ticket.setFallaReportada("No enciende");
        ticket.setEstado(Ticket.EstadoTicket.INGRESADO);
        ticket.setCliente(cliente);
        ticket.setCreatedAt(LocalDateTime.now());

        // DTO
        ticketDTO = new TicketDTO();
        ticketDTO.setId(1L);
        ticketDTO.setNumeroTicket("TES-MAT-20251118-0001");
        ticketDTO.setTipoEquipo("Laptop");
        ticketDTO.setMarca("HP");
        ticketDTO.setEstado("INGRESADO");

        // CreateDTO
        createDTO = new TicketCreateDTO();
        createDTO.setClienteId(1L);
        createDTO.setTipoEquipo("Laptop");
        createDTO.setMarca("HP");
        createDTO.setModelo("Pavilion 15");
        createDTO.setFallaReportada("No enciende");
    }

    @Test
    @DisplayName("Debe crear un ticket exitosamente")
    void create_DatosValidos_CreaTicket() {
        // Arrange
        when(clienteRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(cliente));
        when(numeroTicketGenerator.generate()).thenReturn("TES-MAT-20251118-0001");
        when(ticketMapper.toEntity(any(TicketCreateDTO.class))).thenReturn(ticket);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDTO(any(Ticket.class))).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.create(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals("TES-MAT-20251118-0001", result.getNumeroTicket());
        assertEquals("INGRESADO", result.getEstado());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si cliente no existe")
    void create_ClienteNoExiste_LanzaExcepcion() {
        // Arrange
        when(clienteRepository.findByIdAndDeletedAtIsNull(anyLong()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.create(createDTO);
        });
    }

    @Test
    @DisplayName("Debe obtener ticket por ID")
    void findById_IdExiste_RetornaTicket() {
        // Arrange
        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Debe obtener ticket por número")
    void findByNumeroTicket_Existe_RetornaTicket() {
        // Arrange
        when(ticketRepository.findByNumeroTicketAndDeletedAtIsNull(anyString()))
                .thenReturn(Optional.of(ticket));
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.findByNumeroTicket("TES-MAT-20251118-0001");

        // Assert
        assertNotNull(result);
        assertEquals("TES-MAT-20251118-0001", result.getNumeroTicket());
    }

    @Test
    @DisplayName("Debe asignar técnico correctamente")
    void asignarTecnico_TicketIngresado_AsignaTecnico() {
        // Arrange
        AsignarTecnicoDTO asignarDTO = new AsignarTecnicoDTO();
        asignarDTO.setTecnicoId(2L);

        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(tecnico));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDTO(any(Ticket.class))).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.asignarTecnico(1L, asignarDTO);

        // Assert
        assertNotNull(result);
        assertEquals(tecnico, ticket.getTecnicoAsignado());
        assertEquals(Ticket.EstadoTicket.EN_DIAGNOSTICO, ticket.getEstado());
    }

    @Test
    @DisplayName("Debe registrar diagnóstico correctamente")
    void registrarDiagnostico_EnDiagnostico_RegistraDiagnostico() {
        // Arrange
        ticket.setEstado(Ticket.EstadoTicket.EN_DIAGNOSTICO);
        ticket.setTecnicoAsignado(tecnico);

        DiagnosticoDTO diagnosticoDTO = new DiagnosticoDTO();
        diagnosticoDTO.setDiagnostico("Falla en disco duro");
        diagnosticoDTO.setPresupuestoManoObra(new BigDecimal("500.00"));
        diagnosticoDTO.setPresupuestoPiezas(new BigDecimal("300.00"));
        diagnosticoDTO.setTiempoEstimadoDias(3);

        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDTO(any(Ticket.class))).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.registrarDiagnostico(1L, diagnosticoDTO);

        // Assert
        assertNotNull(result);
        assertEquals(Ticket.EstadoTicket.PRESUPUESTADO, ticket.getEstado());
        assertEquals(new BigDecimal("800.00"), ticket.getPresupuestoTotal());
    }

    @Test
    @DisplayName("Debe aprobar presupuesto correctamente")
    void aprobarPresupuesto_Presupuestado_ApruebaPrespuesto() {
        // Arrange
        ticket.setEstado(Ticket.EstadoTicket.PRESUPUESTADO);

        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDTO(any(Ticket.class))).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.aprobarPresupuesto(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Ticket.EstadoTicket.APROBADO, ticket.getEstado());
        assertNotNull(ticket.getFechaRespuestaCliente());
    }

    @Test
    @DisplayName("Debe rechazar presupuesto correctamente")
    void rechazarPresupuesto_Presupuestado_RechazaPresupuesto() {
        // Arrange
        ticket.setEstado(Ticket.EstadoTicket.PRESUPUESTADO);

        RechazarPresupuestoDTO rechazarDTO = new RechazarPresupuestoDTO();
        rechazarDTO.setMotivo("Muy caro");

        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDTO(any(Ticket.class))).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.rechazarPresupuesto(1L, rechazarDTO);

        // Assert
        assertNotNull(result);
        assertEquals(Ticket.EstadoTicket.RECHAZADO, ticket.getEstado());
    }

    @Test
    @DisplayName("Debe iniciar reparación correctamente")
    void iniciarReparacion_Aprobado_IniciaReparacion() {
        // Arrange
        ticket.setEstado(Ticket.EstadoTicket.APROBADO);

        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDTO(any(Ticket.class))).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.iniciarReparacion(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Ticket.EstadoTicket.EN_REPARACION, ticket.getEstado());
        assertNotNull(ticket.getFechaInicioReparacion());
    }

    @Test
    @DisplayName("Debe completar reparación correctamente")
    void completarReparacion_EnReparacion_CompletaReparacion() {
        // Arrange
        ticket.setEstado(Ticket.EstadoTicket.EN_REPARACION);

        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDTO(any(Ticket.class))).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.completarReparacion(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Ticket.EstadoTicket.EN_PRUEBA, ticket.getEstado());
    }

    @Test
    @DisplayName("Debe marcar listo para entrega")
    void marcarListoEntrega_EnPrueba_MarcaListo() {
        // Arrange
        ticket.setEstado(Ticket.EstadoTicket.EN_PRUEBA);

        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDTO(any(Ticket.class))).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.marcarListoEntrega(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Ticket.EstadoTicket.LISTO_ENTREGA, ticket.getEstado());
        assertNotNull(ticket.getFechaFinReparacion());
    }

    @Test
    @DisplayName("Debe entregar equipo correctamente")
    void entregar_ListoEntrega_EntregaEquipo() {
        // Arrange
        ticket.setEstado(Ticket.EstadoTicket.LISTO_ENTREGA);

        EntregaDTO entregaDTO = new EntregaDTO();
        entregaDTO.setObservaciones("Equipo entregado en perfecto estado");

        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDTO(any(Ticket.class))).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.entregar(1L, entregaDTO);

        // Assert
        assertNotNull(result);
        assertEquals(Ticket.EstadoTicket.ENTREGADO, ticket.getEstado());
        assertNotNull(ticket.getFechaEntrega());
    }

    @Test
    @DisplayName("Debe cancelar ticket correctamente")
    void cancelar_NoEntregado_CancelaTicket() {
        // Arrange
        CancelarTicketDTO cancelarDTO = new CancelarTicketDTO();
        cancelarDTO.setMotivo("Cliente desistió");

        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDTO(any(Ticket.class))).thenReturn(ticketDTO);

        // Act
        TicketDTO result = ticketService.cancelar(1L, cancelarDTO);

        // Assert
        assertNotNull(result);
        assertEquals(Ticket.EstadoTicket.CANCELADO, ticket.getEstado());
    }

    @Test
    @DisplayName("No debe cancelar ticket ya entregado")
    void cancelar_YaEntregado_LanzaExcepcion() {
        // Arrange
        ticket.setEstado(Ticket.EstadoTicket.ENTREGADO);
        CancelarTicketDTO cancelarDTO = new CancelarTicketDTO();
        cancelarDTO.setMotivo("Intento de cancelar");

        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            ticketService.cancelar(1L, cancelarDTO);
        });
    }

    @Test
    @DisplayName("Debe validar transición de estado válida")
    void puedeTransicionarA_TransicionValida_RetornaTrue() {
        // Arrange
        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));

        // Act
        boolean puede = ticketService.puedeTransicionarA(1L, "EN_DIAGNOSTICO");

        // Assert
        assertTrue(puede);
    }

    @Test
    @DisplayName("Debe validar transición de estado inválida")
    void puedeTransicionarA_TransicionInvalida_RetornaFalse() {
        // Arrange
        when(ticketRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(ticket));

        // Act
        boolean puede = ticketService.puedeTransicionarA(1L, "ENTREGADO");

        // Assert
        assertFalse(puede);
    }
}
