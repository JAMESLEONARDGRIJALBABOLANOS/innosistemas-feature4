cat > README.md << 'EOF'
InnoSistemas - Feature 4: Comunicaciones y Notificaciones

Plataforma de Integración y Desarrollo de Software para Estudiantes de Ingeniería de Sistemas - Universidad de Antioquia

Descripción

Feature 4 implementa el sistema de comunicaciones y notificaciones para InnoSistemas, incluyendo:

* Notificaciones en tiempo real
* Alertas sobre tareas pendientes  
* Envío de correos masivos
* Dashboard de comunicaciones
* Autenticación y autorización
* Accesibilidad (UI/UX para discapacidad visual)

Stack Tecnológico

Frontend
-Framework: Next.js 14+
-UI Library React 18+
-State Management: React Context + Hooks
-GraphQL Client: Apollo Client
-Styling: Tailwind CSS + shadcn/ui
-Notifications: React Hot Toast

Backend  
-Framework: Spring Boot 3+
-API: GraphQL + REST
-Database: PostgreSQL (Supabase)
-Security: Spring Security + JWT
-Real-time: WebSockets
-Email: Spring Mail

DevOps
-Containerization: Docker
-Orchestration: Kubernetes (Minikube)
-CI/CD: GitHub Actions
-Monitoring: Prometheus + Grafana
-Logging: ELK Stack

Arquitectura

Frontend (Next.js) → API Gateway → Backend (Spring Boot) → PostgreSQL
↓
IAM + Security

Instalación

Ver documentación en `/docs/`
