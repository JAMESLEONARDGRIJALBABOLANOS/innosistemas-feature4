# Configuración de Java 17 para InnoSistemas

Este proyecto requiere **Java 17.0.14** para su correcta ejecución.

## Estado Actual

- **Java configurado en pom.xml:** `17`
- **Java del sistema detectado:** `25`
- **Java requerido:** `17.0.14`

## ⚠️ Problema

Maven está usando Java 25 del sistema en lugar de Java 17. Esto puede causar problemas de compatibilidad.

## 🔧 Soluciones

### Opción 1: Script Automático (Recomendado para Windows)

Ejecuta el script `set-java-17.bat` en cada sesión de terminal antes de trabajar:

```bash
set-java-17.bat
mvn spring-boot:run
```

### Opción 2: Configurar JAVA_HOME Permanentemente (Windows)

#### 2.1. Verificar si tienes Java 17 instalado

```cmd
dir "C:\Program Files\Java"
```

#### 2.2. Descargar Java 17 si no lo tienes

- **Adoptium Temurin 17 (Recomendado):** https://adoptium.net/temurin/releases/?version=17
- **Oracle JDK 17:** https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

#### 2.3. Configurar JAVA_HOME

1. Abre **Panel de Control** → **Sistema** → **Configuración avanzada del sistema**
2. Haz clic en **Variables de entorno**
3. En **Variables del sistema**, busca o crea `JAVA_HOME`
4. Establece el valor a la ruta de Java 17, por ejemplo:
   ```
   C:\Program Files\Java\jdk-17.0.14
   ```
5. Edita la variable `Path` y asegúrate de que `%JAVA_HOME%\bin` esté al principio
6. Haz clic en **Aceptar** en todos los diálogos
7. **Reinicia tu terminal**

#### 2.4. Verificar la configuración

```cmd
java -version
```

Deberías ver:
```
java version "17.0.14" ...
```

### Opción 3: Usar Maven Toolchains (Para múltiples versiones de Java)

Crea el archivo `~/.m2/toolchains.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>17</version>
            <vendor>openjdk</vendor>
        </provides>
        <configuration>
            <jdkHome>C:\Program Files\Java\jdk-17.0.14</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
```

### Opción 4: SDKMAN (Para Linux/Mac/WSL)

```bash
# Instalar SDKMAN
curl -s "https://get.sdkman.io" | bash

# Instalar Java 17
sdk install java 17.0.14-tem

# Usar Java 17 automáticamente en este proyecto
sdk env install
```

### Opción 5: IntelliJ IDEA

1. **File** → **Project Structure** → **Project**
2. Establece **SDK** a Java 17
3. Establece **Language level** a "17 - Sealed types, always-strict floating-point semantics"
4. **File** → **Settings** → **Build, Execution, Deployment** → **Build Tools** → **Maven** → **Runner**
5. Establece **JRE** a Java 17

### Opción 6: VS Code

En `.vscode/settings.json`:

```json
{
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-17",
            "path": "C:\\Program Files\\Java\\jdk-17.0.14",
            "default": true
        }
    ],
    "java.jdt.ls.java.home": "C:\\Program Files\\Java\\jdk-17.0.14"
}
```

## 📋 Verificación

Después de configurar Java 17, ejecuta:

```bash
# Limpiar compilación anterior
mvn clean

# Compilar con Java 17
mvn compile

# Ver información de compilación
mvn -version
```

Deberías ver:
```
Java version: 17.0.14
```

## 🚀 Siguiente Paso

Una vez configurado Java 17, el siguiente problema a resolver es la **base de datos PostgreSQL**.

Ver los logs de inicio para más detalles sobre la conexión a la base de datos.

## 📚 Archivos de Configuración Creados

- `.java-version` - Para jenv/asdf version managers
- `.sdkmanrc` - Para SDKMAN
- `pom.xml` - Actualizado con maven-compiler-plugin para Java 17
- `set-java-17.bat` - Script de configuración automática para Windows