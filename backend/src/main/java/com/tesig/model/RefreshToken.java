package com.tesig.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad para almacenar refresh tokens.
 * Permite invalidar tokens en logout y controlar sesiones activas.
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_token", columnList = "token"),
    @Index(name = "idx_refresh_token_usuario", columnList = "usuario_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Transient
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }

    @Transient
    public boolean isValid() {
        return activo && !isExpired() && !isDeleted();
    }

    public void invalidate() {
        this.activo = false;
    }
}
