# Documentación del Proceso de Desarrollo - Backend TESIG

**Fecha**: 2025-11-05
**Versión**: 0.1.0-SNAPSHOT
**Fase**: MVP - Consulta Pública de Tickets

---

## 📋 Índice

1. [Decisión Inicial](#decisión-inicial)
2. [Arquitectura Implementada](#arquitectura-implementada)
3. [Stack Tecnológico](#stack-tecnológico)
4. [Estructura del Proyecto](#estructura-del-proyecto)
5. [Entidades y Modelo de Datos](#entidades-y-modelo-de-datos)
6. [Endpoints Implementados](#endpoints-implementados)
7. [Principios SOLID Aplicados](#principios-solid-aplicados)
8. [Datos de Prueba](#datos-de-prueba)
9. [Configuración y Deployment](#configuración-y-deployment)
10. [Próximos Pasos](#próximos-pasos)

---

## 🎯 Decisión Inicial

### Problema Original
Se requería un sistema para gestionar un taller electrónico, pero la gestión de usuarios podría ser compleja.

### Solución Propuesta
**Idea clave**: Implementar un sistema de consulta pública de tickets donde los clientes pueden consultar el estado de sus equipos usando únicamente el número de ticket, sin necesidad de crear cuenta o autenticarse.

### Ventajas
- ✅ Reduce complejidad en gestión de usuarios
- ✅ Facilita la experiencia del cliente
- ✅ Mantiene la seguridad (solo información pública visible)
- ✅ Escalable para agregar autenticación después si se requiere

---

## 🏗️ Arquitectura Implementada

### Patrón de Arquitectura
**Arquitectura en Capas (Layered Architecture)**

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← HTTP Requests/Responses
├─────────────────────────────────────┤
│         Service Layer               │  ← Business Logic
├─────────────────────────────────────┤
│         Repository Layer            │  ← Data Access
├─────────────────────────────────────┤
│         Database (PostgreSQL)       │  ← Persistence
└─────────────────────────────────────┘
```

### Flujo de Datos

```
Cliente HTTP Request
    ↓
Controller (TicketPublicoController)
    ↓
Service Interface (ITicketPublicoService)
    ↓
Service Implementation (TicketPublicoServiceImpl)
    ↓
Repository (TicketRepository)
    ↓
JPA/Hibernate
    ↓
PostgreSQL Database
```

---

## 🛠️ Stack Tecnológico

### Backend Framework
- **Spring Boot**: 3.2.0
- **Java**: 17 (LTS)
- **Maven**: 3.x

### Database
- **PostgreSQL**: 15+
- **Spring Data JPA**: Abstracción de acceso a datos
- **Hibernate**: ORM

### Seguridad
- **Spring Security**: 6.x
- **JWT**: Para autenticación (preparado para fase 2)

### Documentación
- **SpringDoc OpenAPI**: 2.3.0
- **Swagger UI**: Documentación interactiva

### Utilidades
- **Lombok**: 1.18.x - Reducción de boilerplate
- **MapStruct**: 1.5.5 - Mapeo automático de objetos
- **Validation API**: jakarta.validation

### DevOps
- **Docker Compose**: Para PostgreSQL y PgAdmin
- **Git**: Control de versiones

---

## 📁 Estructura del Proyecto

```
backend/
├── src/main/java/com/tesig/
│   ├── TesigApplication.java              # Punto de entrada
│   │
│   ├── config/                            # Configuraciones
│   │   ├── SecurityConfig.java            # Spring Security
│   │   └── OpenApiConfig.java             # Swagger/OpenAPI
│   │
│   ├── controller/                        # Capa de presentación
│   │   └── TicketPublicoController.java   # Endpoints públicos
│   │
│   ├── service/                           # Interfaces de servicios
│   │   ├── ITicketPublicoService.java     # Interface del servicio
│   │   └── impl/                          # Implementaciones
│   │       └── TicketPublicoServiceImpl.java
│   │
│   ├── repository/                        # Acceso a datos
│   │   ├── ClienteRepository.java
│   │   ├── TicketRepository.java
│   │   └── UsuarioRepository.java
│   │
│   ├── model/                             # Entidades JPA
│   │   ├── BaseEntity.java                # Clase base con auditoría
│   │   ├── Cliente.java
│   │   ├── Ticket.java
│   │   ├── Usuario.java
│   │   ├── EstadoTicket.java (enum)
│   │   └── Rol.java (enum)
│   │
│   ├── dto/                               # Data Transfer Objects
│   │   ├── ApiResponse.java               # Respuesta genérica
│   │   ├── TicketConsultaPublicaDTO.java
│   │   ├── ClienteBasicoDTO.java
│   │   └── EstadoTicketDTO.java
│   │
│   ├── mapper/                            # MapStruct mappers
│   │   ├── TicketMapper.java
│   │   ├── ClienteMapper.java
│   │   └── EstadoTicketMapper.java
│   │
│   └── exception/                         # Manejo de excepciones
│       ├── GlobalExceptionHandler.java
│       └── ResourceNotFoundException.java
│
├── src/main/resources/
│   ├── application.yml                    # Configuración principal
│   └── data.sql                           # Datos iniciales
│
├── docker-compose.yml                     # PostgreSQL + PgAdmin
├── pom.xml                                # Dependencias Maven
├── README.md                              # Documentación de uso
└── .gitignore
```

---

## 🗄️ Entidades y Modelo de Datos

### 1. BaseEntity (Clase Abstracta)
**Propósito**: Proveer campos comunes de auditoría a todas las entidades

```java
- id: Long (PK, auto-increment)
- createdAt: LocalDateTime (auto)
- updatedAt: LocalDateTime (auto)
- deletedAt: LocalDateTime (soft delete)
```

**Características**:
- Auditoría automática con `@EntityListeners`
- Soft delete (no elimina físicamente)
- Método `isDeleted()` para verificar estado

### 2. Cliente
**Propósito**: Almacenar información de clientes del taller

```java
- nombre: String (required)
- apellido: String (required)
- telefono: String (required, unique)
- email: String (optional)
- direccion: String (optional)
- notas: Text (optional)
- tickets: List<Ticket> (relación 1:N)
```

**Validaciones**:
- Teléfono: 10 dígitos
- Email: formato válido

### 3. Usuario
**Propósito**: Personal del taller con acceso al sistema

```java
- nombre: String (required)
- apellido: String (required)
- email: String (required, unique)
- password: String (required, hashed)
- rol: Rol enum (required)
- activo: Boolean (default: true)
- ticketsAsignados: List<Ticket>
```

**Roles Disponibles**:
- `ADMINISTRADOR`: Acceso total
- `TECNICO`: Diagnósticos y reparaciones
- `RECEPCIONISTA`: Ingreso y entrega

### 4. Ticket
**Propósito**: Representar una orden de servicio/reparación

**Información del Equipo**:
```java
- numeroTicket: String (unique, PK funcional)
- tipoEquipo: String (Laptop, PC, etc.)
- marca: String
- modelo: String
- numeroSerie: String
- fallaReportada: Text
- accesorios: Text
```

**Estado y Seguimiento**:
```java
- estado: EstadoTicket enum
- diagnostico: Text
- presupuestoManoObra: BigDecimal
- presupuestoPiezas: BigDecimal
- presupuestoTotal: BigDecimal (calculado)
- tiempoEstimadoDias: Integer
```

**Fechas Importantes**:
```java
- fechaPresupuesto: LocalDateTime
- fechaRespuestaCliente: LocalDateTime
- fechaInicioReparacion: LocalDateTime
- fechaFinReparacion: LocalDateTime
- fechaEntrega: LocalDateTime
```

**Relaciones**:
```java
- cliente: Cliente (ManyToOne, required)
- tecnicoAsignado: Usuario (ManyToOne, optional)
- usuarioIngreso: Usuario (ManyToOne, optional)
```

### 5. EstadoTicket (Enum)
Estados del flujo completo:

1. **INGRESADO**: Equipo recibido, esperando asignación
2. **EN_DIAGNOSTICO**: Técnico evaluando
3. **PRESUPUESTADO**: Esperando respuesta del cliente
4. **APROBADO**: Cliente aceptó presupuesto
5. **RECHAZADO**: Cliente rechazó presupuesto
6. **EN_REPARACION**: Reparación en proceso
7. **EN_PRUEBA**: Validando funcionamiento
8. **LISTO_ENTREGA**: Esperando que cliente recoja
9. **ENTREGADO**: Ticket completado (FINAL)
10. **CANCELADO**: Ticket cerrado sin completar (FINAL)

---

## 🌐 Endpoints Implementados

### Endpoints Públicos (Sin Autenticación)

#### 1. Consultar Ticket
```http
GET /api/publico/tickets/{numeroTicket}
```

**Ejemplo**:
```bash
curl http://localhost:8080/api/publico/tickets/TKT-2024-00001
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Ticket encontrado",
  "data": {
    "numeroTicket": "TKT-2024-00001",
    "tipoEquipo": "Laptop",
    "marca": "HP",
    "modelo": "Pavilion 15",
    "fallaReportada": "No enciende, se queda en pantalla negra",
    "estado": {
      "codigo": "INGRESADO",
      "nombre": "Ingresado",
      "descripcion": "Equipo recibido, esperando asignación"
    },
    "cliente": {
      "nombre": "Juan",
      "apellido": "Pérez García",
      "nombreCompleto": "Juan Pérez García"
    },
    "fechaIngreso": "2024-11-05 10:30:00",
    "presupuestoTotal": null,
    "diagnostico": null
  },
  "timestamp": "2024-11-05 12:45:30"
}
```

**Respuesta Error (404)**:
```json
{
  "success": false,
  "message": "Ticket no encontrado con número: 'TKT-XXXX'",
  "data": null,
  "timestamp": "2024-11-05 12:45:30"
}
```

#### 2. Verificar Existencia
```http
GET /api/publico/tickets/{numeroTicket}/existe
```

**Respuesta**:
```json
{
  "success": true,
  "message": "El ticket existe",
  "data": true,
  "timestamp": "2024-11-05 12:45:30"
}
```

---

## ⚙️ Principios SOLID Aplicados

### 1. Single Responsibility Principle (SRP)
**"Una clase debe tener una sola razón para cambiar"**

✅ **Aplicado en**:
- `TicketPublicoController`: Solo maneja HTTP requests
- `TicketPublicoServiceImpl`: Solo lógica de negocio de consultas públicas
- `TicketRepository`: Solo acceso a datos de tickets
- `TicketMapper`: Solo mapeo de entidades a DTOs

### 2. Open/Closed Principle (OCP)
**"Abierto para extensión, cerrado para modificación"**

✅ **Aplicado en**:
- Uso de interfaces (`ITicketPublicoService`)
- Se puede agregar nuevas implementaciones sin modificar código existente
- Ejemplo: `TicketPublicoServiceImplV2` podría implementar la misma interface

### 3. Liskov Substitution Principle (LSP)
**"Los objetos deben ser reemplazables por instancias de sus subtipos"**

✅ **Aplicado en**:
- `TicketPublicoServiceImpl` puede sustituirse por cualquier implementación de `ITicketPublicoService`
- El controlador depende de la interface, no de la implementación

### 4. Interface Segregation Principle (ISP)
**"Los clientes no deben depender de interfaces que no usan"**

✅ **Aplicado en**:
- `ITicketPublicoService`: Solo métodos de consulta pública
- No contiene métodos de gestión interna de tickets
- Interfaces específicas por funcionalidad

### 5. Dependency Inversion Principle (DIP)
**"Depender de abstracciones, no de implementaciones concretas"**

✅ **Aplicado en**:
```java
// Controlador depende de la abstracción
private final ITicketPublicoService ticketPublicoService;

// Servicio depende de abstracciones
private final TicketRepository ticketRepository;
private final TicketMapper ticketMapper;
```

---

## 🧪 Datos de Prueba

### Clientes Precargados
| Nombre | Teléfono | Email |
|--------|----------|-------|
| Juan Pérez García | 5512345678 | juan.perez@email.com |
| María López Hernández | 5523456789 | maria.lopez@email.com |
| Carlos Rodríguez Martínez | 5534567890 | carlos.rodriguez@email.com |
| Ana García Sánchez | 5545678901 | ana.garcia@email.com |
| Pedro Martínez López | 5556789012 | pedro.martinez@email.com |

### Usuarios del Sistema
| Email | Password | Rol |
|-------|----------|-----|
| admin@tesig.com | Admin123! | ADMINISTRADOR |
| tecnico1@tesig.com | Admin123! | TECNICO |
| tecnico2@tesig.com | Admin123! | TECNICO |
| recepcion@tesig.com | Admin123! | RECEPCIONISTA |

### Tickets de Prueba
| Número | Cliente | Estado | Descripción |
|--------|---------|--------|-------------|
| TKT-2024-00001 | Juan Pérez | INGRESADO | Laptop HP no enciende |
| TKT-2024-00002 | María López | EN_DIAGNOSTICO | PC Dell se reinicia |
| TKT-2024-00003 | Carlos Rodríguez | PRESUPUESTADO | Laptop Lenovo teclado dañado |
| TKT-2024-00004 | Ana García | EN_REPARACION | iMac con líneas en pantalla |
| TKT-2024-00005 | Pedro Martínez | LISTO_ENTREGA | MacBook batería agotada |
| TKT-2024-00006 | Juan Pérez | ENTREGADO | Laptop Asus sobrecalentamiento |

---

## 🚀 Configuración y Deployment

### Requisitos Previos
- JDK 17+
- Maven 3.6+
- Docker y Docker Compose
- PostgreSQL 15+ (o usar Docker)

### Variables de Entorno
```bash
# Database
POSTGRES_DB=tesig_db
POSTGRES_USER=tesig_user
POSTGRES_PASSWORD=tesig_pass

# JWT (para fase 2)
JWT_SECRET=your-secret-key-here
```

### Iniciar Base de Datos
```bash
cd backend
docker-compose up -d postgres
```

### Ejecutar Backend
```bash
# Compilar
./mvnw clean install

# Ejecutar
./mvnw spring-boot:run

# O construir JAR
./mvnw clean package
java -jar target/tesig-backend-0.1.0-SNAPSHOT.jar
```

### Acceso a Servicios
- **Backend API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **PgAdmin**: http://localhost:5050 (admin@tesig.com / admin123)
- **PostgreSQL**: localhost:5432

### Verificar Instalación
```bash
# Health check
curl http://localhost:8080/api/publico/tickets/TKT-2024-00001

# Debe retornar JSON con información del ticket
```

---

## 📊 Próximos Pasos

### Fase 1: Completar MVP Backend
- [ ] Endpoint de login JWT
- [ ] CRUD completo de clientes
- [ ] CRUD completo de tickets (para personal interno)
- [ ] Asignación de técnicos
- [ ] Cambio de estados de tickets
- [ ] Registro de diagnósticos y presupuestos

### Fase 2: Comunicación y Aprobaciones
- [ ] Sistema de notificaciones por email
- [ ] Portal web para clientes
- [ ] Aprobación digital de presupuestos
- [ ] Firma digital o PIN de conformidad
- [ ] Captura de fotografías

### Fase 3: Inventario
- [ ] CRUD de piezas y repuestos
- [ ] Movimientos de inventario
- [ ] Alertas de stock bajo
- [ ] Asociación de piezas a tickets

### Fase 4: Gestión Avanzada
- [ ] Gestión de proveedores
- [ ] Órdenes de compra
- [ ] Reportes PDF avanzados
- [ ] Analytics y KPIs
- [ ] Integración WhatsApp

---

## 📝 Notas Técnicas

### MapStruct
Los mappers se generan en tiempo de compilación. Después de modificar un mapper:
```bash
./mvnw clean compile
```

### Soft Delete
Todas las entidades usan soft delete. Para filtrar registros activos:
```java
@Query("SELECT e FROM Entity e WHERE e.deletedAt IS NULL")
```

### Seguridad
- Endpoints `/api/publico/**` son públicos
- Todos los demás requieren autenticación (fase 2)
- Passwords hasheados con BCrypt

### Base de Datos
- `ddl-auto: update` para desarrollo
- Cambiar a `validate` en producción
- Script `data.sql` se ejecuta automáticamente

---

## 🎓 Lecciones Aprendidas

1. **Consulta Pública Sin Auth**: Idea brillante que simplifica enormemente la UX
2. **MapStruct**: Ahorra muchísimo código de mapeo manual
3. **SOLID**: Hace el código más mantenible y testeable
4. **Docker**: Facilita setup de desarrollo
5. **Soft Delete**: Mejor que delete físico para auditoría

---

## 📞 Soporte

Para dudas o problemas:
- Ver `backend/README.md` para troubleshooting
- Revisar logs: `docker-compose logs -f`
- Swagger UI para probar endpoints

---

**Documento generado**: 2025-11-05
**Autor**: Claude Code
**Versión Backend**: 0.1.0-SNAPSHOT
**Última actualización**: 2025-11-05
