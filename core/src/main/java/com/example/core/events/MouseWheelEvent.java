// com/example/core/events/MouseWheelEvent.java
package com.example.core.events;

public record MouseWheelEvent(int scrollDelta, long timestamp) implements InputEvent {
}
