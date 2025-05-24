package com.example.agent.listeners;

import com.example.core.events.InputEvent;
import com.example.core.events.MouseClickEvent;
import com.example.core.events.MousePressEvent;
import com.example.core.events.MouseReleaseEvent;
import com.example.core.logging.LogService;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class AppMouseListener implements NativeMouseInputListener {

    private final Consumer<InputEvent> handler;
    private static final Logger log = LogService.get().forClass(AppMouseListener.class);

    public AppMouseListener(Consumer<InputEvent> handler) {
        this.handler = handler;
        log.info("Global Mouse Motion Listener created");
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

}
