package com.tesig.service.impl;

import com.tesig.dto.ReporteFinancieroDTO;
import com.tesig.model.*;
import com.tesig.repository.*;
import com.tesig.service.IReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements IReporteService {

    private final TicketRepository ticketRepository;
    private final CompraRepository compraRepository;
    private final GastoRepository gastoRepository;
    private final PiezaRepository piezaRepository;

    @Override
    public ReporteFinancieroDTO generarReporteFinanciero(LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener tickets entregados en el período
        List<Ticket> ticketsEntregados = ticketRepository.findAll().stream()
                .filter(t -> t.getEstado() == EstadoTicket.ENTREGADO)
                .filter(t -> t.getFechaEntrega() != null)
                .filter(t -> !t.getFechaEntrega().toLocalDate().isBefore(fechaInicio))
                .filter(t -> !t.getFechaEntrega().toLocalDate().isAfter(fechaFin))
                .collect(Collectors.toList());

        // Calcular ingresos
        BigDecimal ingresosManoObra = ticketsEntregados.stream()
                .map(t -> t.getPresupuestoManoObra() != null ? t.getPresupuestoManoObra() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ingresosPiezas = ticketsEntregados.stream()
                .map(t -> t.getPresupuestoPiezas() != null ? t.getPresupuestoPiezas() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuentosTotales = ticketsEntregados.stream()
                .map(t -> t.getDescuentoMonto() != null ? t.getDescuentoMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ingresosTotales = ingresosManoObra.add(ingresosPiezas).subtract(descuentosTotales);

        // Calcular gastos de compras
        BigDecimal gastosCompras = compraRepository.sumTotalByFechaAndEstado(
                fechaInicio, fechaFin, Compra.EstadoCompra.RECIBIDA);
        if (gastosCompras == null) gastosCompras = BigDecimal.ZERO;

        // Calcular gastos operativos
        BigDecimal gastosOperativos = gastoRepository.sumMontoByFechaBetween(fechaInicio, fechaFin);
        if (gastosOperativos == null) gastosOperativos = BigDecimal.ZERO;

        BigDecimal gastosTotales = gastosCompras.add(gastosOperativos);

        // Gastos por categoría
        Map<String, BigDecimal> gastosPorCategoria = new HashMap<>();
        for (Gasto.CategoriaGasto categoria : Gasto.CategoriaGasto.values()) {
            BigDecimal montoCategoria = gastoRepository.sumMontoByFechaAndCategoria(
                    fechaInicio, fechaFin, categoria);
            if (montoCategoria != null && montoCategoria.compareTo(BigDecimal.ZERO) > 0) {
                gastosPorCategoria.put(categoria.name(), montoCategoria);
            }
        }

        // Calcular utilidad
        BigDecimal utilidadBruta = ingresosTotales.subtract(gastosCompras);
        BigDecimal utilidadNeta = ingresosTotales.subtract(gastosTotales);
        BigDecimal margenUtilidad = BigDecimal.ZERO;
        if (ingresosTotales.compareTo(BigDecimal.ZERO) > 0) {
            margenUtilidad = utilidadNeta.multiply(BigDecimal.valueOf(100))
                    .divide(ingresosTotales, 2, RoundingMode.HALF_UP);
        }

        // Calcular ticket promedio
        BigDecimal ticketPromedio = BigDecimal.ZERO;
        if (!ticketsEntregados.isEmpty()) {
            ticketPromedio = ingresosTotales.divide(
                    BigDecimal.valueOf(ticketsEntregados.size()), 2, RoundingMode.HALF_UP);
        }

        // Valor de inventario
        BigDecimal valorInventario = piezaRepository.findAll().stream()
                .filter(p -> !p.getDeleted())
                .map(p -> p.getPrecioCosto().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Piezas con problemas de stock
        long piezasSinStock = piezaRepository.findAll().stream()
                .filter(p -> !p.getDeleted())
                .filter(p -> p.getStock() == 0)
                .count();

        long piezasStockBajo = piezaRepository.findAll().stream()
                .filter(p -> !p.getDeleted())
                .filter(p -> p.getStock() > 0 && p.getStock() <= p.getStockMinimo())
                .count();

        // Tendencias mensuales (últimos 6 meses)
        List<ReporteFinancieroDTO.ResumenMensual> tendencias = calcularTendenciasMensuales(fechaInicio, fechaFin);

        return ReporteFinancieroDTO.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .ingresosTotales(ingresosTotales)
                .ingresosManoObra(ingresosManoObra)
                .ingresosPiezas(ingresosPiezas)
                .ticketsCompletados(ticketsEntregados.size())
                .ticketPromedio(ticketPromedio)
                .gastosTotales(gastosTotales)
                .gastosCompras(gastosCompras)
                .gastosOperativos(gastosOperativos)
                .gastosPorCategoria(gastosPorCategoria)
                .utilidadBruta(utilidadBruta)
                .utilidadNeta(utilidadNeta)
                .margenUtilidad(margenUtilidad)
                .valorInventario(valorInventario)
                .piezasSinStock((int) piezasSinStock)
                .piezasStockBajo((int) piezasStockBajo)
                .tendenciaMensual(tendencias)
                .build();
    }

    @Override
    public ReporteFinancieroDTO getReporteMesActual() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        return generarReporteFinanciero(inicioMes, finMes);
    }

    @Override
    public ReporteFinancieroDTO getReporteAnual() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioAnio = hoy.withDayOfYear(1);
        LocalDate finAnio = hoy.withDayOfYear(hoy.lengthOfYear());
        return generarReporteFinanciero(inicioAnio, finAnio);
    }

    @Override
    public ReporteFinancieroDTO getReporteComparativo(LocalDate fechaInicio, LocalDate fechaFin) {
        return generarReporteFinanciero(fechaInicio, fechaFin);
    }

    private List<ReporteFinancieroDTO.ResumenMensual> calcularTendenciasMensuales(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ReporteFinancieroDTO.ResumenMensual> tendencias = new ArrayList<>();

        YearMonth mesActual = YearMonth.from(fechaInicio);
        YearMonth mesFin = YearMonth.from(fechaFin);

        while (!mesActual.isAfter(mesFin)) {
            LocalDate inicioMes = mesActual.atDay(1);
            LocalDate finMes = mesActual.atEndOfMonth();

            // Ingresos del mes
            BigDecimal ingresosMes = ticketRepository.findAll().stream()
                    .filter(t -> t.getEstado() == EstadoTicket.ENTREGADO)
                    .filter(t -> t.getFechaEntrega() != null)
                    .filter(t -> !t.getFechaEntrega().toLocalDate().isBefore(inicioMes))
                    .filter(t -> !t.getFechaEntrega().toLocalDate().isAfter(finMes))
                    .map(Ticket::getTotalFinal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Gastos del mes
            BigDecimal gastosComprasMes = compraRepository.sumTotalByFechaAndEstado(
                    inicioMes, finMes, Compra.EstadoCompra.RECIBIDA);
            if (gastosComprasMes == null) gastosComprasMes = BigDecimal.ZERO;

            BigDecimal gastosOpMes = gastoRepository.sumMontoByFechaBetween(inicioMes, finMes);
            if (gastosOpMes == null) gastosOpMes = BigDecimal.ZERO;

            BigDecimal gastosMes = gastosComprasMes.add(gastosOpMes);

            // Tickets del mes
            int ticketsMes = (int) ticketRepository.findAll().stream()
                    .filter(t -> t.getEstado() == EstadoTicket.ENTREGADO)
                    .filter(t -> t.getFechaEntrega() != null)
                    .filter(t -> !t.getFechaEntrega().toLocalDate().isBefore(inicioMes))
                    .filter(t -> !t.getFechaEntrega().toLocalDate().isAfter(finMes))
                    .count();

            tendencias.add(ReporteFinancieroDTO.ResumenMensual.builder()
                    .mes(mesActual.format(DateTimeFormatter.ofPattern("MMM yyyy")))
                    .ingresos(ingresosMes)
                    .gastos(gastosMes)
                    .utilidad(ingresosMes.subtract(gastosMes))
                    .tickets(ticketsMes)
                    .build());

            mesActual = mesActual.plusMonths(1);
        }

        return tendencias;
    }
}
