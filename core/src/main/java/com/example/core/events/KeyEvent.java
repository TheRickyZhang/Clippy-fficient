package com.example.core.events;

public record KeyEvent(int keyCode, int modifiers, long timestamp) implements InputEvent {
}
