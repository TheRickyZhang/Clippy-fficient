// src/main/java/com/example/server/AuthService.java
package com.example.server.service;

import com.example.server.model.User;
import com.example.server.repository.UserRepository;
import com.example.server.security.JwtTokenProvider;
import com.example.shared.LoginRequest;
import com.example.shared.TokenResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;

    public AuthService(UserRepository users,
                       PasswordEncoder encoder,
                       JwtTokenProvider jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public TokenResponse authenticate(LoginRequest req) {
        User u = users.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwt.createToken(u.getId());
        return new TokenResponse(token, jwt.getExpiresInMs());
    }
}
