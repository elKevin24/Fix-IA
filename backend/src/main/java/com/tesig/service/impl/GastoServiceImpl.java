package com.tesig.service.impl;

import com.tesig.dto.gasto.CrearGastoDTO;
import com.tesig.dto.gasto.GastoResponseDTO;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.mapper.GastoMapper;
import com.tesig.model.Gasto;
import com.tesig.repository.GastoRepository;
import com.tesig.service.IGastoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GastoServiceImpl implements IGastoService {

    private final GastoRepository gastoRepository;
    private final GastoMapper gastoMapper;

    @Override
    public GastoResponseDTO crear(CrearGastoDTO dto) {
        Gasto gasto = gastoMapper.toEntity(dto);
        Gasto saved = gastoRepository.save(gasto);
        return gastoMapper.toDTO(saved);
    }

    @Override
    public GastoResponseDTO actualizar(Long id, CrearGastoDTO dto) {
        Gasto gasto = gastoRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado: " + id));

        gastoMapper.updateEntity(dto, gasto);
        Gasto saved = gastoRepository.save(gasto);
        return gastoMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public GastoResponseDTO getById(Long id) {
        Gasto gasto = gastoRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado: " + id));
        return gastoMapper.toDTO(gasto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GastoResponseDTO> getAll(Pageable pageable) {
        return gastoRepository.findAllNotDeleted(pageable)
                .map(gastoMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GastoResponseDTO> getByCategoria(String categoria, Pageable pageable) {
        Gasto.CategoriaGasto categoriaEnum = Gasto.CategoriaGasto.valueOf(categoria);
        return gastoRepository.findByCategoria(categoriaEnum, pageable)
                .map(gastoMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoResponseDTO> getByFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return gastoRepository.findByFechaBetween(fechaInicio, fechaFin)
                .stream()
                .map(gastoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalGastos(LocalDate fechaInicio, LocalDate fechaFin) {
        return gastoRepository.sumMontoByFechaBetween(fechaInicio, fechaFin);
    }

    @Override
    public void eliminar(Long id) {
        Gasto gasto = gastoRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado: " + id));
        gasto.setDeleted(true);
        gastoRepository.save(gasto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getCategorias() {
        return Arrays.stream(Gasto.CategoriaGasto.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
