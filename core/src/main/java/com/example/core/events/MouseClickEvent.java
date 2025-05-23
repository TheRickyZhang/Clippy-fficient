package com.example.core.events;

public record MouseClickEvent(
        int button,
        int clickCount,
        int x,
        int y,
        long timestamp
) implements InputEvent {}
