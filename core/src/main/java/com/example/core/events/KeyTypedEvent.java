package com.example.core.events;

public record KeyTypedEvent(
        char keyChar,
        int keyCode,
        int modifiers,
        long timestamp
) implements InputEvent {}
