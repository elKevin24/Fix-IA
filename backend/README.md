# TESIG Backend

Backend del Sistema Integral de Gestión para Taller Electrónico (TESIG).

## 🛠️ Stack Tecnológico

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL 15**
- **MapStruct** - Mapeo automático de entidades a DTOs
- **JWT** - Autenticación y autorización
- **Lombok** - Reducción de código boilerplate
- **SpringDoc OpenAPI** - Documentación API automática

## 📁 Estructura del Proyecto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/tesig/
│   │   │   ├── config/          # Configuraciones (Security, OpenAPI)
│   │   │   ├── controller/      # Controladores REST
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Manejo de excepciones
│   │   │   ├── mapper/          # MapStruct mappers
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── repository/      # Repositorios JPA
│   │   │   ├── security/        # Seguridad (JWT, filters)
│   │   │   └── service/         # Lógica de negocio
│   │   │       └── impl/        # Implementaciones de servicios
│   │   └── resources/
│   │       ├── application.yml  # Configuración principal
│   │       └── data.sql         # Datos de prueba
│   └── test/                    # Tests unitarios e integración
├── docker-compose.yml           # Compose para PostgreSQL
├── pom.xml                      # Dependencias Maven
└── README.md                    # Este archivo
```

## 🚀 Inicio Rápido

### Prerrequisitos

- JDK 17 o superior
- Maven 3.6+
- Docker y Docker Compose (para BD)

### 1. Levantar Base de Datos

```bash
# Iniciar PostgreSQL con Docker
docker-compose up -d postgres

# Ver logs de PostgreSQL
docker-compose logs -f postgres

# Detener servicios
docker-compose down
```

Credenciales por defecto:
- **Base de datos**: `tesig_db`
- **Usuario**: `tesig_user`
- **Password**: `tesig_pass`
- **Puerto**: `5432`

### 2. Configurar Variables de Entorno (Opcional)

```bash
cp .env.example .env
# Editar .env con tus configuraciones
```

### 3. Ejecutar Backend

```bash
# Compilar proyecto
./mvnw clean install

# Ejecutar aplicación
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080/api`

## 📚 Documentación API

Una vez iniciada la aplicación, la documentación interactiva está disponible en:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/api-docs

## 🔐 Endpoints Públicos

### Consulta de Tickets (Sin Autenticación)

Estos endpoints NO requieren autenticación y están diseñados para que los clientes consulten el estado de sus equipos:

#### Consultar Ticket
```http
GET /api/publico/tickets/{numeroTicket}
```

**Ejemplo:**
```bash
curl http://localhost:8080/api/publico/tickets/TKT-2024-00001
```

**Respuesta:**
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
    "fechaIngreso": "2024-11-05 10:30:00"
  },
  "timestamp": "2024-11-05 12:45:30"
}
```

#### Verificar Existencia
```http
GET /api/publico/tickets/{numeroTicket}/existe
```

## 🧪 Datos de Prueba

El sistema incluye datos de prueba que se cargan automáticamente:

### Usuarios del Sistema
| Email | Password | Rol |
|-------|----------|-----|
| admin@tesig.com | Admin123! | Administrador |
| tecnico1@tesig.com | Admin123! | Técnico |
| tecnico2@tesig.com | Admin123! | Técnico |
| recepcion@tesig.com | Admin123! | Recepcionista |

### Tickets de Prueba
- **TKT-2024-00001**: Estado INGRESADO
- **TKT-2024-00002**: Estado EN_DIAGNOSTICO
- **TKT-2024-00003**: Estado PRESUPUESTADO
- **TKT-2024-00004**: Estado EN_REPARACION
- **TKT-2024-00005**: Estado LISTO_ENTREGA
- **TKT-2024-00006**: Estado ENTREGADO

## 🏗️ Arquitectura y Principios SOLID

El proyecto sigue los principios SOLID para mantener un código limpio y mantenible:

### Single Responsibility Principle (SRP)
- Cada clase tiene una única responsabilidad
- Servicios separados por dominio (TicketPublicoService, etc.)
- Mappers dedicados para cada entidad

### Open/Closed Principle (OCP)
- Servicios implementan interfaces
- Extensible mediante nuevas implementaciones sin modificar código existente

### Liskov Substitution Principle (LSP)
- Las implementaciones pueden sustituirse por sus interfaces
- `TicketPublicoServiceImpl` puede ser reemplazada por cualquier implementación de `ITicketPublicoService`

### Interface Segregation Principle (ISP)
- Interfaces específicas para cada funcionalidad
- `ITicketPublicoService` solo expone operaciones públicas

### Dependency Inversion Principle (DIP)
- Dependencia de abstracciones (interfaces) no de implementaciones
- Inyección de dependencias mediante constructor

## 🔧 Herramientas de Desarrollo

### PgAdmin (Opcional)

Si deseas usar PgAdmin para administrar la base de datos:

```bash
docker-compose up -d pgadmin
```

Accede en: http://localhost:5050
- **Email**: admin@tesig.com
- **Password**: admin123

## 🧹 Comandos Útiles

```bash
# Limpiar y compilar
./mvnw clean install

# Ejecutar tests
./mvnw test

# Ejecutar con perfil específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Generar JAR para producción
./mvnw clean package -DskipTests

# Ver dependencias
./mvnw dependency:tree

# Actualizar mappers de MapStruct
./mvnw clean compile
```

## 📝 Notas Importantes

1. **MapStruct**: Los mappers se generan en tiempo de compilación. Si modificas un mapper, ejecuta `mvnw clean compile`.

2. **Lombok**: Asegúrate de tener el plugin de Lombok instalado en tu IDE.

3. **Seguridad**:
   - Los endpoints `/api/publico/**` NO requieren autenticación
   - Todos los demás endpoints requieren JWT válido
   - Cambiar `jwt.secret` en producción

4. **Base de Datos**:
   - El script `data.sql` se ejecuta automáticamente al iniciar
   - En producción, cambiar `ddl-auto` a `validate` o `none`

## 🐛 Troubleshooting

### Error: Port 5432 already in use
```bash
# Ver qué está usando el puerto
lsof -i :5432

# Detener PostgreSQL local si existe
brew services stop postgresql  # macOS
sudo systemctl stop postgresql # Linux
```

### Error: MapStruct mappers not found
```bash
# Recompilar para generar mappers
./mvnw clean compile
```

### Error: Could not connect to PostgreSQL
```bash
# Verificar que el contenedor esté corriendo
docker-compose ps

# Ver logs del contenedor
docker-compose logs postgres
```

## 📄 Licencia

Este proyecto es privado y propietario. Todos los derechos reservados.

## 👥 Contacto

- **Proyecto**: TESIG
- **Email**: contacto@tesig.com
