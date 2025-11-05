1. Nombre del sistema

TESIG – Taller Electrónico Sistema Integral de Gestión

2. Objetivo general

Diseñar un sistema integral para gestionar las operaciones de un taller electrónico, desde la recepción de equipos hasta la entrega final al cliente, incluyendo diagnóstico, presupuestos, reparaciones, control de inventario, compras y generación de reportes administrativos y técnicos.

3. Alcance del sistema
3.1 Procesos incluidos

Recepción de equipos:

Registro de cliente, equipo y falla reportada.

Captura de accesorios y estado físico con fotografías.

Generación automática de ticket con identificador único.

Diagnóstico técnico:

Asignación de técnico.

Registro de diagnóstico y estimación de costos.

Comunicación automática con el cliente para aprobación.

Aprobación de presupuesto:

Cliente acepta o rechaza el servicio.

Registro de decisión con validación digital.

Gestión de reparación:

Ejecución de trabajos.

Consumo y control de piezas en inventario.

Registro de observaciones y seguimiento del progreso.

Inventario y compras:

Control de entradas, salidas y reservas de piezas.

Alertas de bajo stock.

Generación de órdenes de compra y gestión de proveedores.

Entrega de equipos:

Validación de funcionamiento.

Generación de reporte de reparación y factura.

Firma digital o física de conformidad del cliente.

Cancelación:

Registro de cancelación voluntaria o automática.

Emisión de justificativo y cierre del ticket.

Reportes:

Reportes internos de gestión, productividad e ingresos.

Reportes externos para el cliente con detalles del servicio.

Generación de PDF con código QR para verificación.

3.2 Procesos fuera de alcance inicial

Integración con sistemas contables o ERP externos.

Facturación electrónica oficial.

Gestión de garantías extendidas o contratos de mantenimiento preventivo.

4. Usuarios del sistema
Rol	Funciones principales
Recepcionista	Registrar clientes y equipos, crear tickets, coordinar entregas.
Técnico	Realizar diagnóstico, registrar reparaciones, actualizar estados.
Administrador	Configurar usuarios, revisar reportes, gestionar inventario y proveedores.
Cliente (externo)	Consultar el estado del equipo, aprobar presupuestos y revisar reportes.
5. Requisitos generales
5.1 Funcionales

CRUD completo de clientes, equipos, tickets, inventario, proveedores y usuarios.

Control de flujo de servicio por estados definidos.

Comunicación automática con el cliente (correo o WhatsApp).

Generación de reportes y comprobantes en PDF.

Control de acceso basado en roles.

Auditoría de cambios y bitácora de estados.

5.2 No funcionales

Backend en Java Spring Boot 3+.

Base de datos PostgreSQL.

API REST documentada con Swagger/OpenAPI.

Autenticación mediante JWT.

Frontend web responsivo (React o Vue).

Despliegue en contenedores Docker.

Logs centralizados y backups automáticos.

6. Entradas y salidas principales
Entrada	Origen	Descripción
Datos del cliente y equipo	Recepcionista	Información de ingreso.
Diagnóstico técnico	Técnico	Descripción de falla y presupuesto.
Respuesta del cliente	Cliente	Aprobación o rechazo del servicio.
Movimientos de inventario	Técnico o Admin	Registro de consumo o compra de piezas.
Salida	Destinatario	Descripción
Ticket de recepción	Cliente	Confirmación del ingreso del equipo.
Presupuesto	Cliente	Estimación de costos y tiempos.
Reporte de reparación	Cliente	Detalle de los trabajos realizados.
Reportes de gestión	Administrador	Indicadores y métricas del taller.
7. Flujo operativo del sistema
Etapas principales

Ingreso del equipo:

El recepcionista registra cliente, equipo, accesorios y falla reportada.

Se genera un ticket con estado inicial INGRESADO.

Diagnóstico:

El técnico asignado cambia el estado a EN_DIAGNOSTICO.

Se registra el diagnóstico y el costo estimado.

El sistema notifica al cliente para aprobación.

Aprobación:

El cliente responde:

Si aprueba → el estado pasa a APROBADO.

Si rechaza → el ticket pasa a CANCELADO.

Reparación:

Estado EN_REPARACION.

Registro de piezas utilizadas y observaciones.

Si se requiere material → se reserva o se genera compra.

Pruebas y entrega:

Estado EN_PRUEBA y luego LISTO_ENTREGA.

Se genera el reporte final y se notifica al cliente.

Al entregar → estado ENTREGADO.

Cancelación:

Puede ocurrir en cualquier etapa previa a la entrega.

Se documenta causa y se cierra el ticket.

8. Estados del ticket

INGRESADO → EN_DIAGNOSTICO → PRESUPUESTADO → APROBADO → EN_REPARACION → EN_PRUEBA → LISTO_ENTREGA → ENTREGADO
(Cualquier estado puede derivar a CANCELADO)

9. Entregables de la fase de diseño

Documento de alcance (este).

Diagrama de flujo del proceso.

Modelo entidad-relación lógico.

Diagrama de arquitectura del sistema.

Definición modular del backend y frontend.

Borrador de endpoints REST.

Plantillas base de reportes PDF.
