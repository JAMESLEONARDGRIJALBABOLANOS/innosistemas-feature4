package com.udea.innosistemas.resolver;

import com.udea.innosistemas.dto.UserInfo;
import com.udea.innosistemas.dto.UserPermissions;
import com.udea.innosistemas.entity.UserRole;
import com.udea.innosistemas.service.UserQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@GraphQlTest(QueryResolver.class) // Apunta solo al QueryResolver
class QueryResolverTest {

    @Autowired
    private GraphQlTester graphQlTester; // Cliente para ejecutar queries de GQL

    @MockBean
    private UserQueryService userQueryService; // Mockea el servicio que usa el resolver

    @Test
    @WithMockUser // Simula un usuario autenticado para pasar @PreAuthorize("isAuthenticated()")
    void getCurrentUser_ShouldReturnUserInfo() {
        // Arrange (preparamos el mock del servicio)
        UserInfo mockUserInfo = new UserInfo();
        mockUserInfo.setId(1L);
        mockUserInfo.setEmail("testuser@example.com");
        mockUserInfo.setRole(UserRole.STUDENT);
        mockUserInfo.setFullName("Test User");

        when(userQueryService.getCurrentUser()).thenReturn(mockUserInfo);

        // Definimos el query de GQL
        String query = """
            query {
              getCurrentUser {
                id
                email
                role
                fullName
              }
            }
        """;

        // Act (ejecutamos el query)
        graphQlTester.document(query)
                .execute()
                .path("getCurrentUser") // Verificamos la respuesta JSON
                .matchesJson("""
                    {
                      "id": "1",
                      "email": "testuser@example.com",
                      "role": "STUDENT",
                      "fullName": "Test User"
                    }
                """);
    }

    @Test
    @WithMockUser(roles = {"STUDENT"}) // Simula un estudiante
    void getUserPermissions_AsStudent() {
        // Arrange
        UserPermissions mockPermissions = new UserPermissions(1L, UserRole.STUDENT, List.of("team:read"));
        when(userQueryService.getUserPermissions()).thenReturn(mockPermissions);

        String query = "query { getUserPermissions { userId role permissions } }";

        // Act & Assert
        graphQlTester.document(query)
                .execute()
                .path("getUserPermissions.role")
                .entity(UserRole.class).isEqualTo(UserRole.STUDENT)
                .path("getUserPermissions.permissions")
                .entityList(String.class).contains("team:read");
    }

    @Test
    void getCurrentUser_Unauthorized() {
        // Arrange (Sin @WithMockUser)
        String query = "query { getCurrentUser { id } }";

        // Act & Assert
        graphQlTester.document(query)
                .execute()
                .errors() // Verificamos que GQL devuelve un error
                .expect(error -> error.getMessage().contains("Acceso denegado"))
                .verify();
    }
}