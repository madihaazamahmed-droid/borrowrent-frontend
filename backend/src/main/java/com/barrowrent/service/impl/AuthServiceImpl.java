
package com.barrowrent.service.impl;

import java.util.Locale;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barrowrent.entity.User;
import com.barrowrent.exception.EmailAlreadyRegisteredException;
import com.barrowrent.repository.UserRepository;
import com.barrowrent.request.LoginRequest;
import com.barrowrent.request.RegisterUserRequest;
import com.barrowrent.response.LoginResponse;
import com.barrowrent.response.UserResponse;
import com.barrowrent.security.AppUserPrincipal;
import com.barrowrent.security.JwtService;

@Service
public class AuthServiceImpl {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // REGISTER USER
    @Transactional
    public UserResponse register(RegisterUserRequest request) {

        // Normalize email
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Check duplicate email
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyRegisteredException(
                    "An account with this email already exists."
            );
        }

        // Encode password
        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        // Create user
        User user = new User(
                request.getFullName().trim(),
                email,
                encodedPassword,
                normalizeOptional(request.getPhone()),
                normalizeOptional(request.getAddress())
        );

        // Save user
        User savedUser = userRepository.save(user);

        // Return response without password hash
        return UserResponse.from(savedUser);
    }

    // LOGIN USER
    public LoginResponse login(LoginRequest request) {

        var authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email().trim(),
                                request.password()
                        )
                );

        AppUserPrincipal principal =
                (AppUserPrincipal) authentication.getPrincipal();

        String token = jwtService.generateToken(principal);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs() / 1000
        );
    }

    // Normalize optional fields
    private String normalizeOptional(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
