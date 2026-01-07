package com.backend.sri.controller;

import com.backend.sri.dto.AuthDto;
import com.backend.sri.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for Login, Register and Logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    public ResponseEntity<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Registers a new user and returns JWT token")
    public ResponseEntity<AuthDto.AuthResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Logout user", description = "Client should discard the token. (Server-side blacklist not implemented in MVP)")
    public ResponseEntity<String> logout() {
        // In a stateless JWT system, logout is primarily client-side (deleting the
        // token).
        // For enhanced security, implement a Token Blacklist logic here.
        return ResponseEntity.ok("Logged out successfully");
    }
}
