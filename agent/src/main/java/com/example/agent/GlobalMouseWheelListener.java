// agent/src/main/java/com/example/agent/listener/GlobalMouseWheelListener.java
package com.example.agent;

import com.example.core.events.InputEvent;
import com.example.core.events.MouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

import java.util.function.Consumer;

public class GlobalMouseWheelListener implements NativeMouseWheelListener {
    private final Consumer<InputEvent> handler;

    public GlobalMouseWheelListener(Consumer<InputEvent> handler) {
        this.handler = handler;
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
        handler.accept(new MouseWheelEvent(
                // TO LOOK: Encoding the direction and magnitude together
                e.getWheelRotation() * e.getScrollAmount(),
                System.currentTimeMillis()
        ));
    }
}
