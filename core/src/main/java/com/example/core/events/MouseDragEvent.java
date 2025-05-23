package com.example.core.events;

public record MouseDragEvent(
        int oldX, int oldY,
        int newX, int newY,
        int button,
        long timeElapsed,
        long timestamp
) implements InputEvent {}
