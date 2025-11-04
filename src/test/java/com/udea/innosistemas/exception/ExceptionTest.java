package com.udea.innosistemas.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationExceptionTest {

    @Test
    void constructor_WithMessage_ShouldCreateException() {
        // Act
        AuthenticationException exception = new AuthenticationException("Authentication failed");

        // Assert
        assertNotNull(exception);
        assertEquals("Authentication failed", exception.getMessage());
    }

    @Test
    void constructor_WithMessageAndCause_ShouldCreateException() {
        // Arrange
        Throwable cause = new RuntimeException("Root cause");

        // Act
        AuthenticationException exception = new AuthenticationException("Authentication failed", cause);

        // Assert
        assertNotNull(exception);
        assertEquals("Authentication failed", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void exceptionCanBeThrown() {
        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            throw new AuthenticationException("Test exception");
        });
    }

    @Test
    void exceptionExtendsRuntimeException() {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Test");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }
}

class BusinessExceptionTest {

    @Test
    void constructor_WithMessage_ShouldCreateException() {
        // Act
        BusinessException exception = new BusinessException("Business rule violated");

        // Assert
        assertNotNull(exception);
        assertEquals("Business rule violated", exception.getMessage());
    }

    @Test
    void exceptionCanBeThrown() {
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            throw new BusinessException("Test exception");
        });
    }

    @Test
    void exceptionExtendsRuntimeException() {
        // Arrange
        BusinessException exception = new BusinessException("Test");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }
}

