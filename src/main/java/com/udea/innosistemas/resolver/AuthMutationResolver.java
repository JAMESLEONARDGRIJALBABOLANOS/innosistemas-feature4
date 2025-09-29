package com.udea.innosistemas.resolver;

import com.udea.innosistemas.dto.AuthResponse;
import com.udea.innosistemas.dto.LoginRequest;
import com.udea.innosistemas.service.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class AuthMutationResolver {

    @Autowired
    private AuthenticationService authenticationService;

    @MutationMapping
    @PreAuthorize("permitAll()")
    public AuthResponse login(
            @Argument @Valid @Email @NotBlank String email,
            @Argument @Valid @NotBlank String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        return authenticationService.login(loginRequest);
    }
}