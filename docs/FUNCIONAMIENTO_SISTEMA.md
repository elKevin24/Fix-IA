# 🔧 TESIG - Sistema de Gestión de Taller Electrónico

## 📋 Tabla de Contenido
- [Funcionamiento para el Cliente](#-funcionamiento-para-el-cliente)
- [Funcionamiento para el Taller](#-funcionamiento-para-el-taller)
- [Flujo Completo del Servicio](#-flujo-completo-del-servicio)
- [Roles y Responsabilidades](#-roles-y-responsabilidades)

---

## 👤 Funcionamiento para el Cliente

### 1. **Ingreso del Equipo al Taller**

El cliente llega al taller con su equipo dañado:

1. **Recepción**: La recepcionista/administrador registra el equipo en el sistema
2. **Ticket Único**: El sistema genera un número de ticket único (ej: `TKT-20251105-0001`)
3. **Información Registrada**:
   - Datos del cliente (nombre, teléfono, email, dirección)
   - Información del equipo (tipo, marca, modelo, número de serie)
   - Falla reportada por el cliente
   - Accesorios incluidos (cables, cargadores, etc.)

**⚠️ IMPORTANTE**: El cliente debe **guardar el número de ticket**, es su único medio para consultar el estado de su reparación.

### 2. **Consulta del Estado (Sin Necesidad de Cuenta)**

El cliente puede consultar el estado de su reparación **en cualquier momento** sin necesidad de crear una cuenta o iniciar sesión:

#### Opciones de Consulta:

**A) Portal Web Público** (Acceso directo desde el navegador)
```
https://tesig.com/consulta-ticket
```

**Pasos**:
1. Ingresar al portal de consulta pública
2. Escribir el número de ticket (ej: TKT-20251105-0001)
3. Ver información en tiempo real:
   - ✅ Estado actual del ticket
   - 📅 Fecha de ingreso
   - 🔧 Tipo de equipo y marca
   - 💰 Presupuesto (si ya está disponible)
   - ⏰ Tiempo estimado de reparación
   - 📝 Diagnóstico (cuando esté listo)
   - 🎯 Próximo paso en el proceso

**B) Endpoint API** (Para integración o apps móviles)
```
GET /api/publico/tickets/{numeroTicket}
```

### 3. **Estados que el Cliente Puede Ver**

| Estado | Significado para el Cliente | ¿Qué Esperar? |
|--------|----------------------------|---------------|
| **INGRESADO** | Su equipo ha sido recibido y está en espera | El taller asignará un técnico pronto |
| **EN_DIAGNOSTICO** | Un técnico está revisando el equipo | Pronto recibirá el diagnóstico y presupuesto |
| **PRESUPUESTADO** | Ya tiene presupuesto disponible | **Debe contactar al taller para aprobar/rechazar** |
| **APROBADO** | Aceptó el presupuesto, comienza la reparación | El técnico iniciará el trabajo |
| **EN_REPARACION** | Su equipo está siendo reparado | Esperar a que completen el trabajo |
| **EN_PRUEBA** | Reparación terminada, realizando pruebas | Verificando que todo funcione correctamente |
| **LISTO_ENTREGA** | **¡Su equipo está listo!** | **Puede pasar a recogerlo** |
| **ENTREGADO** | Equipo ya fue entregado | Servicio completado ✅ |
| **RECHAZADO** | Rechazó el presupuesto | El equipo está disponible para retiro sin reparar |
| **CANCELADO** | Servicio cancelado | Contactar al taller para más información |

### 4. **Toma de Decisión sobre el Presupuesto**

Cuando el estado cambia a **PRESUPUESTADO**, el cliente verá:

```json
{
  "numeroTicket": "TKT-20251105-0001",
  "estado": "PRESUPUESTADO",
  "diagnostico": "Pantalla rota, necesita reemplazo completo",
  "presupuestoManoObra": 150.00,
  "presupuestoPiezas": 350.00,
  "presupuestoTotal": 500.00,
  "tiempoEstimadoDias": 5
}
```

**El cliente debe**:
1. Revisar el diagnóstico
2. Ver el presupuesto total
3. **Contactar al taller** (por teléfono, WhatsApp, email) para:
   - ✅ **APROBAR**: Si acepta el presupuesto → Estado cambia a APROBADO
   - ❌ **RECHAZAR**: Si no acepta → Estado cambia a RECHAZADO

### 5. **Recogida del Equipo**

Cuando el estado es **LISTO_ENTREGA**:

1. El cliente recibe notificación (llamada/mensaje del taller)
2. Acude al taller con su número de ticket
3. La recepcionista verifica el ticket
4. Realiza el pago (si no se pagó adelantado)
5. Recibe su equipo reparado
6. Estado final: **ENTREGADO**

### 6. **Ventajas para el Cliente**

✅ **No necesita crear cuenta** - Solo su número de ticket
✅ **Transparencia total** - Ve el estado en tiempo real
✅ **Información completa** - Diagnóstico, presupuesto, tiempos
✅ **Acceso 24/7** - Consulta cuando quiera
✅ **Simplicidad** - Un solo número para todo

---

## 🏢 Funcionamiento para el Taller

### Roles del Personal

El sistema maneja **3 roles** con diferentes permisos:

| Rol | Responsabilidades | Permisos |
|-----|------------------|----------|
| **👨‍💼 ADMINISTRADOR** | Gestión completa del sistema | Acceso total a todo |
| **👩‍💻 RECEPCIONISTA** | Atención al cliente, ingreso/entrega de equipos | Crear tickets, asignar técnicos, entregar equipos, gestionar clientes |
| **🔧 TECNICO** | Diagnóstico y reparación de equipos | Ver tickets asignados, registrar diagnóstico, reparación, pruebas |

### Sistema de Autenticación

**⚠️ NO HAY REGISTRO PÚBLICO** - Solo personal autorizado:

1. **Creación de Usuarios**: Solo el administrador puede crear cuentas de personal
2. **Login**:
   ```
   POST /api/auth/login
   {
     "email": "tecnico@tesig.com",
     "password": "contraseña"
   }
   ```
3. **Tokens JWT**:
   - Access Token: 24 horas
   - Refresh Token: 7 días (renovable)

### Flujo de Trabajo por Rol

---

### 📋 **RECEPCIONISTA**

#### 1. **Recepción del Equipo**

**Endpoint**: `POST /api/tickets`

```json
{
  "clienteId": 1,
  "tipoEquipo": "Laptop",
  "marca": "Dell",
  "modelo": "Inspiron 15",
  "numeroSerie": "SN12345678",
  "fallaReportada": "No enciende, luz indicadora parpadea",
  "accesorios": "Cargador original, mochila"
}
```

**Acciones**:
1. Registrar o buscar cliente existente
2. Crear nuevo ticket (Estado: INGRESADO)
3. **Imprimir/entregar número de ticket al cliente**: `TKT-20251105-0001`
4. Guardar accesorios recibidos

#### 2. **Asignar Técnico**

**Endpoint**: `PUT /api/tickets/{id}/asignar-tecnico`

```json
{
  "tecnicoId": 3
}
```

**Proceso**:
- Ver tickets en estado INGRESADO
- Asignar técnico disponible
- Estado cambia automáticamente a: EN_DIAGNOSTICO

#### 3. **Aprobar/Rechazar Presupuesto (Por el Cliente)**

Cuando el cliente llama/viene a responder sobre el presupuesto:

**Aprobar**: `PUT /api/tickets/{id}/aprobar-presupuesto`
- Estado: PRESUPUESTADO → APROBADO

**Rechazar**: `PUT /api/tickets/{id}/rechazar-presupuesto`
```json
{
  "motivoRechazo": "Presupuesto muy elevado, cliente no autoriza"
}
```
- Estado: PRESUPUESTADO → RECHAZADO

#### 4. **Entrega del Equipo**

**Endpoint**: `PUT /api/tickets/{id}/entregar`

```json
{
  "observacionesEntrega": "Cliente satisfecho, equipo funciona correctamente. Pagó en efectivo."
}
```

**Proceso**:
1. Verificar número de ticket con el cliente
2. Realizar cobro
3. Registrar entrega
4. Estado final: ENTREGADO

#### 5. **Gestión de Clientes**

**Endpoints disponibles**:
- `GET /api/clientes` - Listar clientes (paginado)
- `POST /api/clientes` - Crear nuevo cliente
- `PUT /api/clientes/{id}` - Actualizar datos
- `GET /api/clientes/{id}/tickets` - Ver historial de tickets

---

### 🔧 **TÉCNICO**

#### 1. **Ver Tickets Asignados**

**Endpoints**:
- `GET /api/tickets/tecnico/{miId}` - Mis tickets asignados
- `GET /api/tickets/estado/EN_DIAGNOSTICO` - Tickets pendientes de diagnóstico
- `GET /api/tickets/activos` - Todos los tickets activos

#### 2. **Registrar Diagnóstico y Presupuesto**

**Endpoint**: `PUT /api/tickets/{id}/diagnostico`

```json
{
  "diagnostico": "Placa madre dañada por sobrecalentamiento. Necesita reemplazo completo.",
  "presupuestoManoObra": 200.00,
  "presupuestoPiezas": 450.00,
  "tiempoEstimadoDias": 7
}
```

**Validaciones**:
- Estado actual debe ser: EN_DIAGNOSTICO
- Debe tener técnico asignado (tú)
- Presupuestos deben ser >= 0

**Resultado**:
- Estado: EN_DIAGNOSTICO → PRESUPUESTADO
- El cliente puede ver el presupuesto en la consulta pública

#### 3. **Iniciar Reparación**

**Endpoint**: `PUT /api/tickets/{id}/iniciar-reparacion`

**Condiciones**:
- El presupuesto debe estar APROBADO
- Automáticamente cambia estado: APROBADO → EN_REPARACION

#### 4. **Registrar Observaciones Durante la Reparación**

**Endpoint**: `PUT /api/tickets/{id}/observaciones`

```json
{
  "observaciones": "Reemplazo de placa madre completado. Instalando sistema operativo."
}
```

**Función**:
- Documentar progreso
- Las observaciones se concatenan con timestamp
- No cambia el estado

#### 5. **Completar Reparación**

**Endpoint**: `PUT /api/tickets/{id}/completar-reparacion`

**Acción**:
- Indica que terminaste el trabajo
- Estado: EN_REPARACION → EN_PRUEBA

#### 6. **Realizar Pruebas**

**Endpoint**: `PUT /api/tickets/{id}/pruebas`

```json
{
  "exitoso": true,
  "resultadoPruebas": "Todas las pruebas pasadas: POST correcto, SO carga bien, puertos funcionan."
}
```

**Flujos**:

**✅ Pruebas Exitosas** (`exitoso: true`):
- Estado: EN_PRUEBA → LISTO_ENTREGA
- El equipo está listo para que el cliente lo recoja

**❌ Pruebas Fallidas** (`exitoso: false`):
- Estado: EN_PRUEBA → EN_REPARACION
- Vuelves a reparar lo que falló

#### 7. **Marcar Listo para Entrega** (Alternativa)

**Endpoint**: `PUT /api/tickets/{id}/listo-entrega`

**Uso**: Si las pruebas ya están completas y solo necesitas marcar como listo
- Estado: EN_PRUEBA → LISTO_ENTREGA

---

### 👨‍💼 **ADMINISTRADOR**

**Acceso Completo** a todo el sistema:

#### 1. **Gestión de Usuarios**

- Crear cuentas para técnicos y recepcionistas
- Activar/desactivar usuarios
- Cambiar contraseñas

#### 2. **Cancelar Tickets**

**Endpoint**: `PUT /api/tickets/{id}/cancelar`

```json
{
  "motivoCancelacion": "Cliente solicitó cancelación, no autoriza reparación"
}
```

**Reglas**:
- Puede cancelar desde cualquier estado
- **Excepto**: ENTREGADO (no se puede cancelar lo ya entregado)

#### 3. **Estadísticas y Reportes**

**Endpoint**: `GET /api/tickets/estadisticas`

**Información disponible**:

```json
{
  "totalTickets": 150,
  "ticketsActivos": 35,
  "ticketsPorEstado": {
    "INGRESADO": 5,
    "EN_DIAGNOSTICO": 8,
    "PRESUPUESTADO": 3,
    "APROBADO": 2,
    "EN_REPARACION": 10,
    "EN_PRUEBA": 4,
    "LISTO_ENTREGA": 3,
    "ENTREGADO": 100,
    "RECHAZADO": 10,
    "CANCELADO": 5
  },
  "ticketsPorTecnico": {
    "Juan Pérez": 15,
    "María García": 20
  },
  "tiempoPromedioReparacion": 5.8
}
```

**Uso**:
- Monitorear rendimiento del taller
- Identificar cuellos de botella
- Evaluar productividad de técnicos
- Planificar recursos

#### 4. **Gestión Completa de Clientes**

- Crear, editar, eliminar clientes
- Ver historial completo de cada cliente
- Buscar clientes por nombre, teléfono, email

**Endpoint de Eliminación**: `DELETE /api/clientes/{id}`

**Validación**:
- ⚠️ No se puede eliminar un cliente con tickets activos
- Se hace soft delete (deletedAt) para mantener historial

#### 5. **Consultas Avanzadas**

**Búsqueda General**:
```
GET /api/tickets/buscar?q=laptop
```
Busca en: número de ticket, tipo de equipo, marca, modelo, falla reportada, nombre del cliente

**Filtros**:
- Por estado: `GET /api/tickets/estado/EN_REPARACION`
- Por técnico: `GET /api/tickets/tecnico/3`
- Por cliente: `GET /api/tickets/cliente/1`
- Activos solamente: `GET /api/tickets/activos`

---

## 🔄 Flujo Completo del Servicio

### Escenario Típico: Reparación Exitosa

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. RECEPCIONISTA: Cliente llega con laptop que no enciende     │
│    → Crea ticket TKT-20251105-0001                              │
│    → Estado: INGRESADO                                          │
│    → Entrega número al cliente                                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 2. RECEPCIONISTA: Asigna al Técnico Juan                       │
│    → Estado: INGRESADO → EN_DIAGNOSTICO                         │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 3. TÉCNICO JUAN: Revisa el equipo                              │
│    → Diagnóstico: "Placa madre dañada"                          │
│    → Presupuesto: $500 total, 7 días                            │
│    → Estado: EN_DIAGNOSTICO → PRESUPUESTADO                     │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 4. CLIENTE: Consulta en portal público                          │
│    → Ve diagnóstico y presupuesto                               │
│    → Llama al taller para aprobar                               │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 5. RECEPCIONISTA: Registra aprobación del cliente              │
│    → Estado: PRESUPUESTADO → APROBADO                           │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 6. TÉCNICO JUAN: Inicia reparación                             │
│    → Estado: APROBADO → EN_REPARACION                           │
│    → Registra observaciones del progreso                        │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 7. TÉCNICO JUAN: Completa reparación                           │
│    → Estado: EN_REPARACION → EN_PRUEBA                          │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 8. TÉCNICO JUAN: Realiza pruebas                               │
│    → Pruebas exitosas ✅                                        │
│    → Estado: EN_PRUEBA → LISTO_ENTREGA                          │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 9. CLIENTE: Consulta portal y ve "LISTO_ENTREGA"               │
│    → Recibe llamada del taller                                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 10. RECEPCIONISTA: Entrega equipo al cliente                   │
│     → Cliente paga y recibe su laptop                           │
│     → Estado: LISTO_ENTREGA → ENTREGADO ✅                      │
└─────────────────────────────────────────────────────────────────┘
```

### Escenario Alternativo: Presupuesto Rechazado

```
... (pasos 1-3 iguales) ...
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 4. CLIENTE: Ve presupuesto de $500                              │
│    → Le parece muy caro                                         │
│    → Llama para rechazar                                        │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 5. RECEPCIONISTA: Registra rechazo                             │
│    → Motivo: "Presupuesto muy elevado"                          │
│    → Estado: PRESUPUESTADO → RECHAZADO                          │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 6. ADMINISTRADOR/RECEPCIONISTA: Cancela ticket                 │
│    → Estado: RECHAZADO → CANCELADO                              │
│    → Equipo disponible para retiro sin reparar                  │
└─────────────────────────────────────────────────────────────────┘
```

### Escenario: Pruebas Fallidas

```
... (pasos 1-7 iguales) ...
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 8. TÉCNICO: Realiza pruebas                                     │
│    → Equipo sigue sin encender correctamente ❌                 │
│    → exitoso: false                                             │
│    → Estado: EN_PRUEBA → EN_REPARACION (regresa)                │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 9. TÉCNICO: Identifica problema adicional                      │
│    → Registra nuevas observaciones                              │
│    → Completa segunda reparación                                │
│    → Estado: EN_REPARACION → EN_PRUEBA                          │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 10. TÉCNICO: Nuevas pruebas exitosas ✅                        │
│     → Estado: EN_PRUEBA → LISTO_ENTREGA                         │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📊 Roles y Responsabilidades

### Tabla de Permisos por Endpoint

| Endpoint | ADMIN | RECEPCIONISTA | TECNICO |
|----------|-------|---------------|---------|
| **Consulta Pública** | - | - | - |
| `GET /api/publico/tickets/{numero}` | ✅ SIN AUTH | ✅ SIN AUTH | ✅ SIN AUTH |
| **Tickets** |
| `GET /api/tickets` | ✅ | ✅ | ✅ |
| `GET /api/tickets/{id}` | ✅ | ✅ | ✅ |
| `POST /api/tickets` (crear) | ✅ | ✅ | ❌ |
| `PUT /api/tickets/{id}/asignar-tecnico` | ✅ | ✅ | ❌ |
| `PUT /api/tickets/{id}/diagnostico` | ✅ | ❌ | ✅ |
| `PUT /api/tickets/{id}/aprobar-presupuesto` | ✅ | ✅ | ❌ |
| `PUT /api/tickets/{id}/rechazar-presupuesto` | ✅ | ✅ | ❌ |
| `PUT /api/tickets/{id}/iniciar-reparacion` | ✅ | ❌ | ✅ |
| `PUT /api/tickets/{id}/observaciones` | ✅ | ❌ | ✅ |
| `PUT /api/tickets/{id}/completar-reparacion` | ✅ | ❌ | ✅ |
| `PUT /api/tickets/{id}/pruebas` | ✅ | ❌ | ✅ |
| `PUT /api/tickets/{id}/listo-entrega` | ✅ | ❌ | ✅ |
| `PUT /api/tickets/{id}/entregar` | ✅ | ✅ | ❌ |
| `PUT /api/tickets/{id}/cancelar` | ✅ | ❌ | ❌ |
| `GET /api/tickets/estadisticas` | ✅ | ❌ | ❌ |
| `GET /api/tickets/tecnico/{id}` | ✅ | ❌ | ✅ |
| **Clientes** |
| `GET /api/clientes` | ✅ | ✅ | ❌ |
| `POST /api/clientes` | ✅ | ✅ | ❌ |
| `PUT /api/clientes/{id}` | ✅ | ✅ | ❌ |
| `DELETE /api/clientes/{id}` | ✅ | ❌ | ❌ |

---

## 🎯 Casos de Uso Comunes

### Para el Cliente

**1. Consultar estado de mi reparación**
```
1. Ir a: https://tesig.com/consulta-ticket
2. Ingresar: TKT-20251105-0001
3. Ver estado actual y detalles
```

**2. Ver cuánto costará la reparación**
```
1. Esperar a que estado sea: PRESUPUESTADO
2. Consultar ticket
3. Ver: diagnóstico, presupuesto total, días estimados
4. Llamar al taller para decidir
```

**3. Saber si ya puedo recoger mi equipo**
```
1. Consultar ticket periódicamente
2. Cuando estado = LISTO_ENTREGA
3. Acudir al taller con el número de ticket
```

### Para la Recepcionista

**1. Recibir un equipo nuevo**
```
1. POST /api/tickets con datos del cliente y equipo
2. Anotar número de ticket generado
3. Entregárselo al cliente
```

**2. Asignar trabajo a técnico disponible**
```
1. GET /api/tickets/estado/INGRESADO
2. Ver tickets pendientes
3. PUT /api/tickets/{id}/asignar-tecnico
4. Seleccionar técnico con menos carga
```

**3. Procesar respuesta del cliente sobre presupuesto**
```
Cliente acepta:
PUT /api/tickets/{id}/aprobar-presupuesto

Cliente rechaza:
PUT /api/tickets/{id}/rechazar-presupuesto
{
  "motivoRechazo": "Muy caro"
}
```

**4. Entregar equipo reparado**
```
1. GET /api/tickets/estado/LISTO_ENTREGA
2. Cliente llega con su número
3. Verificar identidad
4. Cobrar
5. PUT /api/tickets/{id}/entregar
```

### Para el Técnico

**1. Ver mis trabajos asignados**
```
GET /api/tickets/tecnico/{miId}
o
GET /api/tickets/estado/EN_DIAGNOSTICO
```

**2. Hacer diagnóstico y cotizar**
```
1. Revisar equipo físicamente
2. PUT /api/tickets/{id}/diagnostico
3. Incluir: diagnóstico detallado, presupuesto, tiempo
```

**3. Reparar equipo aprobado**
```
1. PUT /api/tickets/{id}/iniciar-reparacion
2. Trabajar en la reparación
3. PUT /api/tickets/{id}/observaciones (opcional, múltiples veces)
4. PUT /api/tickets/{id}/completar-reparacion
```

**4. Probar y validar reparación**
```
1. Realizar pruebas de funcionamiento
2. PUT /api/tickets/{id}/pruebas
   - exitoso: true → Va a LISTO_ENTREGA
   - exitoso: false → Regresa a EN_REPARACION
```

### Para el Administrador

**1. Monitorear el taller**
```
GET /api/tickets/estadisticas

Ver:
- Total de tickets
- Tickets activos vs completados
- Distribución por estado
- Productividad por técnico
- Tiempo promedio de reparación
```

**2. Manejar situaciones especiales**
```
Cliente cancela:
PUT /api/tickets/{id}/cancelar

Eliminar cliente inactivo:
DELETE /api/clientes/{id}
(Solo si no tiene tickets activos)
```

**3. Búsquedas avanzadas**
```
Buscar cualquier cosa:
GET /api/tickets/buscar?q=samsung

Filtrar por estado específico:
GET /api/tickets/estado/EN_REPARACION

Ver tickets de un cliente:
GET /api/tickets/cliente/5
```

---

## 🔐 Seguridad

### Para el Cliente
- ✅ No necesita cuenta (menos fricciones)
- ✅ Solo necesita su número de ticket (único, difícil de adivinar)
- ✅ No puede modificar nada, solo consultar
- ✅ Acceso HTTPS encriptado

### Para el Taller
- 🔒 Autenticación JWT obligatoria
- 🔒 Refresh tokens con rotación
- 🔒 Permisos granulares por rol (@PreAuthorize)
- 🔒 Validación de tokens en cada request
- 🔒 Passwords hasheados con BCrypt
- 🔒 Sesiones con expiración (24h + refresh 7 días)
- 🔒 Registro de usuario y contraseña en logs de acciones

---

## 📱 Integraciones Futuras

### Para el Cliente
- 📧 **Email/SMS**: Notificaciones automáticas de cambios de estado
- 📱 **App Móvil**: Consulta desde smartphone
- 💳 **Pago Online**: Aprobar presupuesto y pagar en línea
- ⭐ **Valoración**: Calificar servicio al finalizar

### Para el Taller
- 📊 **Dashboard en Tiempo Real**: Visualización de métricas
- 📈 **Reportes Avanzados**: Análisis de rentabilidad, tendencias
- 🔔 **Notificaciones Push**: Alertas de tickets urgentes
- 💰 **Facturación Electrónica**: Integración con SAT
- 📦 **Inventario**: Control de piezas y repuestos

---

## ✅ Resumen

### ✨ Beneficios para el Cliente
1. **Simplicidad**: Solo un número de ticket
2. **Transparencia**: Ve todo el proceso en tiempo real
3. **Sin fricción**: No necesita crear cuenta
4. **Información clara**: Sabe qué esperar en cada momento
5. **Acceso 24/7**: Consulta cuando quiera

### 💼 Beneficios para el Taller
1. **Organización**: Flujo de trabajo estructurado
2. **Trazabilidad**: Historial completo de cada reparación
3. **Eficiencia**: Menos llamadas de clientes preguntando estado
4. **Control**: Permisos específicos por rol
5. **Métricas**: Estadísticas para mejorar el servicio
6. **Profesionalismo**: Imagen moderna y tecnológica

---

**Desarrollado con ❤️ aplicando principios SOLID y mejores prácticas de Spring Boot**
