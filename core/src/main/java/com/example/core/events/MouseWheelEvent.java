package com.example.core.events;

public record MouseWheelEvent(
        int cumScroll,
        int x, int y,
        int newX, int newY,
        long timestamp
) implements InputEvent {}

