package com.tesig.service;

import com.tesig.dto.cliente.ClienteCreateDTO;
import com.tesig.dto.cliente.ClienteDTO;
import com.tesig.dto.cliente.ClienteUpdateDTO;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.mapper.ClienteMapper;
import com.tesig.model.Cliente;
import com.tesig.repository.ClienteRepository;
import com.tesig.service.impl.ClienteServiceImpl;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ClienteService.
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private ClienteDTO clienteDTO;
    private ClienteCreateDTO createDTO;
    private ClienteUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setTelefono("5512345678");
        cliente.setEmail("juan@email.com");
        cliente.setDireccion("Calle 123");
        cliente.setCreatedAt(LocalDateTime.now());
        cliente.setUpdatedAt(LocalDateTime.now());

        clienteDTO = new ClienteDTO();
        clienteDTO.setId(1L);
        clienteDTO.setNombre("Juan");
        clienteDTO.setApellido("Pérez");
        clienteDTO.setTelefono("5512345678");
        clienteDTO.setEmail("juan@email.com");

        createDTO = new ClienteCreateDTO();
        createDTO.setNombre("Juan");
        createDTO.setApellido("Pérez");
        createDTO.setTelefono("5512345678");
        createDTO.setEmail("juan@email.com");

        updateDTO = new ClienteUpdateDTO();
        updateDTO.setNombre("Juan Carlos");
        updateDTO.setApellido("Pérez García");
        updateDTO.setTelefono("5512345678");
    }

    @Test
    @DisplayName("Debe crear un cliente exitosamente")
    void create_DatosValidos_CreaCliente() {
        // Arrange
        when(clienteRepository.existsByTelefono(anyString())).thenReturn(false);
        when(clienteMapper.toEntity(any(ClienteCreateDTO.class))).thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toDTO(any(Cliente.class))).thenReturn(clienteDTO);

        // Act
        ClienteDTO result = clienteService.create(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Juan", result.getNombre());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si teléfono ya existe")
    void create_TelefonoDuplicado_LanzaExcepcion() {
        // Arrange
        when(clienteRepository.existsByTelefono(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            clienteService.create(createDTO);
        });
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe obtener cliente por ID")
    void findById_IdExiste_RetornaCliente() {
        // Arrange
        when(clienteRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toDTO(cliente)).thenReturn(clienteDTO);

        // Act
        ClienteDTO result = clienteService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción si cliente no existe")
    void findById_IdNoExiste_LanzaExcepcion() {
        // Arrange
        when(clienteRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.findById(99L);
        });
    }

    @Test
    @DisplayName("Debe actualizar cliente exitosamente")
    void update_DatosValidos_ActualizaCliente() {
        // Arrange
        when(clienteRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toDTO(any(Cliente.class))).thenReturn(clienteDTO);

        // Act
        ClienteDTO result = clienteService.update(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe eliminar cliente (soft delete)")
    void delete_IdExiste_EliminaCliente() {
        // Arrange
        when(clienteRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(cliente));

        // Act
        clienteService.delete(1L);

        // Assert
        assertNotNull(cliente.getDeletedAt());
        verify(clienteRepository).save(cliente);
    }

    @Test
    @DisplayName("Debe listar todos los clientes con paginación")
    void findAll_ConPaginacion_RetornaLista() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> clientesPage = new PageImpl<>(List.of(cliente));

        when(clienteRepository.findAllByDeletedAtIsNull(pageable)).thenReturn(clientesPage);
        when(clienteMapper.toDTO(any(Cliente.class))).thenReturn(clienteDTO);

        // Act
        var result = clienteService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Debe buscar clientes por nombre")
    void search_PorNombre_RetornaResultados() {
        // Arrange
        when(clienteRepository.searchByNombreOrApellidoOrTelefono(anyString()))
                .thenReturn(List.of(cliente));
        when(clienteMapper.toDTO(any(Cliente.class))).thenReturn(clienteDTO);

        // Act
        List<ClienteDTO> result = clienteService.search("Juan");

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Debe obtener cliente por teléfono")
    void findByTelefono_Existe_RetornaCliente() {
        // Arrange
        when(clienteRepository.findByTelefonoAndDeletedAtIsNull(anyString()))
                .thenReturn(Optional.of(cliente));
        when(clienteMapper.toDTO(cliente)).thenReturn(clienteDTO);

        // Act
        ClienteDTO result = clienteService.findByTelefono("5512345678");

        // Assert
        assertNotNull(result);
        assertEquals("5512345678", result.getTelefono());
    }
}
