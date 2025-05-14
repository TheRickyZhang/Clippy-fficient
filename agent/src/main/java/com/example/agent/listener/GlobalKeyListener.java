package com.example.agent.listener;

import com.example.core.events.InputEvent;
import com.example.core.events.KeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.function.Consumer;

public class GlobalKeyListener implements NativeKeyListener {
    private final Consumer<InputEvent> handler;

    /**
     * @param handler a callback to receive each wrapped InputEvent
     */
    public GlobalKeyListener(Consumer<InputEvent> handler) {
        this.handler = handler;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        handler.accept(new KeyEvent(e.getKeyCode(), e.getModifiers(), System.currentTimeMillis()));
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        handler.accept(new KeyEvent(e.getKeyCode(), e.getModifiers(), System.currentTimeMillis()));
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // only typed events carry a real char, but you can still forward code+mods if you like
        handler.accept(new KeyEvent(e.getKeyCode(), e.getModifiers(), System.currentTimeMillis()));
    }
}
