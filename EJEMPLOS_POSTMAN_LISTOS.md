# 🚀 EJEMPLOS LISTOS PARA COPIAR Y PEGAR EN POSTMAN

## ⚙️ CONFIGURACIÓN POSTMAN
- **Método:** POST
- **URL:** `http://localhost:8080/api/v1/graphql`
- **Header:** `Content-Type: application/json`
- **Body:** Seleccionar **raw** y **JSON**

---

## ✅ EJEMPLO 1: Usuario demo1@example.com (CORREGIDO)

**Copia TODO esto en el Body de Postman:**

```json
{
  "query": "mutation RegisterUser($input: RegisterUserInput!) { registerUser(input: $input) { id email role firstName lastName fullName teamId courseId } }",
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

---

## ✅ EJEMPLO 2: Usuario demo@example.com

**Copia TODO esto en el Body de Postman:**

```json
{
  "query": "mutation RegisterUser($input: RegisterUserInput!) { registerUser(input: $input) { id email role firstName lastName fullName teamId courseId } }",
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

---

## ✅ EJEMPLO 3: Profesor

**Copia TODO esto en el Body de Postman:**

```json
{
  "query": "mutation RegisterUser($input: RegisterUserInput!) { registerUser(input: $input) { id email role firstName lastName fullName teamId courseId } }",
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

---

## ✅ EJEMPLO 4: Administrador

**Copia TODO esto en el Body de Postman:**

```json
{
  "query": "mutation RegisterUser($input: RegisterUserInput!) { registerUser(input: $input) { id email role firstName lastName fullName teamId courseId } }",
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

---

## ✅ EJEMPLO 5: Estudiante con Team y Course

**Copia TODO esto en el Body de Postman:**

```json
{
  "query": "mutation RegisterUser($input: RegisterUserInput!) { registerUser(input: $input) { id email role firstName lastName fullName teamId courseId } }",
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

---

## 📋 RESPUESTA ESPERADA

Si todo funciona bien, recibirás:

```json
{
  "data": {
    "registerUser": {
      "id": "1",
      "email": "demo1@example.com",
      "role": "STUDENT",
      "firstName": "Demo",
      "lastName": "Usuario",
      "fullName": "Demo Usuario",
      "teamId": null,
      "courseId": null
    }
  }
}
```

---

## ⚠️ CAMPOS OBLIGATORIOS

**IMPORTANTE:** Estos campos SON OBLIGATORIOS:
- ✅ `email` - Email válido
- ✅ `password` - Mínimo 8 caracteres
- ✅ `role` - STUDENT, TEACHER o ADMIN (mayúsculas)
- ✅ `firstName` - Nombre
- ✅ `lastName` - Apellido

**Campos opcionales:**
- ⭕ `teamId` - ID del equipo (número)
- ⭕ `courseId` - ID del curso (número)

---

## 🔐 HACER LOGIN DESPUÉS DE CREAR USUARIO

Una vez creado el usuario, haz login así:

```json
{
  "query": "mutation Login($email: String!, $password: String!) { login(email: $email, password: $password) { token refreshToken userInfo { id email role firstName lastName fullName } } }",
  "variables": {
    "email": "demo1@example.com",
    "password": "password123"
  }
}
```

---

## 💡 TIPS

1. **Usa variables de GraphQL** (como en los ejemplos) en lugar de poner los valores directamente en el query
2. **El rol debe estar en MAYÚSCULAS:** STUDENT, TEACHER, ADMIN
3. **La contraseña se encripta automáticamente** con BCrypt
4. **No necesitas token JWT** para crear usuarios
5. **El email debe ser único** - si ya existe, recibirás un error

---

## ❌ ERRORES COMUNES Y SOLUCIONES

### Error: "missing required fields '[firstName, lastName]'"
**Solución:** Agrega firstName y lastName al input

### Error: "El email ya está registrado"
**Solución:** Usa un email diferente

### Error: "Rol inválido"
**Solución:** Usa STUDENT, TEACHER o ADMIN en MAYÚSCULAS

### Error: "Password debe tener al menos 8 caracteres"
**Solución:** Usa una contraseña más larga (mínimo 8 caracteres)

---

## ✅ PASOS RÁPIDOS

1. Abre Postman
2. POST → `http://localhost:8080/api/v1/graphql`
3. Headers → `Content-Type: application/json`
4. Body → raw → JSON
5. Copia uno de los ejemplos de arriba
6. Click en **Send**
7. ¡Listo! 🎉

