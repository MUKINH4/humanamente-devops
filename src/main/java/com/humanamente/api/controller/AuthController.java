package com.humanamente.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.AuthenticationException;

import com.humanamente.api.dto.LoginRequest;
import com.humanamente.api.dto.RegisterRequest;
import com.humanamente.api.dto.RegisterResponse;
import com.humanamente.api.dto.TokenDTO;
import com.humanamente.api.model.User;
import com.humanamente.api.model.enums.UserRoles;
import com.humanamente.api.service.AuthService;
import com.humanamente.api.service.MessageService;
import com.humanamente.api.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    private final MessageService messageService;

    public AuthController(AuthService authService, MessageService messageService) {
        this.authService = authService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest req) {
        try {
            User user = User.builder()
                .username(req.username())
                .email(req.email())
                .password(req.password())
                .role(req.role() != null ? req.role() : UserRoles.USER)
                .build();

            authService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.getMessage("success.user.created"));
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação no registro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao registrar usuário: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(messageService.getMessage("error.internal"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest credentials) {
        try {
            if (credentials == null || credentials.email() == null || credentials.password() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(messageService.getMessage("error.invalid.credentials"));
            }
            
            TokenDTO token = authService.login(credentials);
            return ResponseEntity.ok(token.toString());
            
        } catch (AuthenticationException e) {
            log.warn("Autenticação falhou: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Email ou senha inválidos");
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação no login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao realizar login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(messageService.getMessage("error.internal"));
        }
    }
}
