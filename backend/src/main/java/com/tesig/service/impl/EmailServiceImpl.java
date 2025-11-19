package com.tesig.service.impl;

import com.tesig.model.Ticket;
import com.tesig.service.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Implementación del servicio de envío de emails.
 *
 * Utiliza:
 * - JavaMailSender: Para envío de emails SMTP
 * - Thymeleaf: Para renderizar templates HTML
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo envía emails
 * - Dependency Inversion: Depende de JavaMailSender (abstracción)
 * - Open/Closed: Fácil agregar nuevos templates
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from:taller@tesig.com}")
    private String emailFrom;

    @Value("${spring.mail.from-name:Taller TESIG}")
    private String emailFromName;

    @Value("${tesig.app.name:TESIG}")
    private String appName;

    @Value("${tesig.app.public-url:http://localhost:3000}")
    private String publicUrl;

    @Value("${tesig.app.ticket-consultation-path:/consulta}")
    private String consultationPath;

    @Value("${tesig.notifications.email.enabled:true}")
    private boolean emailEnabled;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void enviarEmailTicketCreado(Ticket ticket) {
        if (!emailEnabled) {
            log.info("Email deshabilitado. No se envía notificación de ticket creado: {}", ticket.getNumeroTicket());
            return;
        }

        if (ticket.getCliente().getEmail() == null || ticket.getCliente().getEmail().isBlank()) {
            log.warn("Cliente del ticket {} no tiene email configurado", ticket.getNumeroTicket());
            return;
        }

        try {
            Context context = new Context();
            context.setVariable("nombreCliente", ticket.getCliente().getNombreCompleto());
            context.setVariable("numeroTicket", ticket.getNumeroTicket());
            context.setVariable("tipoEquipo", ticket.getTipoEquipo());
            context.setVariable("marca", ticket.getMarca());
            context.setVariable("modelo", ticket.getModelo() != null ? ticket.getModelo() : "N/A");
            context.setVariable("fechaIngreso", ticket.getCreatedAt().format(DATE_FORMATTER));
            context.setVariable("fallaReportada", ticket.getFallaReportada());
            context.setVariable("urlConsulta", buildConsultationUrl(ticket.getNumeroTicket()));
            context.setVariable("appName", appName);

            String contenido = templateEngine.process("email/ticket-creado", context);

            enviarEmail(
                    ticket.getCliente().getEmail(),
                    "Ticket " + ticket.getNumeroTicket() + " - Equipo recibido",
                    contenido
            );

            log.info("Email de ticket creado enviado a: {} para ticket: {}",
                    ticket.getCliente().getEmail(),
                    ticket.getNumeroTicket());

        } catch (Exception e) {
            log.error("Error al enviar email de ticket creado para ticket: {}", ticket.getNumeroTicket(), e);
        }
    }

    @Override
    public void enviarEmailPresupuestoDisponible(Ticket ticket) {
        if (!emailEnabled) {
            log.info("Email deshabilitado. No se envía notificación de presupuesto para: {}", ticket.getNumeroTicket());
            return;
        }

        if (ticket.getCliente().getEmail() == null || ticket.getCliente().getEmail().isBlank()) {
            log.warn("Cliente del ticket {} no tiene email configurado", ticket.getNumeroTicket());
            return;
        }

        try {
            Context context = new Context();
            context.setVariable("nombreCliente", ticket.getCliente().getNombreCompleto());
            context.setVariable("numeroTicket", ticket.getNumeroTicket());
            context.setVariable("tipoEquipo", ticket.getTipoEquipo());
            context.setVariable("diagnostico", ticket.getDiagnostico());
            context.setVariable("presupuestoManoObra", formatCurrency(ticket.getPresupuestoManoObra()));
            context.setVariable("presupuestoPiezas", formatCurrency(ticket.getPresupuestoPiezas()));
            context.setVariable("presupuestoTotal", formatCurrency(ticket.getPresupuestoTotal()));
            context.setVariable("tiempoEstimado", ticket.getTiempoEstimadoDias() + " días");
            context.setVariable("urlConsulta", buildConsultationUrl(ticket.getNumeroTicket()));
            context.setVariable("appName", appName);

            String contenido = templateEngine.process("email/presupuesto-disponible", context);

            enviarEmail(
                    ticket.getCliente().getEmail(),
                    "Presupuesto disponible - Ticket " + ticket.getNumeroTicket(),
                    contenido
            );

            log.info("Email de presupuesto disponible enviado a: {} para ticket: {}",
                    ticket.getCliente().getEmail(),
                    ticket.getNumeroTicket());

        } catch (Exception e) {
            log.error("Error al enviar email de presupuesto para ticket: {}", ticket.getNumeroTicket(), e);
        }
    }

    @Override
    public void enviarEmailListoParaEntrega(Ticket ticket) {
        if (!emailEnabled) {
            log.info("Email deshabilitado. No se envía notificación de listo para entrega: {}", ticket.getNumeroTicket());
            return;
        }

        if (ticket.getCliente().getEmail() == null || ticket.getCliente().getEmail().isBlank()) {
            log.warn("Cliente del ticket {} no tiene email configurado", ticket.getNumeroTicket());
            return;
        }

        try {
            Context context = new Context();
            context.setVariable("nombreCliente", ticket.getCliente().getNombreCompleto());
            context.setVariable("numeroTicket", ticket.getNumeroTicket());
            context.setVariable("tipoEquipo", ticket.getTipoEquipo());
            context.setVariable("marca", ticket.getMarca());
            context.setVariable("presupuestoTotal", formatCurrency(ticket.getPresupuestoTotal()));
            context.setVariable("urlConsulta", buildConsultationUrl(ticket.getNumeroTicket()));
            context.setVariable("appName", appName);

            String contenido = templateEngine.process("email/listo-para-entrega", context);

            enviarEmail(
                    ticket.getCliente().getEmail(),
                    "¡Su equipo está listo! - Ticket " + ticket.getNumeroTicket(),
                    contenido
            );

            log.info("Email de listo para entrega enviado a: {} para ticket: {}",
                    ticket.getCliente().getEmail(),
                    ticket.getNumeroTicket());

        } catch (Exception e) {
            log.error("Error al enviar email de listo para entrega para ticket: {}", ticket.getNumeroTicket(), e);
        }
    }

    @Override
    public void enviarEmailRecordatorioRecogida(Ticket ticket, int diasEspera) {
        if (!emailEnabled) {
            log.info("Email deshabilitado. No se envía recordatorio para: {}", ticket.getNumeroTicket());
            return;
        }

        if (ticket.getCliente().getEmail() == null || ticket.getCliente().getEmail().isBlank()) {
            log.warn("Cliente del ticket {} no tiene email configurado", ticket.getNumeroTicket());
            return;
        }

        try {
            Context context = new Context();
            context.setVariable("nombreCliente", ticket.getCliente().getNombreCompleto());
            context.setVariable("numeroTicket", ticket.getNumeroTicket());
            context.setVariable("tipoEquipo", ticket.getTipoEquipo());
            context.setVariable("diasEspera", diasEspera);
            context.setVariable("urlConsulta", buildConsultationUrl(ticket.getNumeroTicket()));
            context.setVariable("appName", appName);

            String contenido = templateEngine.process("email/recordatorio-recogida", context);

            enviarEmail(
                    ticket.getCliente().getEmail(),
                    "Recordatorio: Su equipo está esperando - Ticket " + ticket.getNumeroTicket(),
                    contenido
            );

            log.info("Email de recordatorio de recogida enviado a: {} para ticket: {} ({} días esperando)",
                    ticket.getCliente().getEmail(),
                    ticket.getNumeroTicket(),
                    diasEspera);

        } catch (Exception e) {
            log.error("Error al enviar email de recordatorio para ticket: {}", ticket.getNumeroTicket(), e);
        }
    }

    @Override
    public void enviarEmail(String destinatario, String asunto, String contenido) {
        if (!emailEnabled) {
            log.info("Email deshabilitado. No se envía email a: {}", destinatario);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(emailFrom, emailFromName);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenido, true); // true = es HTML

            mailSender.send(message);

            log.debug("Email enviado exitosamente a: {} con asunto: {}", destinatario, asunto);

        } catch (MessagingException e) {
            log.error("Error al enviar email a: {} con asunto: {}", destinatario, asunto, e);
            throw new RuntimeException("Error al enviar email", e);
        } catch (Exception e) {
            log.error("Error inesperado al enviar email a: {}", destinatario, e);
            throw new RuntimeException("Error inesperado al enviar email", e);
        }
    }

    @Override
    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    /**
     * Construye la URL completa para consultar un ticket.
     *
     * @param numeroTicket Número del ticket
     * @return URL completa (ej: http://localhost:3000/consulta/TKT-001)
     */
    private String buildConsultationUrl(String numeroTicket) {
        return publicUrl + consultationPath + "/" + numeroTicket;
    }

    /**
     * Formatea un BigDecimal como moneda.
     *
     * @param amount Monto
     * @return String formateado (ej: "$500.00")
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return String.format("$%.2f", amount);
    }
}
