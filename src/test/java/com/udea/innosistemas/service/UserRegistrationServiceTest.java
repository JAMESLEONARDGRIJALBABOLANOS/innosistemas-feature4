package com.udea.innosistemas.service;

import com.udea.innosistemas.dto.RegisterUserInput;
import com.udea.innosistemas.dto.UserInfo;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.entity.UserRole;
import com.udea.innosistemas.exception.BusinessException;
import com.udea.innosistemas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    private RegisterUserInput input;

    @BeforeEach
    void setUp() {
        input = new RegisterUserInput();
        input.setEmail("newuser@example.com");
        input.setPassword("password123");
        input.setRole("STUDENT");
        input.setFirstName("New");
        input.setLastName("User");
    }

    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Simular que el save devuelve el usuario con un ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        UserInfo userInfo = userRegistrationService.registerUser(input);

        // Assert
        assertNotNull(userInfo);
        assertEquals(1L, userInfo.getId());
        assertEquals("newuser@example.com", userInfo.getEmail());
        assertEquals(UserRole.STUDENT, userInfo.getRole());
        assertEquals("New User", userInfo.getFullName());

        // Verificar que se llamó a save
        verify(userRepository, times(1)).save(any(User.class));
        // Verificar que la contraseña fue encriptada
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void registerUser_EmailAlreadyExists_ShouldThrowBusinessException() {
        // Arrange
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.of(new User()));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userRegistrationService.registerUser(input);
        });

        assertEquals("El email ya está registrado: newuser@example.com", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_InvalidRole_ShouldThrowBusinessException() {
        // Arrange
        input.setRole("INVALID_ROLE");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userRegistrationService.registerUser(input);
        });

        assertTrue(exception.getMessage().contains("Rol inválido: INVALID_ROLE"));
        verify(userRepository, never()).save(any(User.class));
    }
}