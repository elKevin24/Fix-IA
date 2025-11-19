package com.tesig.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.tesig.service.IQRCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio de generación de códigos QR.
 *
 * Utiliza la librería ZXing (Zebra Crossing) de Google.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo genera códigos QR
 */
@Service
@Slf4j
public class QRCodeServiceImpl implements IQRCodeService {

    private static final int DEFAULT_SIZE = 300;
    private static final String IMAGE_FORMAT = "PNG";

    @Override
    public byte[] generarQRCode(String contenido, int ancho, int alto) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            generarQRCode(contenido, ancho, alto, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error al generar código QR para contenido: {}", contenido, e);
            throw new RuntimeException("Error al generar código QR", e);
        }
    }

    @Override
    public byte[] generarQRCode(String contenido) {
        return generarQRCode(contenido, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    @Override
    public void generarQRCode(String contenido, int ancho, int alto, ByteArrayOutputStream outputStream) {
        try {
            // Configurar hints para el QR Code
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Máxima corrección de errores
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1); // Margen alrededor del QR

            // Generar matriz de bits
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    contenido,
                    BarcodeFormat.QR_CODE,
                    ancho,
                    alto,
                    hints
            );

            // Escribir la imagen al output stream
            MatrixToImageWriter.writeToStream(bitMatrix, IMAGE_FORMAT, outputStream);

            log.debug("Código QR generado exitosamente: {}x{} píxeles", ancho, alto);

        } catch (WriterException e) {
            log.error("Error de ZXing al generar código QR", e);
            throw new RuntimeException("Error al codificar el código QR", e);
        } catch (IOException e) {
            log.error("Error de I/O al generar código QR", e);
            throw new RuntimeException("Error al escribir el código QR", e);
        }
    }
}
