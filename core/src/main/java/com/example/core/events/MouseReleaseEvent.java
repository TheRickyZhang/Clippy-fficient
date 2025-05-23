package com.example.core.events;

public record MouseReleaseEvent(
        int button,
        int x,
        int y,
        long timestamp
) implements InputEvent {}
