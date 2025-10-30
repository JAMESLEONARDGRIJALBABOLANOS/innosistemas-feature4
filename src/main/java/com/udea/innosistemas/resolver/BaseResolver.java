package com.udea.innosistemas.resolver;

import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.entity.UserRole;
import com.udea.innosistemas.exception.AuthenticationException;
import com.udea.innosistemas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Clase base abstracta para todos los resolvers GraphQL
 * Proporciona funcionalidad común para:
 * - Obtener el usuario autenticado
 * - Validar permisos
 * - Manejo de errores comunes
 *
 * Patrón: Template Method
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
public abstract class BaseResolver {

    @Autowired
    protected UserRepository userRepository;

    /**
     * Obtiene el usuario actualmente autenticado desde el contexto de seguridad
     *
     * @return User autenticado
     * @throws AuthenticationException si no hay usuario autenticado
     * @throws UsernameNotFoundException si el usuario no existe en la BD
     */
    protected User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (username == null || username.equals("anonymousUser")) {
            throw new AuthenticationException("No hay usuario autenticado");
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    /**
     * Obtiene solo el ID del usuario autenticado
     *
     * @return ID del usuario
     */
    protected Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Verifica si el usuario actual tiene un rol específico
     *
     * @param role Rol a verificar
     * @return true si el usuario tiene el rol
     */
    protected boolean hasRole(UserRole role) {
        return getCurrentUser().getRole() == role;
    }

    /**
     * Verifica si el usuario actual es profesor o admin
     *
     * @return true si es profesor o admin
     */
    protected boolean isProfessorOrAdmin() {
        UserRole role = getCurrentUser().getRole();
        return role == UserRole.PROFESSOR || role == UserRole.ADMIN || role == UserRole.TA;
    }

    /**
     * Verifica si el usuario tiene permiso para acceder a un equipo
     * Los estudiantes solo pueden acceder a su propio equipo
     * Profesores y admins pueden acceder a cualquier equipo
     *
     * @param teamId ID del equipo
     * @throws AuthenticationException si no tiene permiso
     */
    protected void validateTeamAccess(Long teamId) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == UserRole.STUDENT) {
            if (!teamId.equals(currentUser.getTeamId())) {
                throw new AuthenticationException("No tienes permiso para acceder a este equipo");
            }
        }
    }

    /**
     * Verifica si el usuario tiene permiso para modificar un equipo
     * Solo profesores y admins pueden modificar equipos
     *
     * @throws AuthenticationException si no tiene permiso
     */
    protected void validateTeamModificationAccess() {
        if (!isProfessorOrAdmin()) {
            throw new AuthenticationException("Solo profesores y administradores pueden modificar equipos");
        }
    }

    /**
     * Verifica si una notificación pertenece al usuario actual
     *
     * @param notificationUserId ID del usuario dueño de la notificación
     * @throws AuthenticationException si no tiene permiso
     */
    protected void validateNotificationOwnership(Long notificationUserId) {
        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(notificationUserId)) {
            throw new AuthenticationException("No tienes permiso para acceder a esta notificación");
        }
    }

    /**
     * Extrae un valor Long de un Map, con manejo seguro de nulos
     *
     * @param input Map de entrada
     * @param key Clave a buscar
     * @return Long o null si no existe
     */
    protected Long extractLong(java.util.Map<String, Object> input, String key) {
        if (input.containsKey(key) && input.get(key) != null) {
            Object value = input.get(key);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return Long.parseLong(value.toString());
        }
        return null;
    }

    /**
     * Extrae un valor String de un Map, con manejo seguro de nulos
     *
     * @param input Map de entrada
     * @param key Clave a buscar
     * @return String o null si no existe
     */
    protected String extractString(java.util.Map<String, Object> input, String key) {
        if (input.containsKey(key) && input.get(key) != null) {
            return input.get(key).toString();
        }
        return null;
    }

    /**
     * Extrae un valor Integer de un Map, con manejo seguro de nulos
     *
     * @param input Map de entrada
     * @param key Clave a buscar
     * @return Integer o null si no existe
     */
    protected Integer extractInteger(java.util.Map<String, Object> input, String key) {
        if (input.containsKey(key) && input.get(key) != null) {
            Object value = input.get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        }
        return null;
    }

    /**
     * Extrae un valor Boolean de un Map, con manejo seguro de nulos
     *
     * @param input Map de entrada
     * @param key Clave a buscar
     * @return Boolean o null si no existe
     */
    protected Boolean extractBoolean(java.util.Map<String, Object> input, String key) {
        if (input.containsKey(key) && input.get(key) != null) {
            Object value = input.get(key);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            return Boolean.parseBoolean(value.toString());
        }
        return null;
    }
}
