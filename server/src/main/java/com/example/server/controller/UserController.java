package com.example.server.controller;

import com.example.server.model.User;
import com.example.server.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User create(@RequestBody Map<String,String> payload) {
        return service.create(
                payload.get("name"),
                payload.get("email"),
                payload.get("password")
        );
    }
}
