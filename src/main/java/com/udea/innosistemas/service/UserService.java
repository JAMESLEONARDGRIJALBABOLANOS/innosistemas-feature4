package com.udea.innosistemas.service;

import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.entity.UserRole;
import com.udea.innosistemas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio auxiliar para operaciones básicas de usuarios,
 * como obtener el ID a partir del email autenticado.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Obtiene el ID del usuario usando su correo electrónico.
     */
    public Long emailToUserId(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    /**
     * Verifica si un usuario tiene un rol específico.
     */
    public boolean hasRole(String email, String role) {
        return userRepository.findByEmail(email)
                .map(u -> u.getRole() != null && u.getRole().name().equalsIgnoreCase(role))
                .orElse(false);
    }
}
