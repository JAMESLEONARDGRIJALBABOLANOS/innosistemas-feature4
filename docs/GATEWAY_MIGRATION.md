# Spring Cloud Gateway - Guía de Migración Futura

**Estado:** Planificado (no implementado)
**Versión:** 1.0.0
**Autor:** Fábrica-Escuela de Software UdeA

---

## Descripción

Este documento describe cómo migrar InnoSistemas a una arquitectura con **Spring Cloud Gateway** como punto único de entrada para el sistema.

**IMPORTANTE:** Esta configuración NO está activa actualmente. El sistema funciona con Spring Boot standalone.

---

## Arquitectura Propuesta

```
[Frontend] ──> [API Gateway] ──> [Backend Services]
                    │
                    ├── JWT Validation
                    ├── Rate Limiting
                    ├── Security Headers
                    ├── Routing
                    └── Load Balancing
```

### Beneficios del Gateway

1. **Punto único de entrada** - Simplifica la gestión de peticiones
2. **Validación centralizada de JWT** - Seguridad en una sola capa
3. **Rate limiting distribuido** - Protección contra abuso
4. **Load balancing** - Distribución de carga entre instancias
5. **Circuit breaker** - Resiliencia ante fallos
6. **Request/Response transformation** - Normalización de datos
7. **Logging y monitoring centralizados** - Mejor observabilidad

---

## Dependencias Necesarias

Para implementar el gateway, crear un proyecto separado con estas dependencias en `pom.xml`:

```xml
<dependencies>
    <!-- Spring Cloud Gateway -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>

    <!-- Circuit Breaker con Resilience4j -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
    </dependency>

    <!-- Redis reactivo para rate limiting -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
    </dependency>
</dependencies>
```

---

## 1. Configuración de Rutas

En `application.yml` del proyecto gateway:

```yaml
spring:
  cloud:
    gateway:
      routes:
        # Ruta para GraphQL
        - id: graphql-service
          uri: http://backend:8080
          predicates:
            - Path=/graphql
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200

        # Ruta para API REST
        - id: rest-api
          uri: http://backend:8080
          predicates:
            - Path=/api/v1/**
          filters:
            - name: JwtAuthenticationGatewayFilter
            - name: RequestRateLimiter

        # Ruta para WebSocket
        - id: websocket-service
          uri: ws://backend:8080
          predicates:
            - Path=/graphql-ws/**
```

---

## 2. Validación de JWT en Gateway

```java
@Bean
public GatewayFilter jwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
    return (exchange, chain) -> {
        String token = extractToken(exchange.getRequest());

        if (token != null && tokenProvider.validateToken(token)) {
            String username = tokenProvider.getUsernameFromJWT(token);

            // Agregar información del usuario en headers para backend
            exchange.getRequest().mutate()
                .header("X-User-Name", username)
                .build();
        }

        return chain.filter(exchange);
    };
}

private String extractToken(ServerHttpRequest request) {
    String bearerToken = request.getHeaders().getFirst("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
    }
    return null;
}
```

---

## 3. Rate Limiting por Usuario

```java
@Bean
public KeyResolver userKeyResolver() {
    return exchange -> {
        // Usar username del token si está autenticado
        String username = exchange.getRequest()
            .getHeaders()
            .getFirst("X-User-Name");

        // Fallback a IP si no hay usuario autenticado
        if (username == null) {
            InetSocketAddress remoteAddress = exchange.getRequest()
                .getRemoteAddress();
            username = remoteAddress != null
                ? remoteAddress.getAddress().getHostAddress()
                : "unknown";
        }

        return Mono.just(username);
    };
}
```

---

## 4. Circuit Breaker y Fallbacks

```java
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("backend-with-fallback", r -> r
            .path("/api/v1/**")
            .filters(f -> f.circuitBreaker(c -> c
                .setName("backendCircuitBreaker")
                .setFallbackUri("forward:/fallback")))
            .uri("http://backend:8080"))
        .build();
}

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public ResponseEntity<Map<String, String>> fallback() {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of(
                "error", "Service Unavailable",
                "message", "El servicio no está disponible temporalmente"
            ));
    }
}
```

---

## 5. Headers de Seguridad

```java
@Bean
public GlobalFilter securityHeadersFilter() {
    return (exchange, chain) -> {
        exchange.getResponse().getHeaders()
            .add("X-Content-Type-Options", "nosniff");
        exchange.getResponse().getHeaders()
            .add("X-Frame-Options", "DENY");
        exchange.getResponse().getHeaders()
            .add("X-XSS-Protection", "1; mode=block");
        exchange.getResponse().getHeaders()
            .add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        return chain.filter(exchange);
    };
}
```

---

## 6. Configuración de CORS

```java
@Bean
public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
        "http://localhost:3000",
        "https://innosistemas.udea.edu.co"
    ));
    config.setAllowedMethods(List.of(
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
    ));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source =
        new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsWebFilter(source);
}
```

---

## Pasos para Migrar

### Fase 1: Preparación

1. ✅ Crear proyecto separado para API Gateway
2. ✅ Agregar dependencias de Spring Cloud Gateway
3. ✅ Configurar properties básicas del gateway

### Fase 2: Seguridad

4. ✅ Mover validación de JWT al gateway
5. ✅ Implementar filtros de seguridad
6. ✅ Configurar headers de seguridad
7. ✅ Migrar configuración de CORS

### Fase 3: Rutas

8. ✅ Configurar rutas a servicios backend
9. ✅ Implementar rate limiting
10. ✅ Configurar WebSocket routing

### Fase 4: Resiliencia

11. ✅ Implementar circuit breaker
12. ✅ Configurar fallbacks
13. ✅ Implementar retry policies

### Fase 5: Producción

14. ✅ Configurar load balancing si es necesario
15. ✅ Implementar logging centralizado
16. ✅ Configurar monitoring y métricas
17. ✅ Realizar pruebas de carga

---

## Configuración de Redis

El rate limiting requiere Redis. Configurar en `application.yml`:

```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
```

---

## Cambios en el Backend

Al migrar al gateway, el backend debe:

1. **Eliminar filtros de seguridad** - El gateway maneja JWT
2. **Confiar en headers del gateway** - Leer `X-User-Name`
3. **Eliminar rate limiting** - El gateway lo maneja
4. **Simplificar CORS** - El gateway lo maneja

---

## Consideraciones de Producción

### Ventajas

- ✅ Escalabilidad independiente del gateway y backend
- ✅ Centralización de seguridad
- ✅ Mejor observabilidad
- ✅ Resiliencia con circuit breaker

### Desventajas

- ❌ Mayor complejidad operacional
- ❌ Latencia adicional (mínima)
- ❌ Requiere Redis para rate limiting
- ❌ Más componentes que mantener

---

## Cuándo Implementar

Se recomienda implementar el gateway cuando:

1. **Múltiples servicios backend** - Actualmente solo hay uno
2. **Alto tráfico** - Requiere load balancing
3. **Necesidad de circuit breaker** - Mejorar resiliencia
4. **Equipo con experiencia** - En Spring Cloud

**Recomendación actual:** NO implementar hasta que haya múltiples servicios o requisitos de escalabilidad.

---

## Referencias

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Redis Rate Limiting](https://redis.io/docs/manual/patterns/rate-limiter/)
