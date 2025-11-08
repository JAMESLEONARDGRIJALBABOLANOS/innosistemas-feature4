# 🚀 Despliegue Automático - InnoSistemas Backend

## Configuración de GitHub Secrets

Para que el workflow funcione, configura estos secrets en GitHub:

**Settings → Secrets and variables → Actions → New repository secret**

### Secrets Requeridos:

1. **DOCKER_USERNAME** - Tu usuario de Docker Hub
2. **DOCKER_PASSWORD** - Tu token de Docker Hub o contraseña
3. **SONAR_TOKEN** - Token de SonarCloud (ya configurado)
4. **RENDER_SERVICE_ID** - ID del servicio en Render
5. **RENDER_API_KEY** - API Key de Render

---

## 🔧 Configuración en Render

### 1. Crear Web Service en Render

1. Ve a [dashboard.render.com](https://dashboard.render.com)
2. **New +** → **Web Service**
3. Conecta tu repositorio GitHub
4. Configura:
   - **Name**: `innosistemas-backend`
   - **Region**: Oregon (o tu preferencia)
   - **Branch**: `main`
   - **Runtime**: **Docker**
   - **Instance Type**: Starter ($7/mes) o Free

### 2. Variables de Entorno en Render

En **Environment**, agrega:

```bash
DATABASE_URL=jdbc:postgresql://HOST:PORT/DB?sslmode=require
DATABASE_USERNAME=tu_usuario
DATABASE_PASSWORD=tu_password
JWT_SECRET=clave_muy_larga_minimo_256_caracteres
JWT_EXPIRATION=86400000
SPRING_PROFILES_ACTIVE=prod
PORT=8080
JAVA_OPTS=-Xmx512m -Xms256m
```

### 3. Obtener Service ID y API Key

**Service ID:**
- En tu servicio de Render, copia el ID de la URL:
  `https://dashboard.render.com/web/srv-XXXXXXXXXX` → `srv-XXXXXXXXXX`

**API Key:**
- Ve a **Account Settings** → **API Keys**
- Click **Create API Key**
- Copia la key

### 4. Agregar Secrets a GitHub

```bash
RENDER_SERVICE_ID=srv-XXXXXXXXXX
RENDER_API_KEY=rnd_XXXXXXXXXXXXXXXXXXXXXXXX
```

---

## 🔄 Workflow Pipeline

El workflow automático hace:

1. **Tests** - Ejecuta pruebas unitarias
2. **SonarCloud** - Análisis de calidad de código
3. **Build** - Compila el JAR
4. **Docker** - Construye y sube imagen a Docker Hub
5. **Deploy** - Despliega automáticamente en Render

### Trigger del Workflow:

- ✅ Push a `main` - Deploy completo
- ✅ Pull Request - Solo tests y build
- ✅ Manual - Botón "Run workflow"

---

## 📊 Base de Datos

### Opción 1: Render PostgreSQL
```bash
DATABASE_URL=jdbc:postgresql://dpg-xxx.oregon-postgres.render.com:5432/innosistemas?sslmode=require
```

### Opción 2: Supabase (Recomendado Free Tier)
```bash
DATABASE_URL=jdbc:postgresql://db.xxx.supabase.co:5432/postgres?sslmode=require
DATABASE_USERNAME=postgres
```

---

## ✅ Verificar Despliegue

```bash
curl https://tu-app.onrender.com/api/v1/actuator/health
```

Respuesta esperada:
```json
{"status":"UP"}
```

---

## 🎯 URLs Importantes

- **API GraphQL**: `https://tu-app.onrender.com/api/v1/graphql`
- **GraphiQL**: `https://tu-app.onrender.com/api/v1/graphiql`
- **Health**: `https://tu-app.onrender.com/api/v1/actuator/health`

---

**¡Listo! Cada push a `main` desplegará automáticamente a Render 🚀**

