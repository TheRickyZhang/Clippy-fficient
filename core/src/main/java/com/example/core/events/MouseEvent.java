package com.example.core.events;

public record MouseEvent(int x, int y, MouseButton button, long timestamp) implements InputEvent { }
