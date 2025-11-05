-- Script de inicialización con datos de prueba para TESIG
-- Este script se ejecuta automáticamente al iniciar la aplicación

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
