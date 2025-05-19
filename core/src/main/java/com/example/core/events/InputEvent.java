package com.example.core.events;

public sealed interface InputEvent
        permits KeyEvent, MouseEvent, MouseWheelEvent
{
    long timestamp();
}
