# INNOSISTEMAS Feature 4 - Kubernetes en Docker Desktop
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "DESPLIEGUE KUBERNETES - Docker Desktop" -ForegroundColor Cyan
Write-Host "Feature 4: Comunicaciones y Notificaciones" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar Kubernetes
Write-Host "1. Verificando Kubernetes..." -ForegroundColor Yellow
kubectl cluster-info
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Kubernetes no está habilitado en Docker Desktop" -ForegroundColor Red
    Write-Host "Habilítalo en: Docker Desktop → Settings → Kubernetes" -ForegroundColor Yellow
    exit 1
}

# 2. Construir imagen Docker (si es necesario)
Write-Host "`n2. Construyendo imagen Docker..." -ForegroundColor Yellow
docker build -t innosistemas-feature4-backend:latest .

# 3. Crear namespace
Write-Host "`n3. Creando namespace..." -ForegroundColor Yellow
kubectl apply -f k8s/01-namespace.yaml

# 4. Crear secrets
Write-Host "`n4. Creando secrets..." -ForegroundColor Yellow
kubectl create secret generic innosistemas-secrets `
    --namespace=innosistemas `
    --from-literal=db-password=password `
    --from-literal=db-username=postgres `
    --from-literal=jwt-secret=dev-secret-key-feature4 `
    --dry-run=client -o yaml | kubectl apply -f -

# 5. Aplicar configmap
Write-Host "`n5. Aplicando configmap..." -ForegroundColor Yellow
kubectl apply -f k8s/02-configmap.yaml

# 6. Desplegar PostgreSQL
Write-Host "`n6. Desplegando PostgreSQL..." -ForegroundColor Yellow
kubectl apply -f k8s/04-postgres.yaml

# 7. Esperar PostgreSQL
Write-Host "`n7. Esperando PostgreSQL (30 segundos)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# 8. Desplegar Backend
Write-Host "`n8. Desplegando Backend Spring Boot..." -ForegroundColor Yellow
kubectl apply -f k8s/05-backend.yaml
kubectl apply -f k8s/06-service.yaml

# 9. Esperar y verificar
Write-Host "`n9. Esperando inicio de pods (40 segundos)..." -ForegroundColor Yellow
Start-Sleep -Seconds 40

# 10. Mostrar estado
Write-Host "`n=== ESTADO DEL CLUSTER ===" -ForegroundColor Green
kubectl get all -n innosistemas

Write-Host "`n=== PODS ===" -ForegroundColor Green
kubectl get pods -n innosistemas -o wide

Write-Host "`n=== SERVICIOS ===" -ForegroundColor Green
kubectl get svc -n innosistemas

Write-Host "`n=== CONFIGURACIONES ===" -ForegroundColor Green
kubectl get configmap,secret -n innosistemas

# 11. Port-forward para acceder
Write-Host "`n=== ACCESO A LA APLICACIÓN ===" -ForegroundColor Cyan

$backendPod = kubectl get pods -n innosistemas -l app=backend -o jsonpath='{.items[0].metadata.name}' 2>$null
if ($backendPod) {
    Write-Host "`n✅ Backend desplegado en pod: $backendPod" -ForegroundColor Green
    
    Write-Host "`nPara acceder a la aplicación:" -ForegroundColor Yellow
    Write-Host "1. Ejecuta en una NUEVA terminal:" -ForegroundColor White
    Write-Host "   kubectl port-forward -n innosistemas svc/backend-service 8080:80" -ForegroundColor Gray
    
    Write-Host "`n2. Luego abre en navegador:" -ForegroundColor White
    Write-Host "   http://localhost:8080/api/v1/actuator/health" -ForegroundColor Gray
    Write-Host "   http://localhost:8080/api/v1/graphiql" -ForegroundColor Gray
    
    Write-Host "`n3. Para PostgreSQL:" -ForegroundColor White
    Write-Host "   kubectl port-forward -n innosistemas svc/postgres-service 5432:5432" -ForegroundColor Gray
} else {
    Write-Host "`n⚠ Backend aún no está listo. Verifica con:" -ForegroundColor Yellow
    Write-Host "   kubectl get pods -n innosistemas" -ForegroundColor Gray
    Write-Host "   kubectl logs -n innosistemas -l app=backend" -ForegroundColor Gray
}

Write-Host "`n=== COMANDOS ÚTILES ===" -ForegroundColor Cyan
Write-Host "Ver logs: kubectl logs -n innosistemas -f deployment/backend" -ForegroundColor Gray
Write-Host "Ver pods: kubectl get pods -n innosistemas -w" -ForegroundColor Gray
Write-Host "Eliminar todo: kubectl delete namespace innosistemas" -ForegroundColor Gray
Write-Host "Dashboard: kubectl describe pod -n innosistemas" -ForegroundColor Gray

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "✅ Despliegue Kubernetes iniciado" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
