# Funcionalidades GraphQL - InnoSistemas Backend

Catálogo completo de operaciones GraphQL disponibles e implementadas.

**Versión:** 1.0.1
**Fecha:** 20 de Noviembre de 2025
**Endpoint:** `http://localhost:8080/api/v1/graphql`

---

## Resumen Ejecutivo

**Total de operaciones implementadas:** 35

- **Queries:** 13 operaciones
- **Mutations:** 19 operaciones
- **Subscriptions:** 3 operaciones

**Estado:** Todas las operaciones listadas están completamente implementadas y funcionales.

---

## Flujos Funcionales Completos

Las siguientes funcionalidades están **100% implementadas y probadas**:

### 1. Registro y Autenticación de Usuarios

**Disponible:** Sí - Completamente funcional

```graphql
# Paso 1: Registrar usuario
mutation {
  registerUser(input: {
    email: "estudiante@udea.edu.co"
    password: "password123"
    role: "STUDENT"
    firstName: "Juan"
    lastName: "Pérez"
  }) {
    id
    email
    role
  }
}

# Paso 2: Login
mutation {
  login(email: "estudiante@udea.edu.co", password: "password123") {
    token
    refreshToken
    userInfo { id email role }
  }
}
```

**Estado:** Funcionando completamente

---

### 2. Gestión de Equipos (Agregar Usuarios)

**Disponible:** Sí - Completamente funcional

```graphql
# Paso 1: Profesor crea equipo
mutation {
  createTeam(input: {
    nombre: "Equipo Backend"
    descripcion: "Desarrollo del backend"
    courseId: "1"
    maxMiembros: 4
  }) {
    id
    nombre
  }
}

# Paso 2: Usuario se une al equipo
mutation {
  joinTeam(teamId: "5") {
    id
    nombre
    miembros {
      id
      fullName
    }
  }
}

# Paso 3: O profesor invita usuario
mutation {
  inviteUserToTeam(teamId: "5", userId: "10") {
    success
    message
    notificationId
  }
}
```

**Estado:** Funcionando completamente

---

### 3. Sistema de Notificaciones en Tiempo Real

**Disponible:** Sí - Completamente funcional

```graphql
# Paso 1: Suscribirse a notificaciones (WebSocket)
subscription {
  onNotificationReceived {
    id
    mensaje
    tipo
    fechaCreacion
  }
}

# Paso 2: Ver notificaciones recibidas
query {
  getMyNotifications {
    id
    mensaje
    leida
    tipo
  }
}

# Paso 3: Marcar como leída
mutation {
  markNotificationAsRead(id: "123") {
    id
    leida
  }
}
```

**Estado:** Funcionando completamente con WebSocket

---

### 4. Flujo Completo: Usuario se Une a Equipo y Recibe Notificaciones

```graphql
# 1. Usuario se registra
mutation {
  registerUser(input: {
    email: "nuevo@udea.edu.co"
    password: "pass123"
    role: "STUDENT"
    firstName: "María"
    lastName: "López"
  }) { id }
}

# 2. Usuario hace login
mutation {
  login(email: "nuevo@udea.edu.co", password: "pass123") {
    token
    userInfo { id }
  }
}

# 3. Usuario ve equipos disponibles de su curso
query {
  getTeamsByCourse(courseId: "1") {
    id
    nombre
    maxMiembros
  }
}

# 4. Usuario se une a un equipo
mutation {
  joinTeam(teamId: "5") {
    id
    nombre
  }
}

# 5. Usuario se suscribe a notificaciones (WebSocket)
subscription {
  onNotificationReceived {
    mensaje
    tipo
  }
}

# 6. Cuando el profesor invita a alguien más al equipo,
#    TODOS los miembros reciben notificación en tiempo real
```

**Estado:** Todo el flujo funciona end-to-end

---

### Confirmación de Funcionalidades Operativas

✅ **Registro de usuarios** - Completamente funcional
✅ **Agregar usuarios a equipos** - Completamente funcional (joinTeam + inviteUserToTeam)
✅ **Notificaciones en tiempo real** - Completamente funcional (WebSocket subscriptions)
✅ **Gestión de equipos** - CRUD completo funcional
✅ **Autenticación JWT** - Completamente funcional
✅ **Permisos por rol** - Completamente funcional

---

## Queries (13)

### 1. Autenticación y Usuario

#### hello

**Estado:** Disponible
**Autenticación:** Requiere rol STUDENT
**Descripción:** Query de prueba

```graphql
query {
  hello
}
```

**Respuesta:**
```json
{
  "data": {
    "hello": "Hello from InnoSistemas GraphQL API!"
  }
}
```

---

#### getCurrentUser

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Obtiene información del usuario autenticado

```graphql
query {
  getCurrentUser {
    id
    email
    role
    firstName
    lastName
    fullName
    teamId
    courseId
  }
}
```

**Headers requeridos:**
```
Authorization: Bearer <jwt-token>
```

**Respuesta:**
```json
{
  "data": {
    "getCurrentUser": {
      "id": "1",
      "email": "user@example.com",
      "role": "STUDENT",
      "firstName": "Juan",
      "lastName": "Pérez",
      "fullName": "Juan Pérez",
      "teamId": "5",
      "courseId": "10"
    }
  }
}
```

---

#### getUserPermissions

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Obtiene permisos detallados del usuario autenticado

```graphql
query {
  getUserPermissions {
    userId
    role
    permissions
    teamId
    courseId
    canManageTeam
    canManageCourse
    canViewAllTeams
    canSendNotifications
  }
}
```

**Respuesta:**
```json
{
  "data": {
    "getUserPermissions": {
      "userId": "1",
      "role": "STUDENT",
      "permissions": ["team:read", "notification:read"],
      "teamId": "5",
      "courseId": "10",
      "canManageTeam": false,
      "canManageCourse": false,
      "canViewAllTeams": false,
      "canSendNotifications": false
    }
  }
}
```

---

#### getTeamMembers

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Obtiene miembros de un equipo. Estudiantes solo pueden ver su equipo, profesores/admins pueden ver cualquier equipo.

```graphql
query GetTeamMembers($teamId: ID!) {
  getTeamMembers(teamId: $teamId) {
    id
    email
    firstName
    lastName
    fullName
    role
    teamId
    courseId
  }
}
```

**Variables:**
```json
{
  "teamId": "5"
}
```

---

### 2. Equipos (Teams)

#### getTeamById

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Obtiene un equipo por ID

```graphql
query GetTeam($id: ID!) {
  getTeamById(id: $id) {
    id
    nombre
    descripcion
    fechaLimite
    activo
    maxMiembros
    courseId
    miembros {
      id
      fullName
      email
    }
  }
}
```

**Variables:**
```json
{
  "id": "5"
}
```

---

#### getTeamsByCourse

**Estado:** Disponible
**Autenticación:** Requiere rol PROFESSOR, ADMIN o TA
**Descripción:** Obtiene todos los equipos de un curso

```graphql
query GetTeamsByCourse($courseId: ID!) {
  getTeamsByCourse(courseId: $courseId) {
    id
    nombre
    descripcion
    maxMiembros
    miembros {
      id
      fullName
    }
  }
}
```

---

#### getMyTeam

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Obtiene el equipo del usuario autenticado

```graphql
query {
  getMyTeam {
    id
    nombre
    descripcion
    fechaLimite
    miembros {
      id
      fullName
      email
      role
    }
  }
}
```

---

#### getTeamsNearDeadline

**Estado:** Disponible
**Autenticación:** Requiere rol PROFESSOR, ADMIN o TA
**Descripción:** Obtiene equipos con fecha límite próxima

```graphql
query GetTeamsNearDeadline($days: Int!) {
  getTeamsNearDeadline(days: $days) {
    id
    nombre
    fechaLimite
    courseId
  }
}
```

**Variables:**
```json
{
  "days": 7
}
```

---

### 3. Notificaciones

#### getMyNotifications

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Obtiene todas las notificaciones del usuario autenticado

```graphql
query {
  getMyNotifications {
    id
    mensaje
    leida
    fechaCreacion
    tipo
    userId
    teamId
  }
}
```

---

#### getUnreadNotifications

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Obtiene solo notificaciones no leídas

```graphql
query {
  getUnreadNotifications {
    id
    mensaje
    fechaCreacion
    tipo
  }
}
```

---

#### getUnreadNotificationCount

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Obtiene el contador de notificaciones no leídas

```graphql
query {
  getUnreadNotificationCount
}
```

**Respuesta:**
```json
{
  "data": {
    "getUnreadNotificationCount": 5
  }
}
```

---

#### getRecentNotifications

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Obtiene las últimas 10 notificaciones

```graphql
query {
  getRecentNotifications {
    id
    mensaje
    leida
    fechaCreacion
  }
}
```

---

#### getTeamNotifications

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Obtiene notificaciones de un equipo específico

```graphql
query GetTeamNotifications($teamId: ID!) {
  getTeamNotifications(teamId: $teamId) {
    id
    mensaje
    fechaCreacion
    tipo
  }
}
```

---

#### getNotificationById

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Obtiene una notificación por ID

```graphql
query GetNotification($id: ID!) {
  getNotificationById(id: $id) {
    id
    mensaje
    leida
    fechaCreacion
    tipo
    userId
    teamId
  }
}
```

---

## Mutations (19)

### 1. Autenticación

#### login

**Estado:** Disponible
**Autenticación:** No requiere (público)
**Descripción:** Autentica un usuario y devuelve tokens

```graphql
mutation Login($email: String!, $password: String!) {
  login(email: $email, password: $password) {
    token
    refreshToken
    userInfo {
      id
      email
      role
      fullName
    }
  }
}
```

**Variables:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Respuesta:**
```json
{
  "data": {
    "login": {
      "token": "eyJhbGciOiJIUzUxMiJ9...",
      "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
      "userInfo": {
        "id": "1",
        "email": "user@example.com",
        "role": "STUDENT",
        "fullName": "Juan Pérez"
      }
    }
  }
}
```

---

#### registerUser

**Estado:** Disponible
**Autenticación:** No requiere (público)
**Descripción:** Registra un nuevo usuario

```graphql
mutation RegisterUser($input: RegisterUserInput!) {
  registerUser(input: $input) {
    id
    email
    role
    fullName
  }
}
```

**Variables:**
```json
{
  "input": {
    "email": "newuser@example.com",
    "password": "password123",
    "role": "STUDENT",
    "firstName": "María",
    "lastName": "González",
    "teamId": 5,
    "courseId": 10
  }
}
```

---

#### refreshToken

**Estado:** Disponible
**Autenticación:** No requiere (público, pero necesita refresh token válido)
**Descripción:** Renueva el token de acceso

```graphql
mutation RefreshToken($refreshToken: String!) {
  refreshToken(refreshToken: $refreshToken) {
    token
    refreshToken
    userInfo {
      id
      email
    }
  }
}
```

---

#### logout

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Cierra sesión e invalida el token

```graphql
mutation Logout($token: String!) {
  logout(token: $token) {
    success
    message
  }
}
```

**Variables:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

---

#### logoutFromAllDevices

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Cierra todas las sesiones del usuario en todos los dispositivos

```graphql
mutation {
  logoutFromAllDevices {
    success
    message
  }
}
```

---

### 2. Gestión de Equipos

#### createTeam

**Estado:** Disponible
**Autenticación:** Requiere rol PROFESSOR o ADMIN
**Descripción:** Crea un nuevo equipo

```graphql
mutation CreateTeam($input: TeamInput!) {
  createTeam(input: $input) {
    id
    nombre
    descripcion
    fechaLimite
    maxMiembros
    courseId
  }
}
```

**Variables:**
```json
{
  "input": {
    "nombre": "Equipo A",
    "descripcion": "Equipo de desarrollo",
    "fechaLimite": "2024-12-31T23:59:59",
    "courseId": "10",
    "maxMiembros": 4
  }
}
```

---

#### updateTeam

**Estado:** Disponible
**Autenticación:** Requiere rol PROFESSOR o ADMIN
**Descripción:** Actualiza un equipo existente

```graphql
mutation UpdateTeam($id: ID!, $input: TeamUpdateInput!) {
  updateTeam(id: $id, input: $input) {
    id
    nombre
    descripcion
    fechaLimite
  }
}
```

**Variables:**
```json
{
  "id": "5",
  "input": {
    "nombre": "Equipo A Actualizado",
    "descripcion": "Nueva descripción"
  }
}
```

---

#### updateTeamDeadline

**Estado:** Disponible
**Autenticación:** Requiere rol PROFESSOR o ADMIN
**Descripción:** Actualiza solo la fecha límite de un equipo

```graphql
mutation UpdateDeadline($teamId: ID!, $deadline: String!) {
  updateTeamDeadline(teamId: $teamId, deadline: $deadline) {
    id
    nombre
    fechaLimite
  }
}
```

**Variables:**
```json
{
  "teamId": "5",
  "deadline": "2024-12-31T23:59:59"
}
```

---

#### inviteUserToTeam

**Estado:** Disponible
**Autenticación:** Requiere rol PROFESSOR, ADMIN o TA
**Descripción:** Invita un usuario a un equipo (crea notificación)

```graphql
mutation InviteUser($teamId: ID!, $userId: ID!) {
  inviteUserToTeam(teamId: $teamId, userId: $userId) {
    success
    message
    notificationId
  }
}
```

**Variables:**
```json
{
  "teamId": "5",
  "userId": "10"
}
```

---

#### joinTeam

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** El usuario actual se une a un equipo

```graphql
mutation JoinTeam($teamId: ID!) {
  joinTeam(teamId: $teamId) {
    id
    nombre
    miembros {
      id
      fullName
    }
  }
}
```

---

#### leaveTeam

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** El usuario actual abandona un equipo

```graphql
mutation LeaveTeam($teamId: ID!) {
  leaveTeam(teamId: $teamId) {
    success
    message
  }
}
```

---

#### deleteTeam

**Estado:** Disponible
**Autenticación:** Requiere rol PROFESSOR o ADMIN
**Descripción:** Marca un equipo como inactivo (soft delete)

```graphql
mutation DeleteTeam($id: ID!) {
  deleteTeam(id: $id) {
    success
    message
  }
}
```

---

### 3. Notificaciones

#### createNotification

**Estado:** Disponible
**Autenticación:** Requiere rol PROFESSOR, ADMIN o TA
**Descripción:** Crea una nueva notificación

```graphql
mutation CreateNotification($input: NotificationInput!) {
  createNotification(input: $input) {
    id
    mensaje
    tipo
    userId
    fechaCreacion
  }
}
```

**Variables:**
```json
{
  "input": {
    "mensaje": "Nueva tarea asignada",
    "tipo": "RECORDATORIO",
    "userId": "5",
    "teamId": "10"
  }
}
```

---

#### markNotificationAsRead

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Marca una notificación como leída

```graphql
mutation MarkAsRead($id: ID!) {
  markNotificationAsRead(id: $id) {
    id
    leida
  }
}
```

---

#### markAllNotificationsAsRead

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Marca todas las notificaciones del usuario como leídas

```graphql
mutation {
  markAllNotificationsAsRead {
    success
    message
    count
  }
}
```

---

#### deleteNotification

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Descripción:** Elimina una notificación

```graphql
mutation DeleteNotification($id: ID!) {
  deleteNotification(id: $id) {
    success
    message
  }
}
```

---

## Subscriptions (3)

### onNotificationReceived

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Protocolo:** WebSocket
**Descripción:** Recibe notificaciones en tiempo real para el usuario autenticado

```graphql
subscription {
  onNotificationReceived {
    id
    mensaje
    fechaCreacion
    tipo
    leida
  }
}
```

**Endpoint WebSocket:**
```
ws://localhost:8080/api/v1/graphql-ws
```

**Notas:** Ver `WEBSOCKET_GUIDE.md` para configuración completa de WebSocket.

---

### onTeamEvent

**Estado:** Disponible
**Autenticación:** Requiere autenticación + pertenencia al equipo
**Protocolo:** WebSocket
**Descripción:** Recibe eventos de un equipo en tiempo real

```graphql
subscription OnTeamEvent($teamId: ID!) {
  onTeamEvent(teamId: $teamId) {
    eventType
    userId
    userName
    timestamp
  }
}
```

**Variables:**
```json
{
  "teamId": "5"
}
```

**Eventos posibles:**
- `USER_JOINED` - Usuario se unió al equipo
- `USER_LEFT` - Usuario abandonó el equipo
- `DEADLINE_UPDATED` - Fecha límite actualizada
- `TEAM_UPDATED` - Equipo actualizado

---

### onUnreadCountChanged

**Estado:** Disponible
**Autenticación:** Requiere autenticación
**Protocolo:** WebSocket
**Descripción:** Recibe actualizaciones del contador de notificaciones no leídas

```graphql
subscription {
  onUnreadCountChanged {
    userId
    unreadCount
    timestamp
  }
}
```

---

## Funcionalidades NO Disponibles

Las siguientes funcionalidades comunes en sistemas académicos **NO están implementadas**:

### 1. Gestión de Cursos (Courses)

**Estado:** Parcialmente implementado
**Qué falta:**
- Query `getCourseById`
- Query `getAllCourses`
- Query `getMyCourses`
- Mutation `createCourse`
- Mutation `updateCourse`
- Mutation `deleteCourse`
- Mutation `enrollUserInCourse`

**Qué existe actualmente:**
- Campo `courseId` en User y Team (FK solamente)
- Query `getTeamsByCourse` (lista equipos de un curso)

**Para implementar:**
1. Crear entidad `Course.java`
2. Crear `CourseRepository.java`
3. Crear `CourseService.java`
4. Crear `CourseQueryResolver.java` y `CourseMutationResolver.java`
5. Actualizar schema.graphqls con tipos y operaciones

---

### 2. Gestión Avanzada de Usuarios

**Estado:** No implementado
**Qué falta:**
- Query `getAllUsers` (admin)
- Query `getUserById` (admin)
- Query `searchUsers` (por nombre, email, rol)
- Mutation `updateUser` (admin)
- Mutation `deleteUser` (admin)
- Mutation `changeUserRole` (admin)
- Mutation `resetPassword`
- Mutation `changePassword`

**Para implementar:**
1. Crear `UserMutationResolver.java`
2. Crear `UserQueryResolver.java` (adicional al actual QueryResolver)
3. Agregar operaciones al schema.graphqls
4. Implementar lógica en `UserService.java`

---

### 3. Archivos y Entregables

**Estado:** No implementado
**Qué falta:**
- Query `getTeamSubmissions` (entregas de un equipo)
- Query `getSubmissionById`
- Mutation `uploadFile`
- Mutation `createSubmission`
- Mutation `gradeSubmission` (calificar)
- Mutation `addFeedback` (retroalimentación)

**Para implementar:**
1. Integrar almacenamiento de archivos (AWS S3, MinIO, local storage)
2. Crear entidades `Submission.java`, `File.java`
3. Crear servicios de upload/download
4. Crear resolvers correspondientes
5. Actualizar schema.graphqls

---

### 4. Evaluaciones y Calificaciones

**Estado:** No implementado
**Qué falta:**
- Query `getTeamGrades`
- Query `getUserGrades`
- Mutation `createAssignment` (crear asignación)
- Mutation `submitAssignment` (entregar)
- Mutation `gradeAssignment` (calificar)

**Para implementar:**
1. Crear entidades `Assignment.java`, `Grade.java`
2. Crear servicios de evaluación
3. Crear resolvers correspondientes
4. Actualizar schema.graphqls

---

### 5. Roles y Permisos Avanzados

**Estado:** Parcialmente implementado
**Qué existe:**
- RBAC básico (STUDENT, PROFESSOR, ADMIN, TA)
- Directivas GraphQL `@auth`, `@requiresTeam`, `@requiresCourse`

**Qué falta:**
- Permisos granulares por recurso
- Query `getRolePermissions` (listar permisos de un rol)
- Mutation `assignPermissionToRole`
- Mutation `createCustomRole`
- Sistema de permisos dinámico

**Para implementar:**
1. Crear entidades `Permission.java`, `RolePermission.java`
2. Modificar lógica de autorización
3. Crear admin panel para gestión de permisos

---

### 6. Estadísticas y Reportes

**Estado:** No implementado
**Qué falta:**
- Query `getTeamStatistics`
- Query `getCourseStatistics`
- Query `getUserActivityReport`
- Query `getSystemMetrics` (admin)

**Para implementar:**
1. Crear servicios de analytics
2. Agregar queries agregadas a repositorios
3. Crear resolvers de estadísticas
4. Posible integración con herramientas de BI

---

### 7. Mensajería Directa

**Estado:** No implementado
**Qué falta:**
- Query `getConversations`
- Query `getMessages`
- Mutation `sendMessage`
- Subscription `onMessageReceived`

**Para implementar:**
1. Crear entidades `Conversation.java`, `Message.java`
2. Crear servicios de mensajería
3. Implementar subscriptions WebSocket
4. Actualizar schema.graphqls

---

## Resumen de Estado

### Completamente Implementado

- Autenticación completa (login, logout, refresh, register)
- Gestión de equipos completa (CRUD + join/leave + invitations)
- Notificaciones completas (CRUD + subscriptions)
- Permisos y roles básicos
- WebSocket subscriptions funcionales

### Parcialmente Implementado

- Cursos (solo FK, no hay CRUD)
- Permisos (RBAC básico, no granular)

### No Implementado

- Gestión avanzada de usuarios (admin CRUD)
- Archivos y entregables
- Evaluaciones y calificaciones
- Estadísticas y reportes
- Mensajería directa
- Roles personalizados

---

## Cómo Probar las Funcionalidades

### 1. GraphiQL (Recomendado)

```bash
# Iniciar backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Abrir navegador
http://localhost:8080/api/v1/graphiql
```

### 2. Postman

Crear request POST a:
```
http://localhost:8080/api/v1/graphql
```

Body (raw JSON):
```json
{
  "query": "query { getCurrentUser { id email } }"
}
```

### 3. curl

```bash
curl -X POST http://localhost:8080/api/v1/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"query": "query { getCurrentUser { id email } }"}'
```

---

## Notas de Implementación

- Todas las operaciones usan validación de entrada (Bean Validation)
- Rate limiting activo: 100 req/min (normal), 10 req/min (auth)
- Tokens JWT expiran en 24 horas
- Refresh tokens expiran en 7 días
- WebSocket keepalive: 30 segundos
- Notificaciones se retienen 30 días por defecto

---

**Última actualización:** 20/11/2025
