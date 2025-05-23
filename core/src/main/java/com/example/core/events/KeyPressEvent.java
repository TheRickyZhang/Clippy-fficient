package com.example.core.events;

public record KeyPressEvent(
        int keyCode,
        int modifiers,
        long timestamp
) implements InputEvent {}
