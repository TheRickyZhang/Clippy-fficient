package com.example.core.events;

// Ignore keyCode here since it will always be null
public record KeyTypedEvent(
        char keyChar,
        int modifiers,
        long timestamp
) implements InputEvent { }
