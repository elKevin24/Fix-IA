package com.tesig;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test principal para verificar que la aplicación carga correctamente.
 */
@SpringBootTest
@ActiveProfiles("test")
class TesigBackendApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring se carga sin errores
    }
}
