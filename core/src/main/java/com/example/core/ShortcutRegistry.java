package com.example.core;

import com.example.core.events.InputEvent;
import com.example.core.sequence.EventBuffer;
import com.example.core.sequence.SequencePattern;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Note this is NOT static since we can have different instances per different windows
public class ShortcutRegistry {
    private final EventBuffer buffer = new EventBuffer(10);
    private final Map<SequencePattern,Runnable> registry = new ConcurrentHashMap<>();

    public void addPattern(SequencePattern pattern, Runnable action) {
        registry.put(pattern, action);
    }

    public void removePattern(SequencePattern pattern) {
        registry.remove(pattern);
    }

    public void onEvent(InputEvent e) {
        buffer.push(e);
        Deque<InputEvent> curr = buffer.snapshot();
        registry.forEach((p, action) -> {
            if (p.matches(curr)) {
                action.run();
            }
        });
    }
}
