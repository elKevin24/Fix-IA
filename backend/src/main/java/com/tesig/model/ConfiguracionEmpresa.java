package com.tesig.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Configuración de la empresa para personalización del sistema.
 * Permite escalabilidad multi-taller con códigos únicos por empresa/sucursal.
 *
 * @author TESIG System
 */
@Entity
@Table(name = "configuracion_empresa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== INFORMACIÓN DE LA EMPRESA ====================

    /**
     * Nombre comercial de la empresa
     */
    @Column(name = "nombre_empresa", nullable = false, length = 200)
    private String nombreEmpresa;

    /**
     * Razón social (nombre legal)
     */
    @Column(name = "razon_social", length = 300)
    private String razonSocial;

    /**
     * RUC/NIT/RFC de la empresa
     */
    @Column(name = "identificacion_fiscal", length = 50)
    private String identificacionFiscal;

    /**
     * Código único de empresa para tickets (3-5 caracteres)
     * Ejemplo: TES (TESIG), REP (Reparaciones Express)
     */
    @Column(name = "codigo_empresa", nullable = false, unique = true, length = 5)
    private String codigoEmpresa;

    /**
     * Código de sucursal (2-4 caracteres)
     * Ejemplo: MAT (Matriz), SUC1 (Sucursal 1), NOR (Norte)
     */
    @Column(name = "codigo_sucursal", nullable = false, length = 4)
    private String codigoSucursal;

    /**
     * Nombre de la sucursal
     */
    @Column(name = "nombre_sucursal", length = 100)
    private String nombreSucursal;

    // ==================== INFORMACIÓN DE CONTACTO ====================

    @Column(length = 500)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 100)
    private String provincia;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(length = 50)
    private String pais;

    @Column(name = "telefono_principal", length = 30)
    private String telefonoPrincipal;

    @Column(name = "telefono_secundario", length = 30)
    private String telefonoSecundario;

    @Column(name = "email_contacto", length = 200)
    private String emailContacto;

    @Column(name = "email_soporte", length = 200)
    private String emailSoporte;

    @Column(name = "sitio_web", length = 300)
    private String sitioWeb;

    // ==================== HORARIO DE ATENCIÓN ====================

    @Column(name = "horario_atencion", length = 200)
    private String horarioAtencion;

    @Column(name = "dias_laborales", length = 100)
    private String diasLaborales;

    // ==================== CONFIGURACIÓN DE TICKETS ====================

    /**
     * Prefijo adicional para tickets (opcional)
     * Permite personalizar aún más el formato
     */
    @Column(name = "prefijo_ticket", length = 10)
    private String prefijoTicket;

    /**
     * Longitud del número secuencial (por defecto 4)
     */
    @Column(name = "longitud_secuencia")
    @Builder.Default
    private Integer longitudSecuencia = 4;

    /**
     * Tiempo de garantía por defecto en días
     */
    @Column(name = "dias_garantia_default")
    @Builder.Default
    private Integer diasGarantiaDefault = 30;

    /**
     * Porcentaje de IVA/IVA aplicable
     */
    @Column(name = "porcentaje_impuesto")
    @Builder.Default
    private Double porcentajeImpuesto = 12.0;

    /**
     * Moneda por defecto (USD, EUR, etc)
     */
    @Column(name = "moneda", length = 3)
    @Builder.Default
    private String moneda = "USD";

    // ==================== TEXTOS PERSONALIZADOS ====================

    /**
     * Mensaje de bienvenida para tickets impresos
     */
    @Column(name = "mensaje_bienvenida", length = 500)
    private String mensajeBienvenida;

    /**
     * Términos y condiciones resumidos para tickets
     */
    @Column(name = "terminos_condiciones", columnDefinition = "TEXT")
    private String terminosCondiciones;

    /**
     * Mensaje de agradecimiento para comprobantes de entrega
     */
    @Column(name = "mensaje_agradecimiento", length = 500)
    private String mensajeAgradecimiento;

    // ==================== REDES SOCIALES ====================

    @Column(name = "facebook_url", length = 300)
    private String facebookUrl;

    @Column(name = "instagram_url", length = 300)
    private String instagramUrl;

    @Column(name = "whatsapp", length = 30)
    private String whatsapp;

    // ==================== LOGO E IMÁGENES ====================

    /**
     * Ruta o URL del logo de la empresa
     */
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    /**
     * Logo en formato Base64 para PDFs
     */
    @Column(name = "logo_base64", columnDefinition = "TEXT")
    private String logoBase64;

    // ==================== AUDITORÍA ====================

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "activo")
    @Builder.Default
    private Boolean activo = true;

    // ==================== MÉTODOS DE UTILIDAD ====================

    /**
     * Genera el prefijo completo para tickets.
     * Formato: {EMPRESA}-{SUCURSAL}
     * Ejemplo: TES-MAT
     */
    public String getPrefijoCompleto() {
        StringBuilder prefijo = new StringBuilder();
        prefijo.append(codigoEmpresa.toUpperCase());
        prefijo.append("-");
        prefijo.append(codigoSucursal.toUpperCase());

        if (prefijoTicket != null && !prefijoTicket.isBlank()) {
            prefijo.append("-");
            prefijo.append(prefijoTicket.toUpperCase());
        }

        return prefijo.toString();
    }

    /**
     * Obtiene la dirección completa formateada
     */
    public String getDireccionCompleta() {
        StringBuilder dir = new StringBuilder();

        if (direccion != null) dir.append(direccion);
        if (ciudad != null) {
            if (dir.length() > 0) dir.append(", ");
            dir.append(ciudad);
        }
        if (provincia != null) {
            if (dir.length() > 0) dir.append(", ");
            dir.append(provincia);
        }
        if (codigoPostal != null) {
            if (dir.length() > 0) dir.append(" ");
            dir.append(codigoPostal);
        }
        if (pais != null) {
            if (dir.length() > 0) dir.append(", ");
            dir.append(pais);
        }

        return dir.toString();
    }
}
