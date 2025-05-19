package com.example.core;

import com.example.core.context.ApplicationContext;
import com.example.core.events.InputEvent;
import com.example.core.sequence.EventBuffer;
import com.example.core.sequence.InputSequence;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// The main program engine that maps sequence patterns to actions, maintains a buffer, and parses incoming input
public class ShortcutEngine {
    private final ApplicationContext context;
    private final EventBuffer buffer = new EventBuffer(10);
    private final Map<InputSequence,Runnable> actions = new ConcurrentHashMap<>();
    private final Map<InputSequence, String> tips = new ConcurrentHashMap<>();


    public ShortcutEngine() {
        this.context = new ApplicationContext(List.of());
    }
    //TODO: Allow initial starting context? Or retrieve initial context if opening with multiple windows?
//    public ShortcutEngine(ApplicationContext context) {
//        this.context = context;
//    }

    public void addAction(InputSequence pattern, Runnable action) {
        actions.put(pattern, action);
    }
    public void removeAction(InputSequence pattern) {
        actions.remove(pattern);
    }
    public void addTip(InputSequence pattern, String tip) {
        tips.put(pattern, tip);
    }
    public void removeTip(InputSequence pattern) {
        tips.remove(pattern);
    }

    public void updateContext(ApplicationContext c) {
        this.context.combine(c);
    }

    public void onEvent(InputEvent e) {
        buffer.push(e);
        Deque<InputEvent> curr = buffer.snapshot();
        // TODO: Edit context to also support removal and efficient mapping / hierarchy of information
        if(this.context.info().contains("Chrome")) {
            System.out.println("Recognized context in chrome");
        }
        actions.forEach((p, action) -> {
            if (p.matches(curr)) {
                action.run();
            }
        });
        tips.forEach((seq, tip) -> {
            if(this.context.info().contains("AHHHH")) {
                System.out.println(tip);
            }
        });
    }
}
