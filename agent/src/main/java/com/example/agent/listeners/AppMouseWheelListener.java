package com.example.agent.listeners;

import com.example.agent.samplers.MouseWheelSampler;
import com.example.core.events.InputEvent;
import com.example.core.events.MouseWheelEvent;
import com.example.core.logging.LogService;
import com.example.agent.utils.MouseWheelSamplerReturn;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class AppMouseWheelListener implements NativeMouseWheelListener {
    private static final Logger log = LogService.get().forClass(AppMouseWheelListener.class);
    private final Consumer<InputEvent> handler;

    private final MouseWheelSampler scrollSampler;

    public AppMouseWheelListener(Consumer<InputEvent> handler) {
        this.handler = handler;
        this.scrollSampler = new MouseWheelSampler();
        log.info("Global Mouse Motion Listener created");
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
        int scroll = e.getWheelRotation() * e.getScrollAmount();
        MouseWheelSamplerReturn t = scrollSampler.report(scroll, e.getX(), e.getY());
        if(t != null) {
            handler.accept(new MouseWheelEvent(
                    t.scroll(),
                    t.x(), t.y(),
                    e.getX(), e.getY(),
                    System.currentTimeMillis()
            ));
            LogService.detailed().info("Mouse Wheel Moved" + e.paramString());
            LogService.simple().info("Mouse Wheel Moved" + t.scroll());
        }
    }
}
