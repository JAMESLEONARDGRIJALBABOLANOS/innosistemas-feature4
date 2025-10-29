package com.udea.innosistemas.service;

import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.entity.UserRole;
import com.udea.innosistemas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Servicio para manejar la lógica de permisos y roles.
 * Determina si el usuario autenticado puede realizar acciones específicas
 * como enviar notificaciones, ver datos de otros usuarios o ejecutar tareas de sistema.
 */
@Service
public class PermissionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * Retorna el usuario autenticado completo.
     */
    private User getAuthenticatedUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado o no autenticado"));
    }

    /**
     * Verifica si el usuario autenticado tiene rol ADMIN.
     */
    public boolean isAdmin(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        return user.getRole() == UserRole.ADMIN;
    }

    /**
     * Verifica si el usuario autenticado tiene rol DEVELOPER.
     */
    public boolean isDeveloper(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        return user.getRole() == UserRole.DEVELOPER;
    }

    /**
     * Verifica si el usuario autenticado tiene rol EMISOR.
     */
    public boolean isEmisor(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        return user.getRole() == UserRole.EMISOR;
    }

    /**
     * Verifica si el usuario autenticado es propietario del recurso (por ID).
     */
    public boolean isOwner(Authentication auth, Long idUsuario) {
        User user = getAuthenticatedUser(auth);
        return user.getId().equals(idUsuario);
    }

    /**
     * Verifica si el usuario autenticado tiene permisos para enviar correos masivos.
     * Se permite a ADMIN o EMISOR.
     */
    public boolean canSendMassEmail(Authentication auth, Long idEquipo) {
        User user = getAuthenticatedUser(auth);
        return user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.EMISOR;
    }

    /**
     * Convierte un correo electrónico a su ID de usuario.
     * Función auxiliar usada por servicios de notificaciones.
     */
    public Long emailToUserId(String email) {
        return userService.emailToUserId(email);
    }
}
