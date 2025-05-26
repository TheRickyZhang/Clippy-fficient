package com.example.agent.listeners;

import com.example.core.events.InputEvent;
import com.example.core.events.KeyPressEvent;
import com.example.core.events.KeyReleaseEvent;
import com.example.core.events.KeyTypedEvent;
import com.example.core.logging.LogService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.function.Consumer;
import java.util.logging.Logger;

// See KeyEvent for specifics of converting keys
public class AppKeyListener implements NativeKeyListener {
    private final Consumer<InputEvent> handler;
    private static final Logger log = LogService.get().forClass(AppKeyListener.class);

    /**
     * @param handler a callback to receive each wrapped InputEvent
     */
    public AppKeyListener(Consumer<InputEvent> handler) {
        this.handler = handler;
        log.info("Global Key Listener created");
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        handler.accept(new KeyPressEvent(e.getKeyCode(), e.getModifiers(), System.currentTimeMillis()));
        LogService.detailed().info("Key " + NativeKeyEvent.getKeyText(e.getKeyCode()) + " Pressed");
        LogService.simple().info(NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        handler.accept(new KeyReleaseEvent(e.getKeyCode(), e.getModifiers(), System.currentTimeMillis()));
        LogService.detailed().info("Key " + NativeKeyEvent.getKeyText(e.getKeyCode()) + " Released");
        LogService.simple().info(NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // only typed events carry a real char, but we can still forward code+mods
        handler.accept(new KeyTypedEvent(e.getKeyChar(), e.getModifiers(), System.currentTimeMillis()));
        LogService.detailed().info("Key " + e.getKeyChar() + " Typed");
        LogService.simple().info(String.valueOf(e.getKeyChar()));
    }
}
