// agent/src/main/java/com/example/agent/listener/GlobalMouseListener.java
package com.example.agent.listener;

import com.example.core.events.InputEvent;
import com.example.core.events.MouseButton;
import com.example.core.events.MouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import java.util.function.Consumer;

public class GlobalMouseListener implements NativeMouseListener {
    private final Consumer<InputEvent> handler;

    public GlobalMouseListener(Consumer<InputEvent> handler) {
        this.handler = handler;
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        handler.accept(toCore(e));
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        handler.accept(toCore(e));
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        handler.accept(toCore(e));
    }

    private MouseEvent toCore(NativeMouseEvent e) {
        MouseButton b;
        switch(e.getButton()) {
            case NativeMouseEvent.BUTTON1 -> b = MouseButton.PRIMARY;
            case NativeMouseEvent.BUTTON2 -> b = MouseButton.MIDDLE;
            case NativeMouseEvent.BUTTON3 -> b = MouseButton.SECONDARY;
            default -> b = MouseButton.OTHER;
        }
        return new MouseEvent(e.getX(), e.getY(), b, System.currentTimeMillis());
    }
}
