package com.example.core.events;

/*
 * Our unified application implementation of any mouse implementation
 * @param eventType: See NativeMouse, (NATIVE_MOUSE_) + {CLICKED, PRESSED, RELEASED, MOVED, DRAGGED, WHEEL}
 * @param button: (BUTTON) + {0, 1, 2, 3, 4, 5}
 * @param x:
 * @param y:
 * @param clickCount:
 * @param timestamp:
 */
public record MouseEvent(
        int    eventType,
        int    button,
        int    x,
        int    y,
        int    clickCount,
        long   timestamp
) implements InputEvent {}
