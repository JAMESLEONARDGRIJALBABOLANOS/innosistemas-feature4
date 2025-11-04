package com.udea.innosistemas.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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

class ResourceNotFoundExceptionTest {

    @Test
    void constructor_WithMessage_ShouldCreateException() {
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        // Assert
        assertNotNull(exception);
        assertEquals("Resource not found", exception.getMessage());
    }

    @Test
    void constructor_WithResourceDetails_ShouldCreateException() {
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException("User", "id", 1L);

        // Assert
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("User"));
        assertTrue(exception.getMessage().contains("id"));
        assertTrue(exception.getMessage().contains("1"));
    }

    @Test
    void exceptionCanBeThrown() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("User", "email", "test@example.com");
        });
    }
}

class BadRequestExceptionTest {

    @Test
    void constructor_WithMessage_ShouldCreateException() {
        // Act
        BadRequestException exception = new BadRequestException("Bad request");

        // Assert
        assertNotNull(exception);
        assertEquals("Bad request", exception.getMessage());
    }

    @Test
    void exceptionCanBeThrown() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException("Invalid input");
        });
    }

    @Test
    void exceptionExtendsRuntimeException() {
        // Arrange
        BadRequestException exception = new BadRequestException("Test");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }
}

class UnauthorizedExceptionTest {

    @Test
    void constructor_WithMessage_ShouldCreateException() {
        // Act
        UnauthorizedException exception = new UnauthorizedException("Unauthorized access");

        // Assert
        assertNotNull(exception);
        assertEquals("Unauthorized access", exception.getMessage());
    }

    @Test
    void exceptionCanBeThrown() {
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            throw new UnauthorizedException("Access denied");
        });
    }
}

class ForbiddenExceptionTest {

    @Test
    void constructor_WithMessage_ShouldCreateException() {
        // Act
        ForbiddenException exception = new ForbiddenException("Forbidden");

        // Assert
        assertNotNull(exception);
        assertEquals("Forbidden", exception.getMessage());
    }

    @Test
    void exceptionCanBeThrown() {
        // Act & Assert
        assertThrows(ForbiddenException.class, () -> {
            throw new ForbiddenException("Insufficient permissions");
        });
    }
}

