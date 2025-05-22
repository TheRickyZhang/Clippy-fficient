package com.example.agent;

import com.example.core.events.InputEvent;
import com.example.core.events.KeyEvent;
import com.example.core.logging.LogService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.function.Consumer;
import java.util.logging.Logger;

// See KeyEvent for specifics of converting keys
public class GlobalKeyListener implements NativeKeyListener {
    private final Consumer<InputEvent> handler;
    private static final Logger log = LogService.get().forClass(GlobalKeyListener.class);
    /**
     * @param handler a callback to receive each wrapped InputEvent
     */
    public GlobalKeyListener(Consumer<InputEvent> handler) {
        this.handler = handler;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        handler.accept(new KeyEvent(e.getID(), e.getKeyCode(), '\0', e.getModifiers(), System.currentTimeMillis()));
        log.info("Key " + NativeKeyEvent.getKeyText(e.getKeyCode()) + " Pressed");
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        handler.accept(new KeyEvent(e.getID(), e.getKeyCode(), '\0', e.getModifiers(), System.currentTimeMillis()));
        log.info("Key " + NativeKeyEvent.getKeyText(e.getKeyCode()) + " Released");
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // only typed events carry a real char, but you can still forward code+mods if you like
        handler.accept(new KeyEvent(e.getID(), e.getKeyCode(), e.getKeyChar(), e.getModifiers(), System.currentTimeMillis()));
        log.info("Key " + e.getKeyChar() + " Typed");
    }
}
