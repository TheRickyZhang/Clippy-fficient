package com.example.core.events;

public record KeyReleaseEvent(
        int keyCode,
        int modifiers,
        long timestamp
) implements InputEvent {}
