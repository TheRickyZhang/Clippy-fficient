package com.example.agent;

import com.example.agent.listeners.AppKeyListener;
import com.example.agent.listeners.AppMouseListener;
import com.example.agent.listeners.AppMouseMotionListener;
import com.example.agent.listeners.AppMouseWheelListener;
import com.example.core.events.InputEvent;
import com.example.core.logging.LogService;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class GlobalHookService {
    private static boolean running = false;
    private final NativeKeyListener keyListener;
    private final NativeMouseListener mouseListener;
    private final NativeMouseMotionListener mouseMotionListener;
    private final NativeMouseWheelListener mouseWheelListener;

    private static final Logger log = LogService.get().forClass(GlobalHookService.class);

    /**
     * @param handler a Consumer<InputEvent> to receive every raw event
     */
    public GlobalHookService(Consumer<InputEvent> handler) {
        this.keyListener = new AppKeyListener(handler);
        this.mouseListener = new AppMouseListener(handler);
        this.mouseMotionListener = new AppMouseMotionListener(handler);
        this.mouseWheelListener = new AppMouseWheelListener(handler);
        start();
    }

    public void start() throws RuntimeException {
        if (running) {
            log.warning("GlobalHookService is still running!");
            return;
        }
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this.keyListener);
            GlobalScreen.addNativeMouseListener(this.mouseListener);
            GlobalScreen.addNativeMouseMotionListener(this.mouseMotionListener);
            GlobalScreen.addNativeMouseWheelListener(this.mouseWheelListener);
        } catch (NativeHookException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        running = true;
    }

    public static void stop() throws RuntimeException {
        if (!running) {
            log.warning("GlobalHookService has not started yet!");
            return;
        }
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            log.severe("Failed to unregister native hook");
            ex.printStackTrace();
            throw new RuntimeException("HUH");
        }
        running = false;
    }
}
