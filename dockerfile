# ============================================
# Dockerfile Multi-Stage para InnoSistemas Backend
# Optimizado para despliegue en Render
# ============================================

# Etapa 1: Build del JAR usando Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar solo el pom.xml primero para aprovechar el cache de Docker
COPY pom.xml .

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar y empaquetar la aplicación (sin tests para build más rápido)
RUN mvn clean package -DskipTests -B

# Etapa 2: Imagen final y liviana para producción
FROM eclipse-temurin:17-jre-jammy

# Metadata
LABEL maintainer="InnoSistemas Team"
LABEL description="Backend de InnoSistemas - Plataforma de Integración y Desarrollo de Software"
LABEL version="1.0.0"

WORKDIR /app

# Instalar curl para health check
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Crear un usuario no-root para mayor seguridad
RUN groupadd -r innosistemas && useradd -r -g innosistemas innosistemas

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar el propietario del JAR al usuario creado
RUN chown innosistemas:innosistemas app.jar

# Cambiar al usuario no-root
USER innosistemas

# Exponer el puerto (Render usa la variable PORT)
EXPOSE 8080

# Variables de entorno con valores por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE=prod

# Health check para Render
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/actuator/health || exit 1

# Ejecutar la aplicación con opciones de JVM optimizadas
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
