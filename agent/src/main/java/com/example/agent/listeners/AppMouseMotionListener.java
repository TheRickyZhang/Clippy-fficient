package com.example.agent.listeners;

import com.example.agent.samplers.MouseMotionSampler;
import com.example.agent.utils.MouseMotionSamplerReturn;
import com.example.core.events.InputEvent;
import com.example.core.events.MouseDragEvent;
import com.example.core.events.MouseMoveEvent;
import com.example.core.logging.LogService;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class AppMouseMotionListener implements NativeMouseMotionListener {

    private final Consumer<InputEvent> handler;
    private static final Logger log = LogService.get().forClass(AppMouseListener.class);

    // FUTURE: Make modifiable
    private final MouseMotionSampler movementSampler;
    private final MouseMotionSampler dragSampler;

    public AppMouseMotionListener(Consumer<InputEvent> handler) {
        this.handler = handler;
        movementSampler = new MouseMotionSampler();
        dragSampler = new MouseMotionSampler();
        log.info("Global Mouse Motion Listener created");
    }

    /*
     * Since these would normally be firing rapidly for continuous movement, parse through EventSamplers.
     */
    // TODO: Modify the input events class to handle {oldX, oldY, newX, newY}
    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        MouseMotionSamplerReturn r = movementSampler.report(e.getX(), e.getY());
        if(r != null) {
            handler.accept(new MouseMoveEvent(
                    r.x(), r.y(),
                    e.getX(), e.getY(),
                    r.duration(),
                    System.currentTimeMillis()
            ));
            LogService.detailed().info("Mouse Moved" + e.paramString());
            LogService.simple().info("Mouse Moved");
        }
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
        MouseMotionSamplerReturn r = dragSampler.report(e.getX(), e.getY());
        if(r != null) {
            handler.accept(new MouseDragEvent(
                    r.x(), r.y(),
                    e.getX(), e.getY(),
                    e.getButton(),
                    r.duration(),
                    System.currentTimeMillis()
            ));
            LogService.detailed().info("Mouse Dragged" + e.paramString());
            LogService.simple().info("Mouse Dragged");
        }
    }
}
