package com.tesig.service.impl;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.tesig.model.Ticket;
import com.tesig.service.IPDFService;
import com.tesig.service.IQRCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Implementación del servicio de generación de PDFs.
 *
 * Utiliza iText 8 para crear documentos PDF profesionales.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo genera documentos PDF
 * - Dependency Inversion: Depende de IQRCodeService (abstracción)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PDFServiceImpl implements IPDFService {

    private final IQRCodeService qrCodeService;

    @Value("${tesig.app.name:TESIG}")
    private String appName;

    @Value("${tesig.app.public-url:http://localhost:3000}")
    private String publicUrl;

    @Value("${tesig.app.ticket-consultation-path:/consulta}")
    private String consultationPath;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DeviceRgb COLOR_PRIMARY = new DeviceRgb(76, 175, 80); // Verde
    private static final DeviceRgb COLOR_SECONDARY = new DeviceRgb(33, 33, 33); // Gris oscuro

    @Override
    public byte[] generarTicketPDF(Ticket ticket) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Márgenes del documento
            document.setMargins(40, 40, 40, 40);

            // === ENCABEZADO ===
            agregarEncabezado(document);

            // === NÚMERO DE TICKET DESTACADO ===
            agregarNumeroTicket(document, ticket);

            // === CÓDIGO QR ===
            agregarCodigoQR(document, ticket);

            // === INFORMACIÓN DEL CLIENTE ===
            agregarInformacionCliente(document, ticket);

            // === INFORMACIÓN DEL EQUIPO ===
            agregarInformacionEquipo(document, ticket);

            // === INSTRUCCIONES ===
            agregarInstrucciones(document, ticket);

            // === FOOTER ===
            agregarFooter(document);

            document.close();
            log.info("PDF del ticket {} generado exitosamente", ticket.getNumeroTicket());

            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar PDF del ticket: {}", ticket.getNumeroTicket(), e);
            throw new RuntimeException("Error al generar PDF del ticket", e);
        }
    }

    @Override
    public byte[] generarPresupuestoPDF(Ticket ticket) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.setMargins(40, 40, 40, 40);

            // Encabezado
            agregarEncabezado(document);

            // Título
            Paragraph titulo = new Paragraph("PRESUPUESTO")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(COLOR_PRIMARY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(titulo);

            // Número de ticket
            Paragraph numeroTicket = new Paragraph("Ticket: " + ticket.getNumeroTicket())
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(numeroTicket);

            // Información del cliente y equipo
            agregarInformacionCliente(document, ticket);
            agregarInformacionEquipo(document, ticket);

            // Diagnóstico
            if (ticket.getDiagnostico() != null) {
                Paragraph diagTitulo = new Paragraph("DIAGNÓSTICO")
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(COLOR_PRIMARY)
                        .setMarginTop(15)
                        .setMarginBottom(5);
                document.add(diagTitulo);

                Paragraph diagnostico = new Paragraph(ticket.getDiagnostico())
                        .setFontSize(11)
                        .setMarginBottom(15);
                document.add(diagnostico);
            }

            // Presupuesto
            if (ticket.getPresupuestoTotal() != null) {
                Paragraph presTitulo = new Paragraph("PRESUPUESTO")
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(COLOR_PRIMARY)
                        .setMarginTop(15)
                        .setMarginBottom(10);
                document.add(presTitulo);

                Table presupuestoTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                        .useAllAvailableWidth();

                // Fila de encabezado
                presupuestoTable.addHeaderCell(createCell("Concepto", true));
                presupuestoTable.addHeaderCell(createCell("Monto", true));

                // Mano de obra
                if (ticket.getPresupuestoManoObra() != null) {
                    presupuestoTable.addCell(createCell("Mano de Obra", false));
                    presupuestoTable.addCell(createCell(formatCurrency(ticket.getPresupuestoManoObra()), false));
                }

                // Piezas
                if (ticket.getPresupuestoPiezas() != null) {
                    presupuestoTable.addCell(createCell("Piezas y Repuestos", false));
                    presupuestoTable.addCell(createCell(formatCurrency(ticket.getPresupuestoPiezas()), false));
                }

                // Total
                Cell totalLabelCell = createCell("TOTAL", false)
                        .setBold()
                        .setFontSize(14)
                        .setBackgroundColor(new DeviceRgb(240, 240, 240));
                Cell totalValueCell = createCell(formatCurrency(ticket.getPresupuestoTotal()), false)
                        .setBold()
                        .setFontSize(14)
                        .setBackgroundColor(new DeviceRgb(240, 240, 240));

                presupuestoTable.addCell(totalLabelCell);
                presupuestoTable.addCell(totalValueCell);

                document.add(presupuestoTable);

                // Tiempo estimado
                if (ticket.getTiempoEstimadoDias() != null) {
                    Paragraph tiempo = new Paragraph("Tiempo estimado de reparación: " + ticket.getTiempoEstimadoDias() + " días")
                            .setFontSize(11)
                            .setItalic()
                            .setMarginTop(10);
                    document.add(tiempo);
                }
            }

            agregarFooter(document);
            document.close();

            log.info("PDF de presupuesto del ticket {} generado exitosamente", ticket.getNumeroTicket());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar PDF de presupuesto del ticket: {}", ticket.getNumeroTicket(), e);
            throw new RuntimeException("Error al generar PDF de presupuesto", e);
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void agregarEncabezado(Document document) {
        Paragraph header = new Paragraph(appName)
                .setFontSize(20)
                .setBold()
                .setFontColor(COLOR_PRIMARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(header);

        Paragraph subHeader = new Paragraph("Taller Electrónico - Sistema Integral de Gestión")
                .setFontSize(10)
                .setFontColor(COLOR_SECONDARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subHeader);
    }

    private void agregarNumeroTicket(Document document, Ticket ticket) {
        // Cuadro con el número de ticket
        Table ticketTable = new Table(1)
                .useAllAvailableWidth()
                .setMarginBottom(20);

        Cell ticketCell = new Cell()
                .add(new Paragraph("NÚMERO DE TICKET")
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(5))
                .add(new Paragraph(ticket.getNumeroTicket())
                        .setFontSize(22)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(COLOR_PRIMARY)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(15)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER);

        ticketTable.addCell(ticketCell);
        document.add(ticketTable);

        // Advertencia
        Paragraph advertencia = new Paragraph("⚠ IMPORTANTE: Guarde este número. Lo necesitará para consultar el estado y recoger su equipo.")
                .setFontSize(10)
                .setItalic()
                .setBackgroundColor(new DeviceRgb(255, 243, 205))
                .setPadding(10)
                .setBorder(new SolidBorder(new DeviceRgb(255, 193, 7), 1))
                .setMarginBottom(20);
        document.add(advertencia);
    }

    private void agregarCodigoQR(Document document, Ticket ticket) {
        try {
            // Generar URL de consulta
            String urlConsulta = publicUrl + consultationPath + "/" + ticket.getNumeroTicket();

            // Generar código QR
            byte[] qrCodeBytes = qrCodeService.generarQRCode(urlConsulta, 200, 200);

            // Agregar imagen QR al documento
            Image qrImage = new Image(ImageDataFactory.create(qrCodeBytes))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
                    .setMarginBottom(10);

            document.add(qrImage);

            Paragraph qrLabel = new Paragraph("Escanee para consultar el estado")
                    .setFontSize(10)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(qrLabel);

        } catch (Exception e) {
            log.warn("No se pudo agregar código QR al PDF del ticket {}", ticket.getNumeroTicket(), e);
            // Continuar sin el QR code
        }
    }

    private void agregarInformacionCliente(Document document, Ticket ticket) {
        Paragraph titulo = new Paragraph("INFORMACIÓN DEL CLIENTE")
                .setFontSize(14)
                .setBold()
                .setFontColor(COLOR_PRIMARY)
                .setMarginBottom(10);
        document.add(titulo);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        table.addCell(createCell("Nombre:", false));
        table.addCell(createCell(ticket.getCliente().getNombreCompleto(), false));

        table.addCell(createCell("Teléfono:", false));
        table.addCell(createCell(ticket.getCliente().getTelefono(), false));

        if (ticket.getCliente().getEmail() != null && !ticket.getCliente().getEmail().isBlank()) {
            table.addCell(createCell("Email:", false));
            table.addCell(createCell(ticket.getCliente().getEmail(), false));
        }

        document.add(table);
    }

    private void agregarInformacionEquipo(Document document, Ticket ticket) {
        Paragraph titulo = new Paragraph("INFORMACIÓN DEL EQUIPO")
                .setFontSize(14)
                .setBold()
                .setFontColor(COLOR_PRIMARY)
                .setMarginBottom(10);
        document.add(titulo);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        table.addCell(createCell("Tipo:", false));
        table.addCell(createCell(ticket.getTipoEquipo(), false));

        table.addCell(createCell("Marca:", false));
        table.addCell(createCell(ticket.getMarca(), false));

        if (ticket.getModelo() != null && !ticket.getModelo().isBlank()) {
            table.addCell(createCell("Modelo:", false));
            table.addCell(createCell(ticket.getModelo(), false));
        }

        if (ticket.getNumeroSerie() != null && !ticket.getNumeroSerie().isBlank()) {
            table.addCell(createCell("Número de Serie:", false));
            table.addCell(createCell(ticket.getNumeroSerie(), false));
        }

        table.addCell(createCell("Falla Reportada:", false));
        table.addCell(createCell(ticket.getFallaReportada(), false));

        if (ticket.getAccesorios() != null && !ticket.getAccesorios().isBlank()) {
            table.addCell(createCell("Accesorios:", false));
            table.addCell(createCell(ticket.getAccesorios(), false));
        }

        table.addCell(createCell("Fecha de Ingreso:", false));
        table.addCell(createCell(ticket.getCreatedAt().format(DATE_FORMATTER), false));

        document.add(table);
    }

    private void agregarInstrucciones(Document document, Ticket ticket) {
        Paragraph titulo = new Paragraph("CÓMO CONSULTAR EL ESTADO")
                .setFontSize(14)
                .setBold()
                .setFontColor(COLOR_PRIMARY)
                .setMarginTop(10)
                .setMarginBottom(10);
        document.add(titulo);

        String url = publicUrl + consultationPath;

        Paragraph instrucciones = new Paragraph()
                .setFontSize(10)
                .add("1. Visite: ")
                .add(new Paragraph(url).setItalic().setFontColor(ColorConstants.BLUE))
                .add("\n2. Ingrese su número de ticket: " + ticket.getNumeroTicket())
                .add("\n3. O escanee el código QR de arriba con su teléfono")
                .setMarginBottom(15);
        document.add(instrucciones);
    }

    private void agregarFooter(Document document) {
        Paragraph footer = new Paragraph("──────────────────────────────────────────")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20)
                .setMarginBottom(5);
        document.add(footer);

        Paragraph footerText = new Paragraph()
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .add("Gracias por confiar en " + appName + "\n")
                .add("Garantía de 30 días sobre todas nuestras reparaciones");
        document.add(footerText);
    }

    private Cell createCell(String content, boolean isHeader) {
        Paragraph paragraph = new Paragraph(content)
                .setFontSize(isHeader ? 11 : 10);

        Cell cell = new Cell().add(paragraph)
                .setPadding(8)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));

        if (isHeader) {
            cell.setBackgroundColor(COLOR_PRIMARY)
                    .setFontColor(ColorConstants.WHITE)
                    .setBold();
        }

        return cell;
    }

    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return String.format("$%.2f", amount);
    }
}
