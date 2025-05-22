package com.example.core.sequence;

import com.example.core.events.InputEvent;
import com.example.core.tokens.TokenRegistry;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;

public class InputSequence {
    private final List<Predicate<InputEvent>> steps;
    private final List<String> tokens;

    public InputSequence(String raw) {
        this.tokens = Arrays.stream(raw.split(">"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        this.steps = tokens.stream().map(s -> {
            var token = TokenRegistry.TOKEN_MAP.get(s);
            if (token == null) {
                throw new IllegalArgumentException("");
            }
            return token;
        }).toList();
    }

    public String toString() {
        return String.join(" > ", tokens);
    }

    public boolean matches(Deque<InputEvent> buf) {
        if (buf.size() < steps.size()) return false;
        InputEvent[] arr = buf.toArray(new InputEvent[0]);
        int start = buf.size() - steps.size();
        for (int i = 0; i < steps.size(); i++) {
            Predicate<InputEvent> p = steps.get(i);
            if (!p.test(arr[start + i])) return false;
        }
        return true;
    }

}
