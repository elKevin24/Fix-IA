package com.tesig.service.impl;

import com.tesig.dto.PaginatedResponseDTO;
import com.tesig.dto.TicketConsultaPublicaDTO;
import com.tesig.dto.cliente.ClienteCreateDTO;
import com.tesig.dto.cliente.ClienteDTO;
import com.tesig.dto.cliente.ClienteUpdateDTO;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.mapper.ClienteMapper;
import com.tesig.mapper.TicketMapper;
import com.tesig.model.Cliente;
import com.tesig.model.Ticket;
import com.tesig.repository.ClienteRepository;
import com.tesig.repository.TicketRepository;
import com.tesig.service.IClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de clientes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final TicketRepository ticketRepository;
    private final ClienteMapper clienteMapper;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<ClienteDTO> findAll(Pageable pageable) {
        log.info("Buscando todos los clientes - Página: {}, Tamaño: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Cliente> page = clienteRepository.findAll(pageable);

        List<ClienteDTO> content = page.getContent().stream()
                .filter(cliente -> !cliente.isDeleted())
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());

        return PaginatedResponseDTO.<ClienteDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO findById(Long id) {
        log.info("Buscando cliente por ID: {}", id);

        Cliente cliente = getClienteById(id);
        return clienteMapper.toDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteDTO create(ClienteCreateDTO createDTO) {
        log.info("Creando nuevo cliente: {} {}", createDTO.getNombre(), createDTO.getApellido());

        // Validar que el teléfono no exista
        validateTelefonoNotExists(createDTO.getTelefono(), null);

        // Si tiene email, validar que no exista
        if (createDTO.getEmail() != null && !createDTO.getEmail().isBlank()) {
            validateEmailNotExists(createDTO.getEmail(), null);
        }

        Cliente cliente = clienteMapper.toEntity(createDTO);
        Cliente savedCliente = clienteRepository.save(cliente);

        log.info("Cliente creado exitosamente con ID: {}", savedCliente.getId());
        return clienteMapper.toDTO(savedCliente);
    }

    @Override
    @Transactional
    public ClienteDTO update(Long id, ClienteUpdateDTO updateDTO) {
        log.info("Actualizando cliente ID: {}", id);

        Cliente cliente = getClienteById(id);

        // Validar teléfono si se está actualizando
        if (updateDTO.getTelefono() != null) {
            validateTelefonoNotExists(updateDTO.getTelefono(), id);
        }

        // Validar email si se está actualizando
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isBlank()) {
            validateEmailNotExists(updateDTO.getEmail(), id);
        }

        // Actualizar campos
        clienteMapper.updateEntityFromDTO(updateDTO, cliente);

        Cliente updatedCliente = clienteRepository.save(cliente);

        log.info("Cliente ID: {} actualizado exitosamente", id);
        return clienteMapper.toDTO(updatedCliente);
    }

    @Override
    @Transactional
    public ClienteDTO partialUpdate(Long id, ClienteUpdateDTO updateDTO) {
        // La implementación es la misma que update ya que el mapper
        // solo actualiza campos no nulos gracias a @BeanMapping
        return update(id, updateDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando cliente ID: {}", id);

        Cliente cliente = getClienteById(id);

        // Verificar que no tenga tickets activos
        long ticketsActivos = ticketRepository.findByClienteId(id).stream()
                .filter(ticket -> !ticket.getEstado().name().equals("ENTREGADO")
                        && !ticket.getEstado().name().equals("CANCELADO"))
                .count();

        if (ticketsActivos > 0) {
            log.warn("Intento de eliminar cliente con tickets activos - ID: {}", id);
            throw new IllegalStateException(
                    "No se puede eliminar el cliente porque tiene " + ticketsActivos +
                            " ticket(s) activo(s). Complete o cancele los tickets primero."
            );
        }

        // Soft delete
        cliente.softDelete();
        clienteRepository.save(cliente);

        log.info("Cliente ID: {} eliminado exitosamente (soft delete)", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketConsultaPublicaDTO> getTicketsByCliente(Long id) {
        log.info("Obteniendo tickets del cliente ID: {}", id);

        // Verificar que el cliente existe
        getClienteById(id);

        List<Ticket> tickets = ticketRepository.findByClienteId(id);

        return tickets.stream()
                .map(ticketMapper::toConsultaPublicaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> search(String query) {
        log.info("Buscando clientes con query: {}", query);

        List<Cliente> clientes = clienteRepository.buscarClientes(query);

        return clientes.stream()
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTelefono(String telefono, Long excludeId) {
        Cliente existente = clienteRepository.findByTelefono(telefono).orElse(null);

        if (existente == null) {
            return false;
        }

        // Si estamos excluyendo un ID (para updates), verificar que no sea el mismo
        if (excludeId != null && existente.getId().equals(excludeId)) {
            return false;
        }

        return !existente.isDeleted();
    }

    // Métodos privados de ayuda

    private Cliente getClienteById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cliente no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Cliente", "id", id);
                });

        if (cliente.isDeleted()) {
            log.error("Intento de acceso a cliente eliminado - ID: {}", id);
            throw new ResourceNotFoundException("Cliente", "id", id);
        }

        return cliente;
    }

    private void validateTelefonoNotExists(String telefono, Long excludeId) {
        if (existsByTelefono(telefono, excludeId)) {
            log.warn("Intento de usar teléfono duplicado: {}", telefono);
            throw new IllegalArgumentException(
                    "Ya existe un cliente registrado con el teléfono: " + telefono
            );
        }
    }

    private void validateEmailNotExists(String email, Long excludeId) {
        Cliente existente = clienteRepository.findByEmail(email).orElse(null);

        if (existente != null && !existente.isDeleted()) {
            // Si estamos excluyendo un ID (para updates), verificar que no sea el mismo
            if (excludeId == null || !existente.getId().equals(excludeId)) {
                log.warn("Intento de usar email duplicado: {}", email);
                throw new IllegalArgumentException(
                        "Ya existe un cliente registrado con el email: " + email
                );
            }
        }
    }
}
