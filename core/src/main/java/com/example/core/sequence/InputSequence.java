package com.example.core.sequence;

import com.example.core.events.InputEvent;
import com.example.core.tokens.TokenRegistry;
import com.example.core.utils.Pair;

import java.util.*;
import java.util.function.Predicate;

public class InputSequence {
    private final List<Predicate<InputEvent>> steps;
    private final List<String> tokens;

    /*
     * @Precondition: Call parse to make sure that this is valid.
     *
     * tokens = toTokenList(raw)
     * var corrections = parse(tokens);
     * if(corrections.empty()) x = new InputSequence(tokens);
     * else for(var c : corrections) ...
     */
    public InputSequence(List<String> tokens) {
        this.tokens = tokens;
        this.steps = tokens.stream().map(TokenRegistry.TOKEN_MAP::get).toList();
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof InputSequence other)) return false;
        return tokens.equals(other.tokens);
    }

    public static List<String> toTokenList(String raw) {
        return Arrays.stream(raw.split(">")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    /*
     * Returns a list of {invalid token, potential correct token}.
     */
    public static List<Pair<String, Optional<String>>> parse(List<String> tokens) {
        List<Pair<String, Optional<String>>> errors = new ArrayList<>();
        for (String s : tokens) {
            if (!TokenRegistry.TOKEN_MAP.containsKey(s)) {
                errors.add(new Pair<>(s, Optional.empty()));
            }
        }
        return errors;
    }

    public static String getErrorString(List<Pair<String, Optional<String>>> errors) {
        StringBuilder msg = new StringBuilder();
        for (Pair<String, Optional<String>> err : errors) {
            msg.append("“").append(err.first()).append("” not recognized");
            if(err.second().isPresent()) {
                msg.append(" (did you mean “").append(err.second().get()).append("”)");
            }
            msg.append("\n");
        }
        return msg.toString();
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
