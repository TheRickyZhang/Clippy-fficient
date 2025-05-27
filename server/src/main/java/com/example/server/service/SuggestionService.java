package com.example.server.service;

import com.example.server.model.Suggestion;
import com.example.server.model.User;
import com.example.server.repository.SuggestionRepository;
import com.example.server.repository.UserRepository;
import com.example.shared.AppAction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SuggestionService {
    private final UserRepository users;
    private final SuggestionRepository suggestions;

    public SuggestionService(UserRepository users, SuggestionRepository suggestions) {
        this.users = users;
        this.suggestions = suggestions;
    }

    @Transactional
    public Suggestion create(Long userId, String sequence, String hint, AppAction action) {
        User u = users.findById(userId).orElseThrow(() -> new NoSuchElementException("No user " + userId));
        Suggestion s = new Suggestion(sequence, hint, action);
        u.addSuggestion(s);
        users.save(u);
        return s;
    }

    @Transactional(readOnly=true)
    public List<Suggestion> listForUser(Long userId) {
        return suggestions.findByUserId(userId);
    }

}
