package com.example.core.events;

import java.util.ArrayDeque;
import java.util.Deque;

public class EventBuffer {
    private final Deque<InputEvent> buf = new ArrayDeque<>();
    private final int maxSize;
    public EventBuffer(int sz) {
        this.maxSize = sz;
    }

    public void push(InputEvent e) {
        buf.addFirst(e);
        if(buf.size() > maxSize) buf.removeLast();
    }

    public Deque<InputEvent> snapshot() {
        return new ArrayDeque<>(buf);
    }
}