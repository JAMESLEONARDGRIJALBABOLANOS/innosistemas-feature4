# 🚀 FORMATO DIRECTO PARA GRAPHQL EN POSTMAN

## 📝 CONFIGURACIÓN EN POSTMAN

1. **Método:** POST
2. **URL:** `http://localhost:8080/api/v1/graphql`
3. **Pestaña:** Selecciona **GraphQL** (no Body/JSON)
4. **En el editor de GraphQL:** Pega la mutación de abajo

---

## ✅ FORMATO CORRECTO (CON firstName Y lastName)

### Ejemplo 1: Usuario demo1@example.com

```graphql
mutation RegisterUser {
  registerUser(input: {
    email: "demo1@example.com"
    password: "password123"
    role: "STUDENT"
    firstName: "Demo"
    lastName: "Usuario"
  }) {
    id
    email
    role
    teamId
    courseId
    firstName
    lastName
    fullName
  }
}
```

---

### Ejemplo 2: Usuario demo@example.com

```graphql
mutation RegisterUser {
  registerUser(input: {
    email: "demo@example.com"
    password: "demo1234"
    role: "STUDENT"
    firstName: "Demo"
    lastName: "User"
  }) {
    id
    email
    role
    teamId
    courseId
    firstName
    lastName
    fullName
  }
}
```

---

### Ejemplo 3: Profesor

```graphql
mutation RegisterUser {
  registerUser(input: {
    email: "profesor@example.com"
    password: "profesor1234"
    role: "TEACHER"
    firstName: "Juan"
    lastName: "Pérez"
  }) {
    id
    email
    role
    teamId
    courseId
    firstName
    lastName
    fullName
  }
}
```

---

### Ejemplo 4: Administrador

```graphql
mutation RegisterUser {
  registerUser(input: {
    email: "admin@example.com"
    password: "admin1234"
    role: "ADMIN"
    firstName: "Admin"
    lastName: "Sistema"
  }) {
    id
    email
    role
    teamId
    courseId
    firstName
    lastName
    fullName
  }
}
```

---

### Ejemplo 5: Estudiante con teamId y courseId

```graphql
mutation RegisterUser {
  registerUser(input: {
    email: "estudiante@example.com"
    password: "estudiante123"
    role: "STUDENT"
    firstName: "María"
    lastName: "García"
    teamId: 1
    courseId: 1
  }) {
    id
    email
    role
    teamId
    courseId
    firstName
    lastName
    fullName
  }
}
```

---

## 📋 CAMPOS OBLIGATORIOS

⚠️ **IMPORTANTE:** Debes incluir TODOS estos campos:

```graphql
mutation RegisterUser {
  registerUser(input: {
    email: "tu-email@example.com"           # ✅ OBLIGATORIO
    password: "tupassword123"                # ✅ OBLIGATORIO (mínimo 8 caracteres)
    role: "STUDENT"                          # ✅ OBLIGATORIO (STUDENT, TEACHER, ADMIN)
    firstName: "TuNombre"                    # ✅ OBLIGATORIO
    lastName: "TuApellido"                   # ✅ OBLIGATORIO
    teamId: 1                                # ⭕ OPCIONAL
    courseId: 1                              # ⭕ OPCIONAL
  }) {
    id
    email
    role
    teamId
    courseId
    firstName
    lastName
    fullName
  }
}
```

---

## ⚠️ ERRORES COMUNES Y SOLUCIONES

### ❌ Error: "missing required fields '[firstName, lastName]'"

**TU CÓDIGO ORIGINAL (INCORRECTO):**
```graphql
mutation RegisterUser {
  registerUser(input: {
    email: "demo1@example.com"
    password: "password123"
    role: "estudiante"                        # ❌ Mal: minúsculas y en español
  }) {                                        # ❌ Falta firstName y lastName
    id
    email
  }
}
```

**CÓDIGO CORREGIDO:**
```graphql
mutation RegisterUser {
  registerUser(input: {
    email: "demo1@example.com"
    password: "password123"
    role: "STUDENT"                          # ✅ MAYÚSCULAS e inglés
    firstName: "Demo"                        # ✅ AGREGADO
    lastName: "Usuario"                      # ✅ AGREGADO
  }) {
    id
    email
    role
    firstName
    lastName
    fullName
  }
}
```

---

### ❌ Error: "Rol inválido: estudiante"

**Problema:** El rol debe estar en INGLÉS y MAYÚSCULAS

**INCORRECTO:**
- ❌ "estudiante"
- ❌ "profesor"
- ❌ "admin"
- ❌ "student"
- ❌ "teacher"

**CORRECTO:**
- ✅ "STUDENT"
- ✅ "TEACHER"
- ✅ "ADMIN"

---

## 🎯 PASOS EN POSTMAN CON LA PESTAÑA GRAPHQL

1. **Abre Postman**
2. **Nueva Request** → POST
3. **URL:** `http://localhost:8080/api/v1/graphql`
4. **Selecciona la pestaña "GraphQL"** (al lado de Body, Params, etc.)
5. **En el editor de GraphQL:** Pega una de las mutaciones de arriba
6. **Click en "Send"**

---

## 📊 RESPUESTA ESPERADA

```json
{
  "data": {
    "registerUser": {
      "id": "1",
      "email": "demo1@example.com",
      "role": "STUDENT",
      "teamId": null,
      "courseId": null,
      "firstName": "Demo",
      "lastName": "Usuario",
      "fullName": "Demo Usuario"
    }
  }
}
```

---

## 🔐 HACER LOGIN DESPUÉS (FORMATO DIRECTO)

Una vez creado el usuario, haz login con este formato:

```graphql
mutation Login {
  login(
    email: "demo1@example.com"
    password: "password123"
  ) {
    token
    refreshToken
    userInfo {
      id
      email
      role
      firstName
      lastName
      fullName
    }
  }
}
```

---

## 💡 TIPS IMPORTANTES

1. ✅ **NO uses comillas en los nombres de campos** dentro del input
2. ✅ **SÍ usa comillas en los valores** de strings
3. ✅ **Role siempre en MAYÚSCULAS:** STUDENT, TEACHER, ADMIN
4. ✅ **Incluye SIEMPRE:** email, password, role, firstName, lastName
5. ✅ **Password mínimo 8 caracteres**
6. ✅ **Email debe ser único** (no duplicados)

---

## 🆚 COMPARACIÓN DE FORMATOS

### Con Variables (más complejo pero recomendado):
```graphql
mutation RegisterUser($input: RegisterUserInput!) {
  registerUser(input: $input) {
    id
    email
  }
}

# En la pestaña "Variables" de Postman:
{
  "input": {
    "email": "demo@example.com",
    "password": "demo1234",
    "role": "STUDENT",
    "firstName": "Demo",
    "lastName": "User"
  }
}
```

### Sin Variables (más simple y directo):
```graphql
mutation RegisterUser {
  registerUser(input: {
    email: "demo@example.com"
    password: "demo1234"
    role: "STUDENT"
    firstName: "Demo"
    lastName: "User"
  }) {
    id
    email
  }
}
```

---

## 📦 PLANTILLA LISTA PARA COPIAR

```graphql
mutation RegisterUser {
  registerUser(input: {
    email: "CAMBIA_ESTO@example.com"
    password: "CAMBIA_ESTO_MIN8CHARS"
    role: "STUDENT"
    firstName: "CAMBIA_NOMBRE"
    lastName: "CAMBIA_APELLIDO"
  }) {
    id
    email
    role
    teamId
    courseId
    firstName
    lastName
    fullName
  }
}
```

**Reemplaza los valores CAMBIA_ESTO con tus datos y listo!**

---

## ✅ VERIFICACIÓN FINAL

Antes de enviar, verifica que tu mutación tenga:

- [ ] `email` - Email válido
- [ ] `password` - Mínimo 8 caracteres
- [ ] `role` - STUDENT, TEACHER o ADMIN (MAYÚSCULAS)
- [ ] `firstName` - Nombre del usuario
- [ ] `lastName` - Apellido del usuario
- [ ] Sin comas entre campos del input
- [ ] Comillas dobles en valores string

---

## 🎉 ¡LISTO PARA USAR!

Copia cualquiera de los ejemplos de arriba, pégalo en la pestaña GraphQL de Postman y presiona Send. ¡Debería funcionar perfectamente! 🚀

