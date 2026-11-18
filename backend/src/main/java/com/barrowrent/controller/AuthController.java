
package com.barrowrent.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.barrowrent.request.LoginRequest;
import com.barrowrent.request.RegisterUserRequest;
import com.barrowrent.response.LoginResponse;
import com.barrowrent.response.UserResponse;
import com.barrowrent.service.impl.AuthServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthServiceImpl authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterUserRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        return ResponseEntity.noContent().build();
    }
}
