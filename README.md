# InnoSistemas Backend

[![CI/CD Pipeline](https://github.com/JPablo0505/innosistemas-feature4-main/actions/workflows/build.yml/badge.svg)](https://github.com/JPablo0505/innosistemas-feature4-main/actions/workflows/build.yml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4&metric=bugs)](https://sonarcloud.io/summary/new_code?id=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4) [![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4&metric=coverage)](https://sonarcloud.io/summary/new_code?id=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4) [![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4) [![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=JAMESLEONARDGRIJALBABOLANOS_innosistemas-feature4)

Backend GraphQL para gestión de equipos, autenticación y notificaciones en tiempo real para la Universidad de Antioquia.

## Stack Tecnológico

- Java 17 + Spring Boot 3.2.0
- GraphQL API (Spring for GraphQL)
- Spring Security + JWT
- PostgreSQL (prod) / H2 (dev)
- WebSocket subscriptions
- Redis (opcional)

## Quick Start

### Requisitos

- Java 17+
- Maven 3.6+
- PostgreSQL 12+ (producción)

### Instalación

```bash
# Clonar repositorio
git clone <repository-url>
cd innosistemas-feature4

# Compilar
mvn clean install

# Ejecutar en desarrollo (H2 en memoria)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Configuración de Variables de Entorno

Crear archivo `.env` o configurar variables de sistema:

```bash
# Base de datos (producción)
DB_URL=jdbc:postgresql://localhost:5432/innosistemas
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your-256-bit-secret-key-here

# Redis (opcional)
REDIS_HOST=localhost
REDIS_PORT=6379
```

Ver `docs/DEPLOY.md` para configuración completa.

### Endpoints

```
GraphQL API:    http://localhost:8080/api/v1/graphql
GraphiQL IDE:   http://localhost:8080/api/v1/graphiql
Health Check:   http://localhost:8080/api/v1/actuator/health
H2 Console:     http://localhost:8080/api/v1/h2-console (dev only)
```

### Ejemplo de Uso

```graphql
# Login
mutation {
  login(email: "user@example.com", password: "password123") {
    token
    refreshToken
    userInfo {
      id
      email
      role
    }
  }
}

# Query autenticada (agregar header: Authorization: Bearer <token>)
query {
  getCurrentUser {
    id
    email
    fullName
    role
  }
}
```

## Documentación

### Esenciales

- **[docs/ESTADO_DEL_PROYECTO.md](docs/ESTADO_DEL_PROYECTO.md)** - Arquitectura completa y estado del proyecto
- **[docs/API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)** - Referencia completa de la API GraphQL
- **[docs/DEPLOY.md](docs/DEPLOY.md)** - Despliegue y configuración de producción

### Guías Técnicas

- **[docs/WEBSOCKET_GUIDE.md](docs/WEBSOCKET_GUIDE.md)** - WebSocket y subscriptions en tiempo real
- **[docs/TESTING_ACCEPTANCE_CRITERIA.md](docs/TESTING_ACCEPTANCE_CRITERIA.md)** - Tests y criterios de aceptación
- **[docs/GATEWAY_MIGRATION.md](docs/GATEWAY_MIGRATION.md)** - Migración futura a API Gateway

### Changelog

- **[CHANGELOG.md](CHANGELOG.md)** - Historial de versiones y cambios

## Características Principales

### Autenticación

- JWT con tokens de acceso (24h) y refresh (7 días)
- Logout con blacklist de tokens
- Gestión de sesiones multi-dispositivo

### Autorización

- Control basado en roles: STUDENT, PROFESSOR, ADMIN, TA
- Directivas GraphQL personalizadas: `@auth`, `@requiresTeam`, `@requiresCourse`
- Permisos granulares por operación

### API GraphQL

**Queries:**
- `getCurrentUser` - Info del usuario autenticado
- `getUserPermissions` - Permisos del usuario
- `getTeamMembers` - Miembros de un equipo

**Mutations:**
- `login`, `logout`, `refreshToken` - Autenticación
- `registerUser` - Registro de usuarios
- `createTeam`, `joinTeam`, `leaveTeam` - Gestión de equipos
- `inviteUserToTeam`, `deleteTeam` - Operaciones de equipo

**Subscriptions:**
- `onNotificationReceived` - Notificaciones en tiempo real
- `onTeamEvent` - Eventos de equipo (WebSocket)

### Seguridad

- Rate limiting: 100 req/min (normal), 10 req/min (auth)
- Security headers: CSP, HSTS, X-Frame-Options
- CORS configurado
- Validación de entrada con Bean Validation

### Monitoreo

- Spring Boot Actuator
- Métricas de Prometheus
- Health checks
- Logging estructurado

## Estructura del Proyecto

```
src/main/java/com/udea/innosistemas/
├── config/          # Configuración (Security, GraphQL, Redis)
├── dto/             # Data Transfer Objects
├── entity/          # Entidades JPA
├── repository/      # Repositorios Spring Data
├── resolver/        # GraphQL Resolvers
├── security/        # JWT, Filters, Directivas
├── service/         # Lógica de negocio
└── exception/       # Manejo de errores

src/main/resources/
├── application.yml  # Configuración principal
├── graphql/
│   └── schema.graphqls  # Schema GraphQL
└── db/migration/    # Scripts Flyway
```

## Perfiles de Configuración

### Development (`dev`)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

- Base de datos H2 en memoria
- H2 Console habilitada
- CORS: localhost:3000, localhost:8080
- DDL: create-drop

### Test (`test`)

```bash
mvn test
```

- Base de datos H2 en memoria
- Logging: DEBUG

### Production (`prod`)

```bash
java -jar target/innosistemas-1.0.0.jar --spring.profiles.active=prod
```

- PostgreSQL
- Redis cache habilitado
- DDL: validate
- Logging: INFO/WARN

## Testing

```bash
# Todos los tests
mvn test

# Tests de integración
mvn verify

# Reporte de cobertura
mvn clean test jacoco:report
```

## Troubleshooting

### Puerto 8080 en uso

```bash
export PORT=8081
mvn spring-boot:run
```

### Error de conexión a PostgreSQL

- Verificar que PostgreSQL esté corriendo
- Validar credenciales en variables de entorno
- Confirmar que existe la base de datos `innosistemas`

### JWT Secret no configurado

```bash
export JWT_SECRET="your-secure-256-bit-secret"
```

### Redis no disponible

Redis es opcional. El sistema funciona sin Redis usando cache en memoria:

```bash
export CACHE_TYPE=simple
```

## Migraciones de Base de Datos

Flyway se ejecuta automáticamente al iniciar:

- `V1__Create_users_table.sql` - Tabla de usuarios
- `V2__Add_user_team_course_fields.sql` - Campos adicionales

Para ejecutar manualmente:

```bash
mvn flyway:migrate
```

## Contribución

1. Fork el repositorio
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## Licencia

Universidad de Antioquia - Facultad de Ingeniería - Ingeniería de Sistemas

---

**Documentación completa:** Ver carpeta `/docs` para guías detalladas de API, deployment, testing y WebSocket.
