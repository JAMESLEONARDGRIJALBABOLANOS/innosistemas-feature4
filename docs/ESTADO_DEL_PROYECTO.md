# Estado del Proyecto InnoSistemas Backend
## Funcionalidades Implementadas

### 1. Autenticación y Sesiones
- Login con email/password
- JWT tokens (acceso + refresh)
- Logout con blacklist de tokens
- Logout de todos los dispositivos
- Gestión de sesiones activas
- Registro de usuarios sin autenticación

### 2. Autorización Basada en Roles (RBAC)
- **STUDENT:** Acceso básico
- **PROFESSOR:** Gestión de cursos y equipos
- **ADMIN:** Acceso completo
- **TA:** Asistente con permisos limitados

### 3. Directivas GraphQL Personalizadas
- `@auth` - Requiere autenticación JWT
- `@requiresTeam` - Acceso solo a miembros del equipo
- `@requiresCourse` - Acceso solo a miembros del curso

### 4. Gestión de Equipos
- Creación y actualización
- Agregar/remover miembros (máx 3, mín 2)
- Listar miembros
- Eventos de equipo

### 5. Notificaciones en Tiempo Real
- Subscripciones GraphQL vía WebSocket
- Tipos: email, in-app, eventos de equipo
- Retención configurable (30 días)
- Contador de no leídas

### 6. Seguridad Avanzada
- Rate limiting configurable
- Headers de seguridad (CSP, HSTS, X-Frame-Options)
- CORS configurado
- Validación de entrada (email, contraseña mín 8 chars)

### 7. Monitoreo y Observabilidad
- Actuator endpoints: health, info, metrics, prometheus
- Logging estructurado con rotación
- Métricas de Prometheus

---

## API GraphQL

### Queries Disponibles
- `hello` - Test básico
- `getCurrentUser` - Información del usuario autenticado
- `getUserPermissions` - Permisos detallados
- `getTeamMembers(teamId)` - Miembros de un equipo

### Mutations Disponibles
- `login(email, password)` - Autenticación
- `refreshToken(refreshToken)` - Renovar tokens
- `registerUser(input)` - Registro público
- `logout(token)` - Cerrar sesión
- `logoutFromAllDevices` - Cerrar todas las sesiones
- `createNotification(...)` - Crear notificación
- `publishTeamEvent(...)` - Publicar evento de equipo

### Subscriptions Disponibles
- `onNotificationReceived` - Notificaciones en tiempo real
- `onTeamEventPublished` - Eventos de equipo en tiempo real

---

## Configuración de Perfiles

### Desarrollo (dev)
- Base de datos: H2 en memoria
- Console H2: `/h2-console`
- DDL: update
- Logging: DEBUG
- GraphiQL: habilitado
- CORS: localhost:3000, localhost:8080

### Testing (test)
- Base de datos: H2 en memoria
- DDL: create-drop
- Logging: DEBUG
- Aislamiento total

### Producción (prod)
- Base de datos: PostgreSQL (Supabase)
- DDL: validate
- Redis: habilitado
- Logging: INFO/WARN
- CORS: variable de entorno
- Health checks activos

---

## CI/CD Pipeline

### Triggers
- Push a `main` → Deploy completo
- Pull Request → Tests + SonarCloud
- Manual → Workflow manual

### Etapas
1. **Tests Unitarios** - JUnit 5
2. **SonarCloud Analysis** - Calidad de código
3. **Build** - Maven package
4. **Docker Build & Push** - Imagen a Docker Hub
5. **Deploy** - Render automático

---

