package com.example.core.events;

public record MouseMoveEvent(
        int oldX, int oldY,
        int newX, int newY,
        long timeElapsed,
        long timestamp
) implements InputEvent {}
