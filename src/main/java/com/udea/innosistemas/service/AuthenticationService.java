package com.udea.innosistemas.service;

import com.udea.innosistemas.dto.AuthResponse;
import com.udea.innosistemas.dto.LoginRequest;
import com.udea.innosistemas.dto.UserInfo;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.exception.AuthenticationException;
import com.udea.innosistemas.repository.UserRepository;
import com.udea.innosistemas.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            logger.info("Attempting login for user ID");

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);

            UserInfo userInfo = new UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getRole()
            );

            logger.info("Login successful for user ID: {}", user.getId());

            return new AuthResponse(jwt, userInfo);

        } catch (BadCredentialsException e) {
            logger.warn("Login failed - Invalid credentials");
            throw new AuthenticationException("Credenciales inválidas");
        } catch (UsernameNotFoundException e) {
            logger.warn("Login failed - User not found");
            throw new AuthenticationException("Usuario no encontrado");
        } catch (Exception e) {
            logger.error("Login failed - Unexpected error: {}", e.getMessage());
            throw new AuthenticationException("Error durante la autenticación");
        }
    }

    public boolean validateCredentials(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            return true;
        } catch (BadCredentialsException e) {
            return false;
        }
    }
}