package com.tesig.service;

import java.io.ByteArrayOutputStream;

/**
 * Interface para el servicio de generación de códigos QR.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo genera códigos QR
 * - Dependency Inversion: Clientes dependen de esta abstracción
 */
public interface IQRCodeService {

    /**
     * Genera un código QR como array de bytes (imagen PNG).
     *
     * @param contenido Contenido del QR (URL, texto, etc.)
     * @param ancho Ancho de la imagen en píxeles
     * @param alto Alto de la imagen en píxeles
     * @return Array de bytes de la imagen PNG
     */
    byte[] generarQRCode(String contenido, int ancho, int alto);

    /**
     * Genera un código QR con tamaño por defecto (300x300).
     *
     * @param contenido Contenido del QR
     * @return Array de bytes de la imagen PNG
     */
    byte[] generarQRCode(String contenido);

    /**
     * Genera un código QR como OutputStream.
     *
     * @param contenido Contenido del QR
     * @param ancho Ancho de la imagen
     * @param alto Alto de la imagen
     * @param outputStream Stream de salida
     */
    void generarQRCode(String contenido, int ancho, int alto, ByteArrayOutputStream outputStream);
}
