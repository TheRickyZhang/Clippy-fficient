package com.example.agent;

import com.example.core.events.InputEvent;
import com.example.core.events.MouseEvent;
import com.example.core.events.MouseWheelEvent;
import com.example.core.logging.LogService;
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

    public GlobalMouseListener(Consumer<InputEvent> handler) {
        this.handler = handler;
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        handler.accept(new MouseEvent(
                e.getID(), e.getButton(),
                e.getX(), e.getY(),
                e.getClickCount(),
                System.currentTimeMillis()
        ));
        log.info("Mouse Button " + e.paramString() + " Clicked");
    }
    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        handler.accept(new MouseEvent(
                e.getID(), e.getButton(),
                e.getX(), e.getY(),
                e.getClickCount(),
                System.currentTimeMillis()
        ));
        log.info("Mouse Button " + e.paramString() + " Clicked");
    }
    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        handler.accept(new MouseEvent(
                e.getID(), e.getButton(),
                e.getX(), e.getY(),
                e.getClickCount(),
                System.currentTimeMillis()
        ));
        log.info("Mouse Button " + e.paramString() + " Released");
    }
    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        handler.accept(new MouseEvent(
                e.getID(), NativeMouseEvent.NOBUTTON,
                e.getX(), e.getY(),
                0,
                System.currentTimeMillis()
        ));
        log.info("Mouse " + e.paramString() + " Moved");
    }
    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
        handler.accept(new MouseEvent(
                e.getID(), e.getButton(),
                e.getX(), e.getY(),
                e.getClickCount(),
                System.currentTimeMillis()
        ));
        log.info("Mouse " + e.paramString() + " Dragged");
    }

    // *** separate wheel listener ***
    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
        handler.accept(new MouseWheelEvent(
                e.getID(),
                e.getWheelRotation(),
                e.getX(), e.getY(),
                System.currentTimeMillis()
        ));
        log.info("Mouse Wheel " + e.paramString() + " Moved");
    }
}
