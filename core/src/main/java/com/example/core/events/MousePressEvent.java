package com.example.core.events;

public record MousePressEvent(
        int button,
        int x,
        int y,
        long timestamp
) implements InputEvent {}
