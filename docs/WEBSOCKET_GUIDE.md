# Guía de WebSocket y GraphQL Subscriptions

Esta guía explica cómo usar las subscripciones GraphQL en tiempo real con WebSocket en el proyecto InnoSistemas.

## Tabla de Contenidos

1. [Configuración](#configuración)
2. [Endpoints WebSocket](#endpoints-websocket)
3. [Autenticación](#autenticación)
4. [Subscripciones Disponibles](#subscripciones-disponibles)
5. [Ejemplos de Uso](#ejemplos-de-uso)
6. [Clientes Soportados](#clientes-soportados)

---

## Configuración

### Endpoints WebSocket

- **URL Principal**: `ws://localhost:8080/api/v1/graphql-ws`
- **Con SockJS** (fallback): `ws://localhost:8080/api/v1/graphql-ws/websocket`

### Variables de Entorno

```yaml
# application.yml
spring:
  graphql:
    websocket:
      path: /graphql-ws
      allowed-origins: http://localhost:3000,http://localhost:8080
      connection-init-timeout: 60000
      keep-alive-interval: 15000
```

---

## Autenticación

Las conexiones WebSocket requieren autenticación JWT. Hay tres formas de proporcionar el token:

### Opción 1: Header Authorization (Recomendado)

```javascript
const client = new GraphQLWsClient({
  url: 'ws://localhost:8080/api/v1/graphql-ws',
  connectionParams: {
    Authorization: `Bearer ${jwtToken}`
  }
});
```

### Opción 2: Header token personalizado

```javascript
const client = new GraphQLWsClient({
  url: 'ws://localhost:8080/api/v1/graphql-ws',
  connectionParams: {
    token: jwtToken
  }
});
```

### Opción 3: Query Parameter (SockJS)

```javascript
const socket = new SockJS('http://localhost:8080/api/v1/graphql-ws?token=' + jwtToken);
```

---

## Subscripciones Disponibles

### 1. onNotificationReceived

Recibe notificaciones en tiempo real del usuario autenticado.

**GraphQL:**
```graphql
subscription {
  onNotificationReceived {
    id
    mensaje
    tipo
    leida
    fechaCreacion
    prioridad
    teamId
    enlace
  }
}
```

### 2. onTeamEvent

Recibe eventos de equipo en tiempo real.

**GraphQL:**
```graphql
subscription OnTeamEvent($teamId: ID) {
  onTeamEvent(teamId: $teamId) {
    teamId
    tipoEvento
    usuarioOrigenId
    detalles
    timestamp
    metadata
  }
}
```

### 3. onUnreadCountChanged

Recibe actualizaciones del contador de notificaciones no leídas.

**GraphQL:**
```graphql
subscription {
  onUnreadCountChanged {
    userId
    count
    timestamp
  }
}
```

---

## Ejemplos de Uso

### JavaScript/TypeScript con graphql-ws

```typescript
import { createClient } from 'graphql-ws';

// Crear cliente
const client = createClient({
  url: 'ws://localhost:8080/api/v1/graphql-ws',
  connectionParams: () => ({
    Authorization: `Bearer ${localStorage.getItem('token')}`
  }),
});

// Suscribirse a notificaciones
const subscription = client.subscribe(
  {
    query: `
      subscription {
        onNotificationReceived {
          id
          mensaje
          tipo
          leida
          fechaCreacion
        }
      }
    `,
  },
  {
    next: (data) => {
      console.log('Nueva notificación:', data.data.onNotificationReceived);
    },
    error: (error) => {
      console.error('Error en subscription:', error);
    },
    complete: () => {
      console.log('Subscription completada');
    },
  }
);

// Cancelar suscripción
// subscription();
```

### Apollo Client (React)

```typescript
import { ApolloClient, InMemoryCache, split, HttpLink } from '@apollo/client';
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { createClient } from 'graphql-ws';
import { getMainDefinition } from '@apollo/client/utilities';

// HTTP link para queries y mutations
const httpLink = new HttpLink({
  uri: 'http://localhost:8080/api/v1/graphql',
  headers: {
    Authorization: `Bearer ${localStorage.getItem('token')}`
  }
});

// WebSocket link para subscriptions
const wsLink = new GraphQLWsLink(
  createClient({
    url: 'ws://localhost:8080/api/v1/graphql-ws',
    connectionParams: {
      Authorization: `Bearer ${localStorage.getItem('token')}`
    },
  })
);

// Split entre HTTP y WebSocket según el tipo de operación
const splitLink = split(
  ({ query }) => {
    const definition = getMainDefinition(query);
    return (
      definition.kind === 'OperationDefinition' &&
      definition.operation === 'subscription'
    );
  },
  wsLink,
  httpLink,
);

// Crear Apollo Client
const client = new ApolloClient({
  link: splitLink,
  cache: new InMemoryCache(),
});

// Usar en componente React
import { useSubscription, gql } from '@apollo/client';

const NOTIFICATION_SUBSCRIPTION = gql`
  subscription OnNotificationReceived {
    onNotificationReceived {
      id
      mensaje
      tipo
      leida
      fechaCreacion
      prioridad
    }
  }
`;

function NotificationComponent() {
  const { data, loading, error } = useSubscription(NOTIFICATION_SUBSCRIPTION);

  if (loading) return <p>Conectando...</p>;
  if (error) return <p>Error: {error.message}</p>;

  if (data) {
    console.log('Nueva notificación:', data.onNotificationReceived);
    // Mostrar notificación en UI
  }

  return <div>Esperando notificaciones...</div>;
}
```

### Java/Kotlin con Spring Boot GraphQL Client

```kotlin
import org.springframework.graphql.client.GraphQlClient
import org.springframework.graphql.client.WebSocketGraphQlClient
import reactor.core.publisher.Flux

@Service
class NotificationSubscriptionService {

    private val webSocketClient: WebSocketGraphQlClient

    init {
        webSocketClient = WebSocketGraphQlClient.builder()
            .url("ws://localhost:8080/api/v1/graphql-ws")
            .headers { headers ->
                headers.set("Authorization", "Bearer $token")
            }
            .build()
    }

    fun subscribeToNotifications(): Flux<Notification> {
        val subscription = """
            subscription {
              onNotificationReceived {
                id
                mensaje
                tipo
                leida
                fechaCreacion
              }
            }
        """.trimIndent()

        return webSocketClient.document(subscription)
            .retrieveSubscription("onNotificationReceived")
            .toEntity(Notification::class.java)
    }
}
```

### Python con gql

```python
from gql import Client, gql
from gql.transport.websockets import WebsocketsTransport

# Configurar transporte WebSocket
transport = WebsocketsTransport(
    url="ws://localhost:8080/api/v1/graphql-ws",
    headers={
        "Authorization": f"Bearer {jwt_token}"
    }
)

# Crear cliente
client = Client(transport=transport, fetch_schema_from_transport=True)

# Definir subscription
subscription = gql("""
    subscription {
      onNotificationReceived {
        id
        mensaje
        tipo
        leida
        fechaCreacion
      }
    }
""")

# Suscribirse
async for result in client.subscribe(subscription):
    print(f"Nueva notificación: {result}")
```

---

## Testing con GraphiQL/Altair

### Altair GraphQL Client

1. Instalar Altair GraphQL Client
2. Configurar:
   - **GraphQL Endpoint**: `http://localhost:8080/api/v1/graphql`
   - **Subscription URL**: `ws://localhost:8080/api/v1/graphql-ws`
3. Agregar header:
   ```
   Authorization: Bearer YOUR_JWT_TOKEN
   ```
4. Ejecutar subscription:
   ```graphql
   subscription {
     onNotificationReceived {
       id
       mensaje
       tipo
     }
   }
   ```

### Postman

1. Crear nueva Request → WebSocket
2. URL: `ws://localhost:8080/api/v1/graphql-ws`
3. Conectar y enviar mensaje de inicio:
   ```json
   {
     "type": "connection_init",
     "payload": {
       "Authorization": "Bearer YOUR_JWT_TOKEN"
     }
   }
   ```
4. Enviar subscription:
   ```json
   {
     "id": "1",
     "type": "subscribe",
     "payload": {
       "query": "subscription { onNotificationReceived { id mensaje tipo } }"
     }
   }
   ```

---

## Seguridad

### Headers de Seguridad

El servidor envía los siguientes headers:

```
Strict-Transport-Security: max-age=31536000
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Content-Security-Policy: connect-src 'self' ws://localhost:* wss://localhost:*
```

### CORS

Configurado en `application.yml`:

```yaml
spring:
  graphql:
    websocket:
      allowed-origins: http://localhost:3000,http://localhost:8080
```

### Rate Limiting

El interceptor de autenticación valida el token JWT antes de establecer la conexión.

---

## Troubleshooting

### Error: "Connection refused"

- Verificar que el servidor esté corriendo
- Verificar la URL del endpoint
- Verificar configuración de firewall

### Error: "Unauthorized"

- Verificar que el token JWT sea válido
- Verificar que el token no haya expirado
- Verificar formato del header Authorization

### Error: "Connection timeout"

- Aumentar `connection-init-timeout` en `application.yml`
- Verificar conectividad de red

### No se reciben mensajes

- Verificar que estés suscrito al tópico correcto
- Verificar logs del servidor
- Verificar que los eventos se estén publicando correctamente

---

## Referencias

- [GraphQL over WebSocket Protocol](https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md)
- [Spring GraphQL WebSocket](https://docs.spring.io/spring-graphql/docs/current/reference/html/#web.websocket)
- [Apollo Client Subscriptions](https://www.apollographql.com/docs/react/data/subscriptions/)

---

**Autor**: Fábrica-Escuela de Software UdeA
**Versión**: 1.0.0
