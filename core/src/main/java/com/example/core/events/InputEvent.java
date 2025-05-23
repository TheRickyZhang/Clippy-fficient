package com.example.core.events;

// Only MouseMoveEvent, MouseDragEvent, and MouseWheelEvent are aggregated periods. The others are discrete.
public sealed interface InputEvent
        permits KeyPressEvent,
        KeyReleaseEvent,
        KeyTypedEvent,
        MouseClickEvent,
        MousePressEvent,
        MouseReleaseEvent,
        MouseMoveEvent,
        MouseDragEvent,
        MouseWheelEvent {
    long timestamp();
}
