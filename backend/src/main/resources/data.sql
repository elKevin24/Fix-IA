-- Script de inicialización con datos de prueba para TESIG
-- Este script se ejecuta automáticamente al iniciar la aplicación

-- =============================================================================
-- CONFIGURACIÓN DE LA EMPRESA
-- =============================================================================

INSERT INTO configuracion_empresa (
    nombre_empresa, razon_social, identificacion_fiscal, codigo_empresa, codigo_sucursal,
    nombre_sucursal, direccion, ciudad, provincia, codigo_postal, pais,
    telefono_principal, telefono_secundario, email_contacto, email_soporte, sitio_web,
    horario_atencion, dias_laborales, prefijo_ticket, longitud_secuencia,
    dias_garantia_default, porcentaje_impuesto, moneda,
    mensaje_bienvenida, terminos_condiciones, mensaje_agradecimiento,
    facebook_url, instagram_url, whatsapp, activo, created_at, updated_at
)
VALUES (
    'TESIG - Taller Electrónico',
    'TESIG Servicios Tecnológicos S.A.',
    '1792345678001',
    'TES',
    'MAT',
    'Matriz Principal',
    'Av. Principal 123, Edificio Tech Center',
    'Quito',
    'Pichincha',
    '170150',
    'Ecuador',
    '+593 2 123 4567',
    '+593 99 123 4567',
    'contacto@tesig.com',
    'soporte@tesig.com',
    'https://www.tesig.com',
    'Lunes a Viernes: 9:00 - 18:00, Sábados: 9:00 - 13:00',
    'Lunes a Sábado',
    NULL,
    4,
    30,
    12.0,
    'USD',
    'Bienvenido a TESIG. Su equipo está en las mejores manos.',
    'La garantía cubre defectos en la reparación realizada. No cubre daños por mal uso, accidentes o problemas no relacionados con el trabajo efectuado. Tiempo máximo de permanencia del equipo: 30 días después de notificada la finalización.',
    '¡Gracias por confiar en TESIG! Su satisfacción es nuestra prioridad.',
    'https://facebook.com/tesigec',
    'https://instagram.com/tesig_ec',
    '+593 99 123 4567',
    true,
    NOW(),
    NOW()
)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- INSERTAR PIEZAS/INVENTARIO DE PRUEBA
-- =============================================================================

INSERT INTO piezas (
    codigo, nombre, descripcion, categoria, marca, modelo_compatible,
    precio_costo, precio_venta, stock, stock_minimo, ubicacion, activo,
    created_at, updated_at, deleted_at
)
VALUES
    ('LCD-SAM-15.6', 'Pantalla LCD 15.6" Samsung', 'Pantalla LCD de reemplazo para laptops Samsung de 15.6 pulgadas', 'Pantallas', 'Samsung', 'Series 3, 5', 85.00, 150.00, 5, 2, 'Estante A-1', true, NOW(), NOW(), NULL),
    ('LCD-HP-14', 'Pantalla LCD 14" HP', 'Pantalla LCD HD para laptops HP de 14 pulgadas', 'Pantallas', 'HP', 'Pavilion, ProBook', 75.00, 130.00, 8, 3, 'Estante A-1', true, NOW(), NOW(), NULL),
    ('BAT-MAC-A1466', 'Batería MacBook Air 13"', 'Batería de reemplazo para MacBook Air 13" 2012-2017', 'Baterías', 'Apple', 'MacBook Air A1466', 45.00, 95.00, 10, 4, 'Estante B-2', true, NOW(), NOW(), NULL),
    ('BAT-DELL-E7450', 'Batería Dell Latitude E7450', 'Batería 4 celdas para Dell Latitude E7450/E7440', 'Baterías', 'Dell', 'Latitude E7450, E7440', 55.00, 110.00, 6, 2, 'Estante B-2', true, NOW(), NOW(), NULL),
    ('TEC-LEN-T480', 'Teclado Lenovo ThinkPad T480', 'Teclado español retroiluminado para ThinkPad T480', 'Teclados', 'Lenovo', 'ThinkPad T480, T490', 40.00, 85.00, 4, 2, 'Estante C-1', true, NOW(), NOW(), NULL),
    ('TEC-HP-840G5', 'Teclado HP EliteBook 840 G5', 'Teclado español con trackpoint para EliteBook 840 G5', 'Teclados', 'HP', 'EliteBook 840 G5, G6', 45.00, 90.00, 3, 2, 'Estante C-1', true, NOW(), NOW(), NULL),
    ('RAM-DDR4-8GB', 'Memoria RAM DDR4 8GB', 'Memoria RAM DDR4 2666MHz 8GB SODIMM', 'Memorias', 'Kingston', 'Universal', 25.00, 55.00, 15, 5, 'Estante D-1', true, NOW(), NOW(), NULL),
    ('RAM-DDR4-16GB', 'Memoria RAM DDR4 16GB', 'Memoria RAM DDR4 3200MHz 16GB SODIMM', 'Memorias', 'Crucial', 'Universal', 45.00, 95.00, 8, 3, 'Estante D-1', true, NOW(), NOW(), NULL),
    ('SSD-SAM-500GB', 'SSD Samsung 500GB', 'Disco SSD SATA 500GB Samsung 870 EVO', 'Almacenamiento', 'Samsung', 'Universal', 50.00, 95.00, 12, 4, 'Estante E-1', true, NOW(), NOW(), NULL),
    ('SSD-NVME-1TB', 'SSD NVMe 1TB', 'Disco SSD NVMe M.2 1TB Western Digital', 'Almacenamiento', 'Western Digital', 'Universal NVMe', 75.00, 145.00, 6, 2, 'Estante E-2', true, NOW(), NOW(), NULL),
    ('CARG-USB-C-65W', 'Cargador USB-C 65W', 'Cargador universal USB-C PD 65W', 'Cargadores', 'Genérico', 'Universal USB-C', 18.00, 45.00, 20, 8, 'Estante F-1', true, NOW(), NOW(), NULL),
    ('CARG-MAC-61W', 'Cargador MacBook Pro 61W', 'Cargador USB-C 61W para MacBook Pro 13"', 'Cargadores', 'Apple', 'MacBook Pro 13"', 35.00, 75.00, 5, 2, 'Estante F-1', true, NOW(), NOW(), NULL),
    ('PASTA-TERMICA', 'Pasta Térmica Arctic MX-4', 'Pasta térmica de alto rendimiento 4g', 'Consumibles', 'Arctic', 'Universal', 8.00, 18.00, 25, 10, 'Estante G-1', true, NOW(), NOW(), NULL),
    ('FLEX-PANTALLA', 'Cable Flex Pantalla Universal', 'Cable flex LVDS 40 pines para pantallas de laptop', 'Cables', 'Genérico', 'Universal 40 pines', 5.00, 15.00, 30, 10, 'Estante G-2', true, NOW(), NOW(), NULL),
    ('VEN-CPU-UNIV', 'Ventilador CPU Laptop', 'Ventilador de CPU para laptops, varios modelos', 'Ventiladores', 'Genérico', 'Universal', 12.00, 30.00, 18, 6, 'Estante H-1', true, NOW(), NOW(), NULL)
ON CONFLICT (codigo) DO NOTHING;

-- =============================================================================
-- INSERTAR CLIENTES DE PRUEBA
-- =============================================================================

INSERT INTO clientes (nombre, apellido, telefono, email, direccion, notas, created_at, updated_at, deleted_at)
VALUES
    ('Juan', 'Pérez García', '5512345678', 'juan.perez@email.com', 'Calle Reforma 123, CDMX', 'Cliente frecuente', NOW(), NOW(), NULL),
    ('María', 'López Hernández', '5523456789', 'maria.lopez@email.com', 'Av. Insurgentes 456, CDMX', NULL, NOW(), NOW(), NULL),
    ('Carlos', 'Rodríguez Martínez', '5534567890', 'carlos.rodriguez@email.com', 'Calle Juárez 789, CDMX', 'Cliente VIP', NOW(), NOW(), NULL),
    ('Ana', 'García Sánchez', '5545678901', 'ana.garcia@email.com', 'Av. Universidad 321, CDMX', NULL, NOW(), NOW(), NULL),
    ('Pedro', 'Martínez López', '5556789012', 'pedro.martinez@email.com', 'Calle Hidalgo 654, CDMX', NULL, NOW(), NOW(), NULL)
ON CONFLICT (telefono) DO NOTHING;

-- =============================================================================
-- INSERTAR USUARIOS DEL SISTEMA
-- =============================================================================
-- Password: Admin123! (hash BCrypt)
-- Nota: En producción, estos usuarios deben cambiarse

INSERT INTO usuarios (nombre, apellido, email, password, rol, activo, created_at, updated_at, deleted_at)
VALUES
    ('Administrador', 'Sistema', 'admin@tesig.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1YC.aDo0iEkEL6cOWLgeX2Y9UhZM4WS', 'ADMINISTRADOR', true, NOW(), NOW(), NULL),
    ('Juan Carlos', 'Técnico Principal', 'tecnico1@tesig.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1YC.aDo0iEkEL6cOWLgeX2Y9UhZM4WS', 'TECNICO', true, NOW(), NOW(), NULL),
    ('Roberto', 'Técnico Auxiliar', 'tecnico2@tesig.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1YC.aDo0iEkEL6cOWLgeX2Y9UhZM4WS', 'TECNICO', true, NOW(), NOW(), NULL),
    ('Laura', 'Recepción Principal', 'recepcion@tesig.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1YC.aDo0iEkEL6cOWLgeX2Y9UhZM4WS', 'RECEPCIONISTA', true, NOW(), NOW(), NULL)
ON CONFLICT (email) DO NOTHING;

-- =============================================================================
-- INSERTAR TICKETS DE PRUEBA
-- =============================================================================

INSERT INTO tickets (
    numero_ticket, tipo_equipo, marca, modelo, numero_serie, falla_reportada, accesorios,
    estado, diagnostico, presupuesto_mano_obra, presupuesto_piezas, presupuesto_total,
    tiempo_estimado_dias, fecha_presupuesto, fecha_respuesta_cliente,
    observaciones_reparacion, resultado_pruebas, cliente_id, tecnico_asignado_id,
    usuario_ingreso_id, created_at, updated_at, deleted_at
)
VALUES
    -- Ticket 1: Ingresado
    (
        'TKT-2024-00001',
        'Laptop',
        'HP',
        'Pavilion 15',
        'HP123456789',
        'No enciende, se queda en pantalla negra',
        'Cargador original, mochila',
        'INGRESADO',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        1, -- Cliente Juan Pérez
        NULL, -- Sin técnico asignado
        4, -- Ingresado por recepcionista
        NOW() - INTERVAL '2 hours',
        NOW() - INTERVAL '2 hours',
        NULL
    ),

    -- Ticket 2: En diagnóstico
    (
        'TKT-2024-00002',
        'PC de escritorio',
        'Dell',
        'Optiplex 7090',
        'DELL987654321',
        'Se reinicia constantemente, ventilador hace ruido',
        'Cable de poder, mouse, teclado',
        'EN_DIAGNOSTICO',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        2, -- Cliente María López
        2, -- Técnico Juan Carlos
        4, -- Ingresado por recepcionista
        NOW() - INTERVAL '1 day',
        NOW() - INTERVAL '3 hours',
        NULL
    ),

    -- Ticket 3: Presupuestado
    (
        'TKT-2024-00003',
        'Laptop',
        'Lenovo',
        'ThinkPad X1 Carbon',
        'LEN456789123',
        'Teclado derrama líquido, algunas teclas no funcionan',
        'Cargador original',
        'PRESUPUESTADO',
        'Daño en teclado por derrame de líquido. Requiere reemplazo completo del teclado. Placa madre sin daños aparentes.',
        800.00,
        1200.00,
        2000.00,
        5,
        NOW() - INTERVAL '2 hours',
        NULL,
        NULL,
        NULL,
        3, -- Cliente Carlos Rodríguez
        2, -- Técnico Juan Carlos
        4,
        NOW() - INTERVAL '2 days',
        NOW() - INTERVAL '2 hours',
        NULL
    ),

    -- Ticket 4: En reparación
    (
        'TKT-2024-00004',
        'iMac',
        'Apple',
        'iMac 27" 2019',
        'APPLE789456123',
        'Pantalla con líneas verticales, a veces parpadea',
        'Cable de poder, teclado mágico, mouse',
        'EN_REPARACION',
        'Falla en cable flex de pantalla. Se requiere reemplazo.',
        1500.00,
        2500.00,
        4000.00,
        7,
        NOW() - INTERVAL '5 days',
        NOW() - INTERVAL '4 days',
        'Cable flex reemplazado. Realizando pruebas de estabilidad.',
        NULL,
        4, -- Cliente Ana García
        3, -- Técnico Roberto
        4,
        NOW() - INTERVAL '6 days',
        NOW() - INTERVAL '1 hour',
        NULL
    ),

    -- Ticket 5: Listo para entrega
    (
        'TKT-2024-00005',
        'MacBook Pro',
        'Apple',
        'MacBook Pro 13" 2020',
        'APPLE123789456',
        'Batería se agota rápido, solo dura 1 hora',
        'Cargador MagSafe',
        'LISTO_ENTREGA',
        'Batería con más de 1000 ciclos y capacidad al 65%. Requiere reemplazo.',
        500.00,
        1800.00,
        2300.00,
        3,
        NOW() - INTERVAL '8 days',
        NOW() - INTERVAL '7 days',
        'Batería reemplazada exitosamente. Sistema recalibrado.',
        'Pruebas completadas: batería tiene ahora 100% de capacidad. Duración de 8+ horas confirmada.',
        5, -- Cliente Pedro Martínez
        2, -- Técnico Juan Carlos
        4,
        NOW() - INTERVAL '10 days',
        NOW() - INTERVAL '12 hours',
        NULL
    ),

    -- Ticket 6: Entregado
    (
        'TKT-2024-00006',
        'Laptop',
        'Asus',
        'ROG Strix G15',
        'ASUS789123456',
        'Sobrecalentamiento durante juegos, se apaga',
        'Cargador, mouse gaming',
        'ENTREGADO',
        'Acumulación de polvo en ventiladores y disipadores. Pasta térmica seca.',
        300.00,
        150.00,
        450.00,
        1,
        NOW() - INTERVAL '15 days',
        NOW() - INTERVAL '14 days',
        'Limpieza profunda realizada. Pasta térmica reemplazada. Ventiladores funcionando correctamente.',
        'Pruebas de estrés completadas exitosamente. Temperaturas normales bajo carga.',
        1, -- Cliente Juan Pérez
        3, -- Técnico Roberto
        4,
        NOW() - INTERVAL '16 days',
        NOW() - INTERVAL '2 days',
        NULL
    )
ON CONFLICT (numero_ticket) DO NOTHING;

-- =============================================================================
-- Actualizar fechas de respuesta y entrega para tickets completados
-- =============================================================================

UPDATE tickets
SET fecha_respuesta_cliente = fecha_presupuesto + INTERVAL '1 day'
WHERE numero_ticket IN ('TKT-2024-00004', 'TKT-2024-00005', 'TKT-2024-00006')
  AND fecha_respuesta_cliente IS NULL;

UPDATE tickets
SET fecha_inicio_reparacion = fecha_respuesta_cliente + INTERVAL '2 hours'
WHERE numero_ticket IN ('TKT-2024-00004', 'TKT-2024-00005', 'TKT-2024-00006')
  AND fecha_inicio_reparacion IS NULL;

UPDATE tickets
SET fecha_fin_reparacion = created_at + INTERVAL '8 days'
WHERE numero_ticket IN ('TKT-2024-00005', 'TKT-2024-00006')
  AND fecha_fin_reparacion IS NULL;

UPDATE tickets
SET fecha_entrega = created_at + INTERVAL '14 days',
    observaciones_entrega = 'Cliente satisfecho con el servicio. Equipo entregado en perfecto estado.'
WHERE numero_ticket = 'TKT-2024-00006'
  AND fecha_entrega IS NULL;
