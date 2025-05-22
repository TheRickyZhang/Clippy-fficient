// com/example/core/events/MouseWheelEvent.java
package com.example.core.events;

public record MouseWheelEvent(
        int    eventType,
        int    wheelRotation,
        int    x,
        int    y,
        long   timestamp
) implements InputEvent {}
