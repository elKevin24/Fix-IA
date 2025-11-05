# 🗺️ ROADMAP TÉCNICO DETALLADO - TESIG

**Proyecto**: Sistema Integral de Gestión para Taller Electrónico
**Versión Actual**: 0.1.0-SNAPSHOT
**Última Actualización**: 2025-11-05

---

## 📊 Estado Actual del Proyecto

```
┌─────────────────────────────────────────────────────────────────┐
│                    PROGRESO GENERAL TESIG                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ✅ FASE 1A: Backend Base              [100%] ████████████████  │
│  🚧 FASE 1B: Backend Completo          [ 0%]  ░░░░░░░░░░░░░░░░  │
│  ⏳ FASE 2: Comunicación & Aprobación  [ 0%]  ░░░░░░░░░░░░░░░░  │
│  ⏳ FASE 3: Inventario                 [ 0%]  ░░░░░░░░░░░░░░░░  │
│  ⏳ FASE 4: Gestión Avanzada           [ 0%]  ░░░░░░░░░░░░░░░░  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## ✅ FASE 1A: Backend Base (COMPLETADO)

### Implementado ✓
- [x] Estructura base Spring Boot 3.2.0
- [x] Configuración PostgreSQL con Docker
- [x] Entidades JPA: Cliente, Ticket, Usuario
- [x] Repositorios con queries personalizadas
- [x] DTOs con MapStruct
- [x] Servicio de consulta pública de tickets
- [x] Endpoint público GET /api/publico/tickets/{numero}
- [x] Spring Security con endpoints públicos
- [x] Swagger/OpenAPI documentación
- [x] Datos de prueba precargados
- [x] Arquitectura SOLID aplicada

**Duración Real**: 1 sesión
**Estado**: ✅ COMPLETADO (2025-11-05)

---

## 🚧 FASE 1B: Completar MVP Backend

**Objetivo**: Sistema funcional completo para operaciones internas del taller

### 1. Sistema de Autenticación JWT

#### Tareas Backend
- [ ] **Auth Controller** (`/api/auth`)
  - [ ] POST `/api/auth/login` - Login con email/password
  - [ ] POST `/api/auth/refresh` - Refresh token
  - [ ] POST `/api/auth/logout` - Invalidar token
  - [ ] GET `/api/auth/me` - Info del usuario autenticado

- [ ] **JWT Service**
  - [ ] Generación de access token (24h)
  - [ ] Generación de refresh token (7 días)
  - [ ] Validación de tokens
  - [ ] Extracción de claims

- [ ] **Security Filter**
  - [ ] JwtAuthenticationFilter
  - [ ] Interceptor para validar token en cada request
  - [ ] Manejo de excepciones de autenticación

- [ ] **User Details Service**
  - [ ] Implementación de UserDetailsService
  - [ ] Carga de usuario por email
  - [ ] Roles y permisos

**Entregables**:
```java
// DTOs
- LoginRequestDTO (email, password)
- LoginResponseDTO (accessToken, refreshToken, user)
- RefreshTokenRequestDTO
- UserInfoDTO

// Services
- IAuthService + AuthServiceImpl
- IJwtService + JwtServiceImpl

// Controllers
- AuthController

// Filters
- JwtAuthenticationFilter
```

**Tiempo Estimado**: 1-2 días

---

### 2. Gestión de Clientes (CRUD Completo)

#### Endpoints
- [ ] **GET** `/api/clientes` - Listar todos (paginado)
  - Query params: `page`, `size`, `sortBy`, `search`

- [ ] **GET** `/api/clientes/{id}` - Obtener por ID

- [ ] **POST** `/api/clientes` - Crear nuevo
  - Validación de teléfono único
  - Validación de email (si existe)

- [ ] **PUT** `/api/clientes/{id}` - Actualizar completo

- [ ] **PATCH** `/api/clientes/{id}` - Actualizar parcial

- [ ] **DELETE** `/api/clientes/{id}` - Soft delete

- [ ] **GET** `/api/clientes/{id}/tickets` - Historial de tickets

- [ ] **GET** `/api/clientes/buscar` - Búsqueda avanzada
  - Por nombre, apellido, teléfono, email

#### Componentes
```java
// DTOs
- ClienteDTO (completo)
- ClienteCreateDTO
- ClienteUpdateDTO
- ClientePaginadoDTO

// Services
- IClienteService + ClienteServiceImpl

// Controllers
- ClienteController

// Mappers
- ClienteMapper (ampliar existente)
```

**Permisos**:
- Crear: `RECEPCIONISTA`, `ADMINISTRADOR`
- Leer: Todos los roles
- Actualizar: `RECEPCIONISTA`, `ADMINISTRADOR`
- Eliminar: Solo `ADMINISTRADOR`

**Tiempo Estimado**: 2 días

---

### 3. Gestión Completa de Tickets

#### Endpoints

**Listado y Búsqueda**
- [ ] **GET** `/api/tickets` - Listar todos (paginado, filtros)
  - Filtros: `estado`, `clienteId`, `tecnicoId`, `fechaDesde`, `fechaHasta`

- [ ] **GET** `/api/tickets/{id}` - Detalle completo

- [ ] **GET** `/api/tickets/tecnico/mis-tickets` - Tickets del técnico logueado

- [ ] **GET** `/api/tickets/estadisticas` - Stats para dashboard
  - Total por estado
  - Tickets por técnico
  - Tiempo promedio por estado

**Creación y Gestión**
- [ ] **POST** `/api/tickets` - Crear nuevo ticket
  - Generar número único automático
  - Validar cliente existe
  - Estado inicial: INGRESADO

- [ ] **PATCH** `/api/tickets/{id}/asignar-tecnico` - Asignar técnico
  - Body: `{ "tecnicoId": 123 }`

- [ ] **PATCH** `/api/tickets/{id}/estado` - Cambiar estado
  - Validar transiciones permitidas
  - Actualizar fechas automáticamente

**Diagnóstico y Presupuesto**
- [ ] **PATCH** `/api/tickets/{id}/diagnostico` - Registrar diagnóstico
  - Body: diagnóstico, presupuestoManoObra, presupuestoPiezas, tiempoEstimado
  - Cambiar estado a PRESUPUESTADO

- [ ] **PATCH** `/api/tickets/{id}/aprobar` - Cliente aprueba (manual en MVP)
  - Cambiar estado a APROBADO

- [ ] **PATCH** `/api/tickets/{id}/rechazar` - Cliente rechaza
  - Body: motivoRechazo
  - Cambiar estado a RECHAZADO → CANCELADO

**Reparación**
- [ ] **PATCH** `/api/tickets/{id}/iniciar-reparacion` - Iniciar
  - Cambiar estado a EN_REPARACION
  - Registrar fechaInicioReparacion

- [ ] **PATCH** `/api/tickets/{id}/observaciones` - Agregar observaciones
  - Durante reparación

- [ ] **PATCH** `/api/tickets/{id}/completar-reparacion` - Completar
  - Cambiar estado a EN_PRUEBA
  - Registrar fechaFinReparacion

**Pruebas y Entrega**
- [ ] **PATCH** `/api/tickets/{id}/pruebas` - Resultado de pruebas
  - Body: resultadoPruebas, exitoso (boolean)
  - Si exitoso: LISTO_ENTREGA
  - Si no: regresa a EN_REPARACION

- [ ] **PATCH** `/api/tickets/{id}/entregar` - Marcar entregado
  - Body: observacionesEntrega
  - Cambiar estado a ENTREGADO
  - Registrar fechaEntrega

**Cancelación**
- [ ] **PATCH** `/api/tickets/{id}/cancelar` - Cancelar ticket
  - Body: motivoCancelacion
  - Desde cualquier estado
  - Cambiar estado a CANCELADO

#### Validaciones de Negocio
```java
// TicketBusinessRules.java
- validarTransicionEstado(estadoActual, estadoNuevo)
- validarTecnicoActivo(tecnicoId)
- validarPresupuestoCompleto(ticket)
- validarClienteExiste(clienteId)
- generarNumeroTicketUnico()
```

#### Componentes
```java
// DTOs
- TicketDTO (completo)
- TicketCreateDTO
- TicketUpdateEstadoDTO
- TicketDiagnosticoDTO
- TicketPaginadoDTO
- TicketEstadisticasDTO

// Services
- ITicketService + TicketServiceImpl
- ITicketEstadoService (validaciones de transiciones)

// Controllers
- TicketController

// Utils
- NumeroTicketGenerator
```

**Tiempo Estimado**: 4-5 días

---

### 4. Dashboard y Reportes Básicos

#### Endpoints
- [ ] **GET** `/api/dashboard/resumen` - Resumen general
  ```json
  {
    "ticketsActivos": 45,
    "ticketsPorEstado": { "INGRESADO": 5, "EN_REPARACION": 12, ... },
    "ticketsPorTecnico": [{ "tecnico": "Juan", "cantidad": 15 }, ...],
    "ultimosIngresos": [...últimos 10 tickets...]
  }
  ```

- [ ] **GET** `/api/dashboard/metricas` - Métricas operativas
  ```json
  {
    "tiempoPromedioReparacion": 3.5, // días
    "tasaAprobacion": 85.5, // %
    "ticketsEsteMes": 120,
    "ticketsMesAnterior": 95
  }
  ```

**Componentes**:
```java
// Services
- IDashboardService + DashboardServiceImpl

// Controllers
- DashboardController
```

**Tiempo Estimado**: 1 día

---

### 5. Testing y Documentación

#### Tests Unitarios
- [ ] Service layer tests
  - ClienteServiceTest
  - TicketServiceTest
  - AuthServiceTest

#### Tests de Integración
- [ ] Controller tests con MockMvc
  - ClienteControllerTest
  - TicketControllerTest
  - AuthControllerTest

#### Documentación
- [ ] Completar JavaDoc en todos los servicios
- [ ] Actualizar Swagger annotations
- [ ] Crear Postman Collection
- [ ] Actualizar README con nuevos endpoints

**Tiempo Estimado**: 2 días

---

## 📅 Cronograma Fase 1B

```
Semana 1:
├─ Lun-Mar: Sistema de Autenticación JWT
├─ Mie-Jue: CRUD Clientes
└─ Vie: Dashboard básico

Semana 2:
├─ Lun-Mie: Gestión de Tickets (creación, estados básicos)
├─ Jue: Gestión de Tickets (diagnóstico, reparación)
└─ Vie: Gestión de Tickets (pruebas, entrega)

Semana 3:
├─ Lun-Mar: Testing completo
├─ Mie: Corrección de bugs
├─ Jue: Documentación
└─ Vie: Deploy y pruebas finales MVP
```

**Duración Estimada Total**: 3 semanas

---

## ⏳ FASE 2: Comunicación y Aprobaciones (2 meses)

### 2.1 Frontend - Portal Cliente

#### Setup Inicial
- [ ] Proyecto Vite + React 18 + TypeScript
- [ ] Tailwind CSS + shadcn/ui
- [ ] React Router 6
- [ ] Zustand para state management
- [ ] Axios para HTTP requests
- [ ] React Hook Form + Zod

#### Páginas Públicas
- [ ] **Página de Consulta**
  - Input para número de ticket
  - Mostrar estado actual
  - Línea de tiempo visual del proceso
  - Información del presupuesto (si existe)

- [ ] **Detalle de Ticket**
  - Información completa del equipo
  - Historial de cambios de estado
  - Diagnóstico (si está disponible)
  - Presupuesto detallado

#### Aprobación Digital
- [ ] **Sistema de Tokens Únicos**
  - Generar token único por presupuesto
  - Link: `https://tesig.com/aprobar/{token}`

- [ ] **Página de Aprobación**
  - Mostrar presupuesto completo
  - Opciones: Aprobar / Rechazar
  - Campo de justificación si rechaza
  - Firma digital o PIN

**Tiempo Estimado**: 3 semanas

---

### 2.2 Sistema de Notificaciones

#### Backend - Email Service
- [ ] Integración con servicio de email (SendGrid, AWS SES, o SMTP)
- [ ] Templates HTML para emails
  - Ticket creado
  - Presupuesto listo
  - Equipo reparado
  - Listo para entrega

- [ ] **INotificacionService**
  ```java
  - enviarTicketCreado(ticket, cliente)
  - enviarPresupuesto(ticket, linkAprobacion)
  - enviarCambioEstado(ticket, estadoAnterior, estadoNuevo)
  - enviarListoEntrega(ticket)
  ```

- [ ] Sistema de cola para envíos asíncronos
- [ ] Registro de notificaciones enviadas

**Tiempo Estimado**: 1 semana

---

### 2.3 Captura de Fotografías

#### Backend
- [ ] Servicio de almacenamiento de archivos
  - Opción 1: Filesystem local
  - Opción 2: AWS S3 / MinIO

- [ ] **Endpoint de upload**
  - POST `/api/tickets/{id}/fotos`
  - Validación de tipo de archivo
  - Resize automático
  - Asociar a ticket

#### Frontend
- [ ] Componente de cámara web
- [ ] Upload de imágenes desde galería
- [ ] Galería de fotos por ticket
- [ ] Comparación antes/después

**Tiempo Estimado**: 1 semana

---

### 2.4 Firma Digital

#### Backend
- [ ] Generar PIN de 6 dígitos para confirma
- [ ] Validación de PIN
- [ ] Registro de fecha y hora de firma
- [ ] Endpoint de confirmación con PIN

#### Frontend
- [ ] Componente de firma con canvas
- [ ] Input de PIN
- [ ] Confirmación visual

**Tiempo Estimado**: 3 días

---

## ⏳ FASE 3: Inventario (2-3 meses)

### 3.1 Catálogo de Piezas

#### Backend - Entidades
```java
// Pieza.java
- codigo: String (unique)
- nombre: String
- descripcion: Text
- categoria: Categoria (enum)
- precioCompra: BigDecimal
- precioVenta: BigDecimal
- stockActual: Integer
- stockMinimo: Integer
- unidadMedida: String
- proveedor: Proveedor
```

#### Endpoints
- [ ] CRUD completo de piezas
- [ ] Búsqueda por código, nombre, categoría
- [ ] Import/Export CSV
- [ ] Generación de códigos de barras

**Tiempo Estimado**: 1 semana

---

### 3.2 Movimientos de Inventario

#### Backend - Entidades
```java
// MovimientoInventario.java
- tipo: TipoMovimiento (ENTRADA, SALIDA, AJUSTE)
- pieza: Pieza
- cantidad: Integer
- precioUnitario: BigDecimal
- ticket: Ticket (opcional)
- usuario: Usuario
- motivo: String
- referencia: String
```

#### Funcionalidades
- [ ] Registro de entradas (compras)
- [ ] Registro de salidas (consumo en reparaciones)
- [ ] Ajustes de inventario
- [ ] Reservas temporales por ticket
- [ ] Validación de stock disponible

**Tiempo Estimado**: 1.5 semanas

---

### 3.3 Alertas y Reportes

#### Sistema de Alertas
- [ ] Detección automática de stock bajo
- [ ] Notificaciones al administrador
- [ ] Lista de piezas a reponer
- [ ] Sugerencias de cantidad a ordenar

#### Reportes
- [ ] Valorización de inventario
- [ ] Movimientos por período
- [ ] Piezas más usadas
- [ ] Análisis de costos por ticket

**Tiempo Estimado**: 1 semana

---

### 3.4 Integración con Tickets

- [ ] Agregar piezas a ticket durante reparación
- [ ] Calcular costo de piezas automáticamente
- [ ] Actualizar stock al completar reparación
- [ ] Historial de piezas usadas por cliente

**Tiempo Estimado**: 1 semana

---

## ⏳ FASE 4: Gestión Avanzada (2 meses)

### 4.1 Gestión de Proveedores

#### Entidades y CRUD
```java
// Proveedor.java
- razonSocial, rfc
- contacto, telefono, email
- direccion, ciudad
- condicionesPago
- tiempoEntrega
```

- [ ] CRUD completo
- [ ] Historial de compras por proveedor
- [ ] Evaluación de proveedores

**Tiempo Estimado**: 3 días

---

### 4.2 Órdenes de Compra

#### Funcionalidades
- [ ] Crear OC desde alertas de stock
- [ ] Enviar OC por email a proveedor
- [ ] Seguimiento de OC (pendiente/recibida)
- [ ] Recepción de mercadería
- [ ] Actualización automática de inventario

**Tiempo Estimado**: 1.5 semanas

---

### 4.3 Reportes PDF Avanzados

#### Backend - PDF Generation
- [ ] Librería: iText 7 o JasperReports
- [ ] Templates de reportes
  - Ticket de recepción con QR
  - Presupuesto detallado
  - Reporte de reparación
  - Comprobante de entrega

- [ ] Endpoints de generación
  - GET `/api/reportes/ticket/{id}/recepcion.pdf`
  - GET `/api/reportes/ticket/{id}/presupuesto.pdf`
  - GET `/api/reportes/ticket/{id}/entrega.pdf`

**Tiempo Estimado**: 1 semana

---

### 4.4 Analytics y KPIs

#### Métricas
- [ ] Tiempo promedio de reparación por tipo de equipo
- [ ] Tasa de aprobación de presupuestos
- [ ] Ingresos por período
- [ ] Productividad por técnico
- [ ] Piezas más rentables
- [ ] Clientes más frecuentes

#### Visualizaciones
- [ ] Dashboard con gráficas (Chart.js)
- [ ] Exportación a Excel
- [ ] Reportes periódicos automáticos

**Tiempo Estimado**: 2 semanas

---

### 4.5 WhatsApp Integration (Opcional)

#### Twilio API
- [ ] Configuración de Twilio
- [ ] Envío de notificaciones por WhatsApp
- [ ] Bot de consulta de estado
- [ ] Templates de mensajes

**Tiempo Estimado**: 1 semana

---

## 🔒 Tareas Transversales (Todas las Fases)

### Seguridad
- [ ] HTTPS obligatorio en producción
- [ ] Rate limiting en endpoints públicos
- [ ] Prevención de SQL injection
- [ ] Validación de inputs
- [ ] Sanitización de outputs
- [ ] CORS configurado correctamente

### Performance
- [ ] Indices de base de datos optimizados
- [ ] Queries con pagination
- [ ] Caching con Redis (opcional)
- [ ] Lazy loading en relaciones JPA
- [ ] Compresión de respuestas HTTP

### DevOps
- [ ] CI/CD con GitHub Actions
- [ ] Docker image del backend
- [ ] Docker Compose para stack completo
- [ ] Backups automáticos de BD
- [ ] Logs centralizados
- [ ] Monitoring con Prometheus/Grafana (opcional)

### Documentación
- [ ] README actualizado
- [ ] API docs siempre actualizadas
- [ ] Manual de usuario
- [ ] Guía de deployment
- [ ] Changelog por versión

---

## 📈 Métricas de Éxito por Fase

### Fase 1 MVP
- [x] 100% de consultas públicas funcionando
- [ ] 100% de tickets gestionados digitalmente
- [ ] Tiempo de registro < 5 minutos
- [ ] Sistema estable 1 semana sin caídas
- [ ] 3+ usuarios concurrentes

### Fase 2
- [ ] 80% aprobaciones digitales
- [ ] 90% notificaciones entregadas
- [ ] Clientes consultan sin llamar

### Fase 3
- [ ] 0 faltantes por falta de control
- [ ] Inventario en tiempo real
- [ ] 50% reducción tiempo de búsqueda

### Fase 4
- [ ] Reportes en < 3 segundos
- [ ] Métricas para decisiones
- [ ] 95% satisfacción usuarios

---

## 🎯 Hitos Importantes

```
✅ 2025-11-05: Backend base con consulta pública (COMPLETADO)
📍 2025-11-26: MVP Backend completo
📍 2025-12-20: Frontend cliente + notificaciones
📍 2026-02-15: Sistema de inventario
📍 2026-04-30: Sistema completo v1.0
```

---

## 📞 Próximos Pasos Inmediatos

### Esta Semana
1. [ ] Implementar sistema de autenticación JWT
2. [ ] Crear CRUD de clientes
3. [ ] Endpoints básicos de gestión de tickets

### Próxima Semana
1. [ ] Completar todos los estados de tickets
2. [ ] Implementar dashboard
3. [ ] Comenzar tests

---

**Este roadmap es un documento vivo y se actualizará conforme avance el proyecto.**

**Última actualización**: 2025-11-05
**Próxima revisión**: 2025-11-12
