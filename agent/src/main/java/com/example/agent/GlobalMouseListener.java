package com.example.agent;

import com.example.agent.samplers.MouseEventSampler;
import com.example.agent.samplers.ScrollEventSampler;
import com.example.core.events.*;
import com.example.core.logging.LogService;
import com.example.core.utils.Pair;
import com.example.core.utils.ScrollDelta;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class GlobalMouseListener
        implements NativeMouseInputListener, NativeMouseWheelListener
{
    private final Consumer<InputEvent> handler;
    private static final Logger log = LogService.get().forClass(GlobalMouseListener.class);

    // FUTURE: Make modifiable
    private final MouseEventSampler movementSampler;
    private final MouseEventSampler dragSampler;
    private final ScrollEventSampler scrollSampler;

    public GlobalMouseListener(Consumer<InputEvent> handler) {
        this.handler = handler;
        movementSampler = new MouseEventSampler();
        dragSampler = new MouseEventSampler();
        scrollSampler = new ScrollEventSampler();
        log.info("Global Mouse Listener created");
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        handler.accept(new MouseClickEvent(
                e.getButton(),
                e.getClickCount(),
                e.getX(), e.getY(),
                System.currentTimeMillis()
        ));
        LogService.detailed().info("Mouse Button " + e.paramString() + " Clicked");
        LogService.simple().info("Mouse Clicked");
    }
    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        handler.accept(new MousePressEvent(
                e.getButton(),
                e.getX(), e.getY(),
                System.currentTimeMillis()
        ));
        LogService.detailed().info("Mouse Button " + e.paramString() + " Pressed");
        LogService.simple().info("Mouse Pressed");
    }
    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        handler.accept(new MouseReleaseEvent(
                e.getButton(),
                e.getX(), e.getY(),
                System.currentTimeMillis()
        ));
        LogService.detailed().info("Mouse Button " + e.paramString() + " Released");
        LogService.simple().info("Mouse Released");
    }

    /*
     * Since these would normally be firing rapidly for continuous movement, parse through EventSamplers.
     */
    // TODO: Modify the input events class to handle {oldX, oldY, newX, newY}
    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        Pair p = movementSampler.report(e.getX(), e.getY());
        if(p != null) {
            handler.accept(new MouseMoveEvent(
                    p.x(), p.y(),
                    e.getX(), e.getY(),
                    movementSampler.getINTERVAL_MS(),
                    System.currentTimeMillis()
            ));
            LogService.detailed().info("Mouse Moved" + e.paramString());
            LogService.simple().info("Mouse Moved");
        }
    }
    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
        Pair p = dragSampler.report(e.getX(), e.getY());
        if(p != null) {
            handler.accept(new MouseDragEvent(
                    p.x(), p.y(),
                    e.getX(), e.getY(),
                    e.getButton(),
                    dragSampler.getINTERVAL_MS(),
                    System.currentTimeMillis()
            ));
            LogService.detailed().info("Mouse Dragged" + e.paramString());
            LogService.simple().info("Mouse Dragged");
        }
    }

    // Note: NativeMouseWheelEvent here is a subclass of NativeMouseEvent and so inherits some useless (?) x, y fields
    // We really only care about the getWheelRotation() amount - ignoring UNIT_SCROLL and BLOCK_SCROLL (page up/down) for now
    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
        int scroll = e.getWheelRotation() * e.getScrollAmount();
        ScrollDelta t = scrollSampler.report(scroll, e.getX(), e.getY());
        if(t != null) {
            handler.accept(new MouseWheelEvent(
                    t.scroll(),
                    t.x(), t.y(),
                    e.getX(), e.getY(),
                    System.currentTimeMillis()
            ));
            LogService.detailed().info("Mouse Wheel Moved" + e.paramString());
            LogService.simple().info("Mouse Wheel Moved");
        }
    }
}
