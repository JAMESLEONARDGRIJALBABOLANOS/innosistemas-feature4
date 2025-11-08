# Guía para Crear Usuarios con GraphQL usando Postman

## ⚠️ IMPORTANTE - CAMBIOS REALIZADOS

Se ha **configurado el sistema de seguridad** para permitir el registro de usuarios sin autenticación.

**Cambios aplicados:**
1. ✅ Endpoint `registerUser` agregado al schema GraphQL
2. ✅ Servicio de registro de usuarios creado
3. ✅ **Interceptor de seguridad actualizado** para permitir `registerUser` sin token JWT
4. ✅ Contraseñas se encriptan automáticamente con BCrypt

**Ahora puedes crear usuarios sin recibir error 401 Unauthorized** 🎉

---

## 🚀 ENDPOINT

```
POST http://localhost:8080/api/v1/graphql
```

## 📝 CONFIGURACIÓN EN POSTMAN

### 1. Headers (Encabezados)
Agrega el siguiente header:
```
Content-Type: application/json
```

### 2. Body (Cuerpo de la petición)
Selecciona: **raw** y **JSON**

## 📋 EJEMPLOS DE USO

### ✅ Crear Usuario ESTUDIANTE (STUDENT)

```json
{
  "query": "mutation RegisterUser($input: RegisterUserInput!) { registerUser(input: $input) { id email role firstName lastName teamId courseId fullName } }",
  "variables": {
    "input": {
      "email": "demo@example.com",
      "password": "demo1234",
      "role": "STUDENT",
      "firstName": "Demo",
      "lastName": "User"
    }
  }
}
```

### ✅ Crear Usuario PROFESOR (TEACHER)

```json
{
  "query": "mutation RegisterUser($input: RegisterUserInput!) { registerUser(input: $input) { id email role firstName lastName teamId courseId fullName } }",
  "variables": {
    "input": {
      "email": "profesor@example.com",
      "password": "profesor1234",
      "role": "TEACHER",
      "firstName": "Juan",
      "lastName": "Pérez"
    }
  }
}
```

### ✅ Crear Usuario ADMINISTRADOR (ADMIN)

```json
{
  "query": "mutation RegisterUser($input: RegisterUserInput!) { registerUser(input: $input) { id email role firstName lastName teamId courseId fullName } }",
  "variables": {
    "input": {
      "email": "admin@example.com",
      "password": "admin1234",
      "role": "ADMIN",
      "firstName": "Admin",
      "lastName": "Sistema"
    }
  }
}
```

### ✅ Crear Usuario con Team y Course IDs

```json
{
  "query": "mutation RegisterUser($input: RegisterUserInput!) { registerUser(input: $input) { id email role firstName lastName teamId courseId fullName } }",
  "variables": {
    "input": {
      "email": "estudiante@example.com",
      "password": "estudiante1234",
      "role": "STUDENT",
      "firstName": "María",
      "lastName": "García",
      "teamId": 1,
      "courseId": 1
    }
  }
}
```

## 📖 PASOS DETALLADOS EN POSTMAN

1. **Abre Postman**

2. **Crea una nueva petición:**
   - Click en "New" → "HTTP Request"

3. **Configura el método y URL:**
   - Método: **POST**
   - URL: `http://localhost:8080/api/v1/graphql`

4. **Configura los Headers:**
   - Click en la pestaña "Headers"
   - Agrega: `Content-Type` = `application/json`

5. **Configura el Body:**
   - Click en la pestaña "Body"
   - Selecciona: **raw**
   - En el dropdown de la derecha selecciona: **JSON**
   - Pega uno de los ejemplos de arriba

6. **Envía la petición:**
   - Click en el botón **Send**

## ✅ RESPUESTA EXITOSA

Si todo sale bien, recibirás algo como esto:

```json
{
  "data": {
    "registerUser": {
      "id": "1",
      "email": "demo@example.com",
      "role": "STUDENT",
      "firstName": "Demo",
      "lastName": "User",
      "teamId": null,
      "courseId": null,
      "fullName": "Demo User"
    }
  }
}
```

## ❌ POSIBLES ERRORES

### Email ya registrado:
```json
{
  "errors": [
    {
      "message": "El email ya está registrado: demo@example.com"
    }
  ]
}
```

### Rol inválido:
```json
{
  "errors": [
    {
      "message": "Rol inválido: INVALID_ROLE. Los valores permitidos son: STUDENT, TEACHER, ADMIN"
    }
  ]
}
```

### Contraseña muy corta:
```json
{
  "errors": [
    {
      "message": "Password debe tener al menos 8 caracteres"
    }
  ]
}
```

## 🔐 DESPUÉS DE CREAR EL USUARIO

### Hacer Login con el usuario creado:

```json
{
  "query": "mutation Login($email: String!, $password: String!) { login(email: $email, password: $password) { token refreshToken userInfo { id email role firstName lastName fullName } } }",
  "variables": {
    "email": "demo@example.com",
    "password": "demo1234"
  }
}
```

## 📌 NOTAS IMPORTANTES

1. **La contraseña se encripta automáticamente** con BCrypt antes de guardarla en la base de datos
2. **Los roles válidos son:** STUDENT, TEACHER, ADMIN (en mayúsculas)
3. **Campos obligatorios:** email, password, role, firstName, lastName
4. **Campos opcionales:** teamId, courseId
5. **La contraseña debe tener mínimo 8 caracteres**
6. **El email debe ser válido** (formato email@domain.com)

## 🎯 CREAR EL USUARIO DEMO ESPECÍFICO

Para crear exactamente el usuario que necesitas (demo@example.com con contraseña demo1234):

```json
{
  "query": "mutation RegisterUser($input: RegisterUserInput!) { registerUser(input: $input) { id email role firstName lastName fullName } }",
  "variables": {
    "input": {
      "email": "demo@example.com",
      "password": "demo1234",
      "role": "STUDENT",
      "firstName": "Demo",
      "lastName": "User"
    }
  }
}
```

**Copia y pega exactamente este JSON en el Body de Postman y presiona Send.**

---

## ⚠️ ERROR COMÚN: Campos Faltantes

Si recibes este error:
```
"argument 'input' is missing required fields '[firstName, lastName]'"
```

**CAUSA:** Olvidaste incluir `firstName` y `lastName` en el input.

**SOLUCIÓN:** Asegúrate de incluir TODOS los campos obligatorios:
- ✅ `email` (obligatorio)
- ✅ `password` (obligatorio)
- ✅ `role` (obligatorio)
- ✅ `firstName` (obligatorio) ⚠️
- ✅ `lastName` (obligatorio) ⚠️
- ⭕ `teamId` (opcional)
- ⭕ `courseId` (opcional)

**Ejemplo INCORRECTO (falta firstName y lastName):**
```json
{
  "query": "mutation RegisterUser { registerUser(input: { email: \"demo1@example.com\", password: \"password123\", role: \"student\"}) { id email role } }"
}
```

**Ejemplo CORRECTO:**
```json
{
  "query": "mutation RegisterUser($input: RegisterUserInput!) { registerUser(input: $input) { id email role firstName lastName fullName } }",
  "variables": {
    "input": {
      "email": "demo1@example.com",
      "password": "password123",
      "role": "STUDENT",
      "firstName": "Demo",
      "lastName": "Usuario"
    }
  }
}
```

