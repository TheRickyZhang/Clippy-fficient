package com.example.core;

import com.example.core.context.ApplicationContext;
import com.example.core.events.InputEvent;
import com.example.core.sequence.EventBuffer;
import com.example.core.sequence.InputSequence;
import com.example.core.utils.AppAction;
import com.example.core.utils.Suggestion;

import java.util.*;
import java.util.function.Consumer;

// The main program engine that maps sequence patterns to actions maintains a buffer, and parses incoming input
public class ShortcutEngine {
    private final ApplicationContext context;
    private final EventBuffer buffer = new EventBuffer(10);
    private final Map<InputSequence, Suggestion> suggestions = new HashMap<>();
    Map<AppAction,Consumer<Suggestion>> listeners;

    // TODO: Initialize with context?
    public ShortcutEngine(Map<AppAction, Consumer<Suggestion>> l) {
        this.context = new ApplicationContext(List.of());
        listeners = l;
    }

    public void putSuggestion(Suggestion s) {
        suggestions.put(s.getSequence(), s);
    }

    public void removeSuggestion(InputSequence i) {
        suggestions.remove(i);
    }

    public Collection<Suggestion> getAllSuggestions() {
        return suggestions.values();
    }

    public void updateContext(ApplicationContext c) {
        this.context.combine(c);
    }

    public void onEvent(InputEvent e) {
        buffer.push(e);
        Deque<InputEvent> curr = buffer.snapshot();
        // TODO: Edit context to also support removal and efficient mapping / hierarchy of information
        if (this.context.info().contains("Chrome")) {
            System.out.println("Recognized context in chrome");
        }
        for (InputSequence seq : suggestions.keySet()) {
            if (seq.matches(curr)) {
                Suggestion s = suggestions.get(seq);
                Consumer<Suggestion> c = listeners.get(s.getActionType());
                if (c == null) {
                    System.err.println("No handler for action “"+s.getActionType()+"”");
                } else {
                    c.accept(s);
                }

            }
        }

    }
}
