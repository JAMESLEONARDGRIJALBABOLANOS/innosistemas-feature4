package com.udea.innosistemas.service;

import com.udea.innosistemas.dto.RegisterUserInput;
import com.udea.innosistemas.dto.UserInfo;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.entity.UserRole;
import com.udea.innosistemas.exception.BusinessException;
import com.udea.innosistemas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para manejar el registro de usuarios
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Service
public class UserRegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema
     *
     * @param input Datos del usuario a registrar
     * @return UserInfo con la información del usuario creado
     * @throws BusinessException si el email ya está registrado o el rol es inválido
     */
    @Transactional
    public UserInfo registerUser(RegisterUserInput input) {
        // Validar que el email no esté registrado
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new BusinessException("El email ya está registrado: " + input.getEmail());
        }

        // Validar y convertir el rol
        UserRole role;
        try {
            role = UserRole.valueOf(input.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Rol inválido: " + input.getRole() +
                ". Los valores permitidos son: STUDENT, TEACHER, ADMIN");
        }

        // Crear el usuario
        User user = new User();
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole(role);
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setTeamId(input.getTeamId());
        user.setCourseId(input.getCourseId());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        // Guardar el usuario
        User savedUser = userRepository.save(user);

        // Retornar la información del usuario
        return new UserInfo(savedUser);
    }
}

