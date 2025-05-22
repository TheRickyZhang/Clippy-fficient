package com.example.core.tokens;

import com.example.core.events.InputEvent;

public record TokenMeta(
        String                  token,
        Class<? extends InputEvent> eventClass,
        int                     eventType,
        Integer                 keyCode,        // null if N/A
        Character               keyChar,        // null if N/A
        Integer                 button,         // null if N/A
        Integer                 wheelRotation   // null if N/A
) {}
