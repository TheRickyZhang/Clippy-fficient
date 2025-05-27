package com.example.server.service;

import com.example.server.model.User;
import com.example.server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User create(String name, String email, String password) {
        User u = new User(name, email, encoder.encode(password));
        return repo.save(u);
    }

    public List<User> list() {
        return repo.findAll();
    }
}
