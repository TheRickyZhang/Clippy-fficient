package com.example.core.events;

public record MouseWheelEvent(int rotation, int amount, long timestamp) implements InputEvent { }
