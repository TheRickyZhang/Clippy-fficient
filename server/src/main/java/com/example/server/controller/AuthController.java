// server/src/main/java/com/example/server/controller/AuthController.java
package com.example.server.controller;

import com.example.server.service.AuthService;
import com.example.shared.LoginRequest;
import com.example.shared.TokenResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        return auth.authenticate(req);
    }
}
