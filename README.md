# TESIG – Taller Electrónico Sistema Integral de Gestión

> Sistema integral para gestionar las operaciones de un taller electrónico, desde la recepción de equipos hasta la entrega final al cliente.

## 📋 Tabla de Contenidos

- [Objetivo General](#objetivo-general)
- [Stack Tecnológico](#stack-tecnológico)
- [Roadmap de Desarrollo](#roadmap-de-desarrollo)
- [Alcance por Fases](#alcance-por-fases)
- [Usuarios del Sistema](#usuarios-del-sistema)
- [Flujo Operativo](#flujo-operativo)
- [Estados del Ticket](#estados-del-ticket)
- [Requisitos del Sistema](#requisitos-del-sistema)
- [Instalación](#instalación)

---

## 🎯 Objetivo General

Diseñar un sistema integral para gestionar las operaciones de un taller electrónico, desde la recepción de equipos hasta la entrega final al cliente, incluyendo diagnóstico, presupuestos, reparaciones, control de inventario, compras y generación de reportes administrativos y técnicos.

---

## 🛠️ Stack Tecnológico

### Backend
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java Spring Boot | 3.x | Framework principal |
| PostgreSQL | 15+ | Base de datos |
| JWT | - | Autenticación |
| Swagger/OpenAPI | 3.0 | Documentación API |
| Docker | - | Contenedorización |

### Frontend
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| React | 18+ | Framework UI |
| Vite | 5.x | Build tool & Dev server |
| TypeScript | 5.x | Lenguaje tipado |
| Tailwind CSS | 3.x | Estilos |
| shadcn/ui | - | Componentes UI |
| React Router | 6.x | Enrutamiento |
| Zustand | 4.x | State management |
| React Hook Form | 7.x | Manejo de formularios |
| Zod | 3.x | Validación schemas |
| TanStack Table | 8.x | Tablas avanzadas |
| Axios | 1.x | Cliente HTTP |

### DevOps
- Docker & Docker Compose
- Git & GitHub
- Logs centralizados
- Backups automáticos

---

## 🗺️ Roadmap de Desarrollo

```
┌─────────────────────────────────────────────────────────────────┐
│                        ROADMAP TESIG                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  FASE 1: MVP Core                    [2-3 meses] ████████░░░░  │
│  └─ Gestión básica de tickets y clientes                       │
│                                                                 │
│  FASE 2: Comunicación & Aprobaciones [2 meses]   ░░░░░░░░░░░░  │
│  └─ Portal cliente y notificaciones                            │
│                                                                 │
│  FASE 3: Inventario                  [2-3 meses] ░░░░░░░░░░░░  │
│  └─ Control completo de stock                                  │
│                                                                 │
│  FASE 4: Gestión Avanzada            [2 meses]   ░░░░░░░░░░░░  │
│  └─ Reportes, proveedores, analytics                           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Estrategia:** Desarrollo iterativo con entregas funcionales cada fase. Cada fase debe estar en producción y probada antes de iniciar la siguiente.

---

## 📦 Alcance por Fases

### ⭐ FASE 1: MVP Core (2-3 meses) - **PRIORIDAD MÁXIMA**

**Objetivo:** Sistema básico funcional que reemplace papel/Excel en el flujo principal.

#### Funcionalidades incluidas:
- ✅ **Gestión de Clientes**
  - CRUD completo de clientes
  - Búsqueda por nombre, teléfono, email
  - Historial de equipos por cliente

- ✅ **Recepción de Equipos**
  - Registro de equipo (tipo, marca, modelo, serie)
  - Falla reportada por cliente
  - Registro de accesorios (sin fotos por ahora)
  - Generación de ticket con ID único
  - Estado inicial: INGRESADO

- ✅ **Gestión de Tickets**
  - Lista de tickets con filtros (estado, técnico, fecha)
  - Detalle de ticket
  - Estados básicos: `INGRESADO → EN_DIAGNOSTICO → EN_REPARACION → ENTREGADO`
  - Asignación de técnico
  - Registro de observaciones

- ✅ **Diagnóstico y Presupuesto**
  - Registro de diagnóstico técnico
  - Cálculo manual de presupuesto
  - Registro de decisión cliente (aprobar/rechazar) - **MANUAL**

- ✅ **Entrega**
  - Cambio a estado ENTREGADO
  - Registro de fecha y hora de entrega
  - Observaciones finales

- ✅ **Cancelación**
  - Cambio a estado CANCELADO desde cualquier punto
  - Registro de motivo de cancelación

- ✅ **Dashboard Básico**
  - Tickets activos por estado
  - Tickets asignados por técnico
  - Últimos ingresos

- ✅ **Autenticación y Roles**
  - Login con JWT
  - Roles: Administrador, Técnico, Recepcionista
  - Control de acceso básico por rol

#### Excluido del MVP:
- ❌ Portal web para clientes
- ❌ Notificaciones automáticas
- ❌ Fotografías de equipos
- ❌ Sistema de inventario
- ❌ Reportes PDF
- ❌ Gestión de proveedores
- ❌ Órdenes de compra
- ❌ Firma digital

**Criterio de éxito:** El taller puede gestionar 100% del flujo básico (ingreso → diagnóstico → reparación → entrega) digitalmente.

---

### 📱 FASE 2: Comunicación & Aprobaciones (2 meses)

**Objetivo:** Automatizar la comunicación con clientes y aprobaciones digitales.

#### Funcionalidades:
- ✅ Portal web para clientes (consulta de estado)
- ✅ Notificaciones por correo electrónico
  - Ticket creado
  - Presupuesto listo
  - Equipo reparado/listo
  - Cambios de estado
- ✅ Aprobación digital de presupuestos
  - Link único por ticket
  - Aprobar/Rechazar con justificación
- ✅ Estados adicionales: `PRESUPUESTADO`, `APROBADO`, `RECHAZADO`
- ✅ Captura de fotografías en recepción y entrega
- ✅ Firma digital o PIN de conformidad
- ✅ Historial de comunicaciones por ticket

**Criterio de éxito:** 80% de las aprobaciones se hacen digitalmente sin llamadas telefónicas.

---

### 📊 FASE 3: Inventario (2-3 meses)

**Objetivo:** Control completo de stock de piezas y repuestos.

#### Funcionalidades:
- ✅ CRUD de piezas y repuestos
  - Código, nombre, descripción
  - Precio de compra y venta
  - Stock mínimo y actual
  - Categorización
- ✅ Movimientos de inventario
  - Entradas (compras)
  - Salidas (consumo en reparaciones)
  - Ajustes manuales
  - Reservas por ticket
- ✅ Alertas de stock bajo
  - Notificaciones automáticas
  - Lista de piezas a reponer
- ✅ Reportes de inventario
  - Valorización de stock
  - Movimientos por período
  - Piezas más usadas
- ✅ Asociación de piezas a tickets
  - Registro de piezas usadas en reparación
  - Cálculo automático de costo de materiales

**Criterio de éxito:** Cero faltantes de stock por falta de control. Inventario siempre actualizado.

---

### 🚀 FASE 4: Gestión Avanzada (2 meses)

**Objetivo:** Completar el sistema con funcionalidades avanzadas de gestión.

#### Funcionalidades:
- ✅ Gestión de Proveedores
  - CRUD de proveedores
  - Contactos y condiciones comerciales
  - Historial de compras
- ✅ Órdenes de Compra
  - Generación de OC desde alertas de stock
  - Seguimiento de OC (pendiente/recibida)
  - Recepción de mercadería
- ✅ Reportes Avanzados PDF
  - Ticket de recepción con QR
  - Presupuesto detallado
  - Reporte de reparación completo
  - Comprobante de entrega
- ✅ Integración WhatsApp (opcional)
  - Notificaciones por WhatsApp
  - Consulta de estado por bot
- ✅ Analytics y KPIs
  - Tiempo promedio de reparación
  - Tasa de aprobación de presupuestos
  - Ingresos por período
  - Productividad por técnico
  - Piezas más rentables
- ✅ Auditoría completa
  - Log de todos los cambios
  - Bitácora por ticket
  - Trazabilidad completa

**Criterio de éxito:** Sistema completo y productivo con métricas que permitan toma de decisiones basada en datos.

---

### 🚫 Fuera de Alcance (Todas las Fases)

Estas funcionalidades NO se implementarán en la versión 1.0:

- ❌ Integración con sistemas contables o ERP externos
- ❌ Facturación electrónica oficial (SAT, SUNAT, etc.)
- ❌ Gestión de garantías extendidas
- ❌ Contratos de mantenimiento preventivo
- ❌ Sistema de punto de venta (POS) completo
- ❌ E-commerce o venta online
- ❌ App móvil nativa (iOS/Android)
- ❌ Integración con redes sociales

---

## 👥 Usuarios del Sistema

| Rol | Funciones Principales | Accesos |
|-----|----------------------|---------|
| **Administrador** | Configurar usuarios, revisar reportes, gestionar inventario y proveedores, configuración del sistema | Total |
| **Recepcionista** | Registrar clientes y equipos, crear tickets, coordinar entregas, atención al cliente | Tickets, Clientes, Dashboard |
| **Técnico** | Realizar diagnóstico, registrar reparaciones, actualizar estados, registrar consumo de piezas | Tickets asignados, Inventario (consulta) |
| **Cliente** (Fase 2+) | Consultar estado del equipo, aprobar presupuestos, revisar reportes | Solo sus tickets |

---

## 🔄 Flujo Operativo

### Diagrama de Flujo Principal

```
┌─────────────────┐
│   INGRESO       │  Cliente llega con equipo
│   Recepcionista │  registra datos y falla
└────────┬────────┘
         │
         v
┌─────────────────┐
│  DIAGNÓSTICO    │  Técnico revisa y determina
│   Técnico       │  problema y costo
└────────┬────────┘
         │
         v
┌─────────────────┐
│ PRESUPUESTADO   │  Cliente recibe presupuesto
│   Sistema       │  y decide
└────┬───────┬────┘
     │       │
   Acepta  Rechaza
     │       │
     v       v
┌─────────┐ ┌─────────┐
│APROBADO │ │CANCELADO│
└────┬────┘ └─────────┘
     │
     v
┌─────────────────┐
│  REPARACIÓN     │  Técnico ejecuta trabajos
│   Técnico       │  y registra piezas usadas
└────────┬────────┘
         │
         v
┌─────────────────┐
│    PRUEBAS      │  Validación de
│   Técnico       │  funcionamiento
└────────┬────────┘
         │
         v
┌─────────────────┐
│ LISTO ENTREGA   │  Cliente es notificado
│   Sistema       │  para recoger equipo
└────────┬────────┘
         │
         v
┌─────────────────┐
│   ENTREGADO     │  Cliente recoge, firma
│  Recepcionista  │  y cierra ticket
└─────────────────┘
```

### Descripción Detallada

#### 1. Ingreso del Equipo
- Recepcionista registra o busca cliente existente
- Registra datos del equipo (tipo, marca, modelo, serie)
- Captura falla reportada por cliente
- Lista accesorios incluidos
- Genera ticket con ID único
- **Estado:** `INGRESADO`
- Imprime comprobante de ingreso para cliente

#### 2. Diagnóstico Técnico
- Administrador asigna ticket a técnico disponible
- Técnico cambia estado a `EN_DIAGNOSTICO`
- Realiza revisión técnica del equipo
- Registra diagnóstico detallado
- Estima costo de reparación (mano de obra + piezas)
- Estima tiempo de reparación
- Cambia estado a `PRESUPUESTADO`
- Sistema notifica a cliente (Fase 2+)

#### 3. Aprobación de Presupuesto
- Cliente recibe notificación con presupuesto
- **Fase 1:** Recepcionista registra decisión manualmente
- **Fase 2+:** Cliente aprueba/rechaza digitalmente
- **Si aprueba:** Estado → `APROBADO`
- **Si rechaza:** Estado → `CANCELADO` (con motivo)

#### 4. Reparación
- Técnico inicia reparación: Estado → `EN_REPARACION`
- Registra piezas utilizadas (Fase 3+)
- Actualiza observaciones de progreso
- Puede agregar hallazgos adicionales
- Al terminar: Estado → `EN_PRUEBA`

#### 5. Pruebas y Control de Calidad
- Técnico valida funcionamiento completo
- Registra resultado de pruebas
- Si está OK: Estado → `LISTO_ENTREGA`
- Si encuentra problemas: Regresa a `EN_REPARACION`

#### 6. Entrega al Cliente
- Sistema notifica a cliente que equipo está listo
- Cliente llega a recoger
- Recepcionista valida identidad
- Cliente revisa equipo
- **Fase 2+:** Cliente firma digitalmente
- Recepcionista cambia estado a `ENTREGADO`
- Se genera comprobante de entrega

#### 7. Cancelación (desde cualquier estado)
- Puede ocurrir por:
  - Cliente rechaza presupuesto
  - Cliente no aprueba en X días
  - Equipo irreparable
  - Cliente no recoge en X días
- Se documenta motivo de cancelación
- Estado → `CANCELADO`
- Se cierra el ticket

---

## 📊 Estados del Ticket

### Diagrama de Estados

```
                    ┌──────────────┐
                    │   INGRESADO  │ (Inicial)
                    └──────┬───────┘
                           │
                           v
                    ┌──────────────┐
                    │EN_DIAGNOSTICO│
                    └──────┬───────┘
                           │
                           v
                    ┌──────────────┐
                    │PRESUPUESTADO │
                    └──────┬───────┘
                           │
                    ┌──────┴───────┐
                    │              │
                    v              v
             ┌──────────┐   ┌──────────┐
             │ APROBADO │   │ RECHAZADO│
             └────┬─────┘   └────┬─────┘
                  │              │
                  v              v
           ┌──────────────┐ ┌──────────┐
           │EN_REPARACION │ │ CANCELADO│ ◄──┐
           └──────┬───────┘ └──────────┘    │
                  │                          │
                  v                          │
           ┌──────────────┐                  │
           │  EN_PRUEBA   │                  │
           └──────┬───────┘                  │
                  │                          │
                  v                          │
           ┌──────────────┐                  │
           │LISTO_ENTREGA │                  │
           └──────┬───────┘                  │
                  │                          │
                  v                          │
           ┌──────────────┐                  │
           │  ENTREGADO   │ (Final)          │
           └──────────────┘                  │
                                             │
          (Desde cualquier estado) ──────────┘
```

### Definición de Estados

| Estado | Descripción | Quién puede cambiarlo | Siguiente estado posible |
|--------|-------------|----------------------|--------------------------|
| `INGRESADO` | Equipo recibido, esperando asignación | Recepcionista | `EN_DIAGNOSTICO`, `CANCELADO` |
| `EN_DIAGNOSTICO` | Técnico está evaluando el equipo | Técnico | `PRESUPUESTADO`, `CANCELADO` |
| `PRESUPUESTADO` | Esperando respuesta del cliente | Técnico | `APROBADO`, `RECHAZADO`, `CANCELADO` |
| `APROBADO` | Cliente aceptó el presupuesto | Sistema/Cliente | `EN_REPARACION`, `CANCELADO` |
| `RECHAZADO` | Cliente rechazó el presupuesto | Sistema/Cliente | `CANCELADO` |
| `EN_REPARACION` | Reparación en proceso | Técnico | `EN_PRUEBA`, `CANCELADO` |
| `EN_PRUEBA` | Validando funcionamiento | Técnico | `LISTO_ENTREGA`, `EN_REPARACION`, `CANCELADO` |
| `LISTO_ENTREGA` | Equipo reparado, esperando cliente | Técnico | `ENTREGADO`, `CANCELADO` |
| `ENTREGADO` | Equipo entregado al cliente (Final) | Recepcionista | - |
| `CANCELADO` | Ticket cerrado sin completar servicio (Final) | Cualquiera | - |

---

## ⚙️ Requisitos del Sistema

### Requisitos Funcionales

- ✅ CRUD completo de: clientes, equipos, tickets, inventario (Fase 3+), proveedores (Fase 4+), usuarios
- ✅ Control de flujo de servicio por estados definidos
- ✅ Comunicación automática con cliente (Fase 2+): correo electrónico y WhatsApp (Fase 4+)
- ✅ Generación de reportes y comprobantes en PDF (Fase 4+)
- ✅ Control de acceso basado en roles (RBAC)
- ✅ Auditoría de cambios y bitácora de estados por ticket
- ✅ Búsqueda y filtrado avanzado de tickets
- ✅ Dashboard con métricas en tiempo real
- ✅ Cálculo automático de costos (mano de obra + piezas)
- ✅ Historial completo por cliente y equipo

### Requisitos No Funcionales

| Categoría | Requisito | Especificación |
|-----------|-----------|----------------|
| **Performance** | Tiempo de respuesta API | < 500ms (promedio) |
| **Performance** | Carga de dashboard | < 2 segundos |
| **Escalabilidad** | Usuarios concurrentes | 50+ simultáneos |
| **Escalabilidad** | Tickets en BD | Hasta 100,000+ |
| **Disponibilidad** | Uptime | 99.5% (objetivo) |
| **Seguridad** | Autenticación | JWT con refresh token |
| **Seguridad** | Encriptación | HTTPS obligatorio |
| **Seguridad** | Passwords | Bcrypt hash |
| **Backup** | Frecuencia | Diario automático |
| **Backup** | Retención | 30 días |
| **Logs** | Centralización | Todos los eventos |
| **Logs** | Retención | 90 días |
| **Browser Support** | Navegadores | Chrome 90+, Firefox 88+, Safari 14+, Edge 90+ |
| **Responsive** | Dispositivos | Desktop, tablet, mobile |

### Supuestos y Restricciones

#### Supuestos:
- Taller tiene conexión a internet estable
- Personal tiene conocimientos básicos de computación
- Existe al menos 1 administrador que configurará el sistema
- Volumen estimado: 50-200 tickets/mes (inicial)
- Equipo de desarrollo: 2-3 desarrolladores

#### Restricciones:
- Presupuesto limitado (solución open-source prioritaria)
- No se puede contratar servicios cloud costosos inicialmente
- Debe funcionar on-premise (Docker en servidor local)
- No requiere alta disponibilidad 24/7 (taller cierra domingos)

---

## 🚀 Instalación

### Prerrequisitos

- Docker & Docker Compose
- Git
- Node.js 18+ (para desarrollo frontend)
- Java 17+ (para desarrollo backend)
- PostgreSQL 15+ (o usar Docker)

### Instalación Rápida (Desarrollo)

#### Backend

```bash
# Clonar repositorio
git clone https://github.com/tu-repo/tesig-backend.git
cd tesig-backend

# Configurar variables de entorno
cp .env.example .env
# Editar .env con tus configuraciones

# Levantar base de datos con Docker
docker-compose up -d postgres

# Ejecutar backend
./mvnw spring-boot:run
```

Backend disponible en: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

#### Frontend

```bash
# Clonar repositorio
git clone https://github.com/tu-repo/tesig-frontend.git
cd tesig-frontend

# Instalar dependencias
npm install

# Configurar variables de entorno
cp .env.example .env
# Editar .env con URL del backend

# Ejecutar en desarrollo
npm run dev
```

Frontend disponible en: `http://localhost:5173`

### Instalación con Docker (Producción)

```bash
# Clonar repositorio completo
git clone https://github.com/tu-repo/tesig.git
cd tesig

# Levantar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f
```

Accesos:
- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080`
- Database: `localhost:5432`

### Usuario por Defecto

```
Email: admin@tesig.com
Password: Admin123!
```

**⚠️ CAMBIAR EN PRODUCCIÓN**

---

## 📈 Entradas y Salidas del Sistema

### Entradas

| Entrada | Origen | Descripción | Fase |
|---------|--------|-------------|------|
| Datos del cliente y equipo | Recepcionista | Información de ingreso, falla reportada | MVP |
| Diagnóstico técnico | Técnico | Descripción de falla y presupuesto | MVP |
| Respuesta del cliente | Cliente | Aprobación o rechazo del servicio | MVP/Fase 2 |
| Observaciones de reparación | Técnico | Avances y detalles del trabajo | MVP |
| Movimientos de inventario | Técnico/Admin | Registro de consumo o compra de piezas | Fase 3 |
| Datos de proveedores | Administrador | Información de proveedores y precios | Fase 4 |

### Salidas

| Salida | Destinatario | Descripción | Formato | Fase |
|--------|-------------|-------------|---------|------|
| Ticket de recepción | Cliente | Confirmación del ingreso del equipo | Impreso/Email | MVP |
| Presupuesto | Cliente | Estimación de costos y tiempos | Email/PDF | MVP/Fase 2 |
| Notificación de estado | Cliente | Cambios en el estado del ticket | Email/WhatsApp | Fase 2 |
| Reporte de reparación | Cliente | Detalle de los trabajos realizados | PDF | Fase 4 |
| Comprobante de entrega | Cliente | Confirmación de entrega y conformidad | PDF | Fase 4 |
| Reportes de gestión | Administrador | Indicadores y métricas del taller | PDF/Dashboard | Fase 4 |
| Alertas de inventario | Administrador | Notificación de stock bajo | Email | Fase 3 |

---

## 🎯 Criterios de Éxito por Fase

### MVP (Fase 1)
- ✅ 100% de tickets gestionados digitalmente (cero papel)
- ✅ Tiempo de registro de ingreso < 5 minutos
- ✅ Sistema estable sin caídas por 1 semana continua
- ✅ 3 usuarios (admin, técnico, recepcionista) trabajando simultáneamente

### Fase 2
- ✅ 80% de aprobaciones digitales (sin llamadas)
- ✅ Clientes pueden consultar estado sin llamar
- ✅ 90% de notificaciones entregadas exitosamente

### Fase 3
- ✅ Cero faltantes de stock por falta de control
- ✅ Inventario actualizado en tiempo real
- ✅ Reducción de 50% en tiempo de búsqueda de piezas

### Fase 4
- ✅ Reportes generados en < 3 segundos
- ✅ Métricas disponibles para toma de decisiones
- ✅ 95% de satisfacción de usuarios del sistema

---

## 📚 Documentación Adicional

- [API Documentation](./docs/api.md) - Endpoints REST
- [Database Schema](./docs/database.md) - Modelo de datos
- [User Manual](./docs/manual.md) - Guía de usuario
- [Deployment Guide](./docs/deployment.md) - Guía de despliegue

---

## 📝 Entregables de la Fase de Diseño

- [x] Documento de alcance (este README)
- [ ] Diagrama de flujo del proceso
- [ ] Modelo entidad-relación lógico
- [ ] Diagrama de arquitectura del sistema
- [ ] Definición modular del backend y frontend
- [ ] Borrador de endpoints REST
- [ ] Plantillas base de reportes PDF

---

## 🤝 Contribución

Este proyecto está en desarrollo activo. Si quieres contribuir:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto es privado y propietario. Todos los derechos reservados.

---

## 📞 Contacto

- **Proyecto:** TESIG
- **Repositorio:** [GitHub](https://github.com/tu-repo/tesig)
- **Email:** contacto@tesig.com

---

## 🔄 Estado Actual del Proyecto

**Fase Actual:** Diseño y Planificación
**Próximo Hito:** Inicio de desarrollo MVP (Fase 1)
**Versión:** 0.1.0-alpha

---

**Última actualización:** 2025-11-05
