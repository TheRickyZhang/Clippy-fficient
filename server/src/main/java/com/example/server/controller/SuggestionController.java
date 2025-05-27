package com.example.server.controller;

import com.example.server.model.Suggestion;
import com.example.server.service.SuggestionService;
import com.example.shared.AppAction;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SuggestionController {
    private final SuggestionService service;

    public SuggestionController(SuggestionService service) {
        this.service = service;
    }

    @PostMapping
    public Suggestion create(
            @PathVariable Long userId,
            @RequestParam String sequence,
            @RequestParam String hint,
            @RequestParam AppAction action
    ) {
        return service.create(userId, sequence, hint, action);
    };

    @GetMapping
    public List<Suggestion> list(@PathVariable Long userId) {
        return service.listForUser(userId);
    }
}
