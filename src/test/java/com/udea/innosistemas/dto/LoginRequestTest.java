package com.udea.innosistemas.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validLoginRequest_ShouldPassValidation() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void loginRequest_WithInvalidEmail_ShouldFailValidation() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("invalid-email", "password123");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void loginRequest_WithBlankEmail_ShouldFailValidation() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("", "password123");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void loginRequest_WithBlankPassword_ShouldFailValidation() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void loginRequest_WithNullValues_ShouldFailValidation() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest(null, null);

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 2);
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();

        // Act
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Assert
        assertEquals("test@example.com", loginRequest.getEmail());
        assertEquals("password123", loginRequest.getPassword());
    }
}

