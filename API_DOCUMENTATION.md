# Documentación de la API para Integración de Frontend

Este documento proporciona la información necesaria para integrar un cliente de frontend con la API del backend de InnoSistemas.

## API REST

La API REST se utiliza exclusivamente para la autenticación inicial.

### Endpoint: `POST /auth/login`

-   **Descripción**: Autentica a un usuario y devuelve tokens de acceso y de refresco.
-   **URL**: `/auth/login`
-   **Método**: `POST`
-   **Content-Type**: `application/json`

#### **Request Body**

```json
{
  "email": "user@example.com",
  "password": "your_password"
}
```

#### **Success Response (200 OK)**

```json
{
  "token": "string (JWT de acceso de corta duración)",
  "refreshToken": "string (Token para renovar el JWT de acceso)",
  "userInfo": {
    "id": "number (ID único del usuario)",
    "email": "string",
    "role": "string (STUDENT, PROFESSOR, ADMIN, TA)",
    "teamId": "number (ID del equipo al que pertenece, opcional)",
    "courseId": "number (ID del curso al que está inscrito, opcional)",
    "firstName": "string (opcional)",
    "lastName": "string (opcional)",
    "fullName": "string (opcional)"
  }
}
```

---

## API GraphQL

El núcleo de la funcionalidad de la aplicación se expone a través de GraphQL.

-   **Endpoint**: `/graphql` (para todas las queries, mutations y subscriptions)
-   **Autenticación**: Todas las operaciones (excepto `login` y `refreshToken`) requieren un header `Authorization` con el token JWT.
    -   `Authorization: Bearer <token>`

### Tipos de Datos Principales (Estructuras)

Estas son las estructuras de datos clave que la API devuelve.

#### `UserInfo`
Representa los datos de un usuario.
```graphql
type UserInfo {
    id: ID!
    email: String!
    role: UserRole!
    teamId: ID
    courseId: ID
    firstName: String
    lastName: String
    fullName: String
}
```

#### `TeamDTO`
Representa un equipo de trabajo.
```graphql
# Estructura inferida de los resolvers
type TeamDTO {
    id: ID!
    nombre: String!
    descripcion: String
    fechaLimite: String # Formato ISO 8601 (ej: "2024-12-31T23:59:59")
    activo: Boolean!
    maxMiembros: Int
    courseId: ID!
    miembros: [TeamMember!]
}
```

#### `NotificationDTO`
Representa una notificación para un usuario.
```graphql
# Estructura inferida de los resolvers
type NotificationDTO {
    id: ID!
    mensaje: String!
    leida: Boolean!
    fechaCreacion: String # Formato ISO 8601
    tipo: String # (ej: "INVITACION", "RECORDATORIO")
    userId: ID!
    teamId: ID
}
```

#### `UserPermissions`
Detalla los permisos específicos de un usuario.
```graphql
type UserPermissions {
    userId: ID!
    role: UserRole!
    permissions: [String!]!
    teamId: ID
    courseId: ID
    canManageTeam: Boolean!
    canManageCourse: Boolean!
    canViewAllTeams: Boolean!
    canSendNotifications: Boolean!
}
```

### Enumeraciones (Valores Permitidos)

#### `UserRole`
Define los roles de usuario en el sistema.
- `STUDENT`
- `PROFESSOR`
- `ADMIN`
- `TA` (Teaching Assistant)

---

### Operaciones GraphQL

#### Queries (Consultas)

Para obtener datos del servidor.

```graphql
# Obtener datos básicos del usuario autenticado
query GetCurrentUser {
  getCurrentUser {
    id
    email
    role
    fullName
  }
}

# Obtener los permisos detallados del usuario
query GetUserPermissions {
  getUserPermissions {
    canManageTeam
    canManageCourse
    permissions
  }
}

# Obtener los miembros de un equipo específico
query GetTeamMembers($teamId: ID!) {
  getTeamMembers(teamId: $teamId) {
    id
    fullName
    email
  }
}

# Obtener las notificaciones del usuario autenticado
query GetMyNotifications {
    getMyNotifications {
        id
        mensaje
        leida
        fechaCreacion
    }
}

# Obtener el equipo del usuario autenticado
query GetMyTeam {
    getMyTeam {
        id
        nombre
        miembros {
            id
            fullName
        }
    }
}
```

#### Mutations (Modificaciones)

Para crear, actualizar o eliminar datos.

```graphql
# Unirse a un equipo (requiere autenticación)
mutation JoinTeam($teamId: ID!) {
  joinTeam(teamId: $teamId) {
    id
    nombre
  }
}

# Crear un equipo (solo roles autorizados)
# El input es un Map<String, Object>, no un tipo GraphQL estricto.
mutation CreateTeam($input: TeamInput!) {
    createTeam(input: $input) {
        id
        nombre
    }
}
# Ejemplo de variables para CreateTeam:
# { "input": { "nombre": "Nuevo Equipo", "descripcion": "Descripción del equipo", "courseId": 1, "maxMiembros": 4 } }


# Marcar una notificación como leída
mutation MarkAsRead($notificationId: ID!) {
  markNotificationAsRead(id: $notificationId) {
    id
    leida
  }
}
```

#### Subscriptions (Suscripciones en Tiempo Real)

Para recibir actualizaciones en tiempo real a través de WebSockets.

```graphql
# Suscribirse a nuevas notificaciones para el usuario autenticado
subscription OnNotificationReceived {
  onNotificationReceived {
    id
    mensaje
    fechaCreacion
    tipo
  }
}

# Suscribirse a eventos de un equipo (ej: un usuario se une o abandona)
subscription OnTeamEvent($teamId: ID!) {
  onTeamEvent(teamId: $teamId) {
    # El payload es un Map, su estructura depende del evento.
    # Ejemplo de payload recibido: { "eventType": "USER_JOINED", "userId": 123, "userName": "John Doe" }
  }
}
```
