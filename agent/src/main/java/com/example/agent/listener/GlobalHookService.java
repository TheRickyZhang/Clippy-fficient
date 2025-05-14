package com.example.agent.listener;

import com.example.core.events.InputEvent;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import java.util.function.Consumer;

public class GlobalHookService {
    private static boolean running = false;

    /**
     * @param eventHandler a Consumer<InputEvent> to receive every raw event
     */
    public static void start(Consumer<InputEvent> eventHandler) throws RuntimeException {
        if (running) return;

        try {
            GlobalScreen.registerNativeHook();

            GlobalScreen.addNativeKeyListener(new GlobalKeyListener(eventHandler));
            GlobalScreen.addNativeMouseListener(new GlobalMouseListener(eventHandler));
            GlobalScreen.addNativeMouseWheelListener(new GlobalMouseWheelListener(eventHandler));
        } catch (NativeHookException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        running = true;
    }

    public static void stop() throws RuntimeException {
        if (!running) return;
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("Cannot unregister Native Hook");
            ex.printStackTrace();
            throw new RuntimeException("HUH");
        }
        running = false;
    }
}
