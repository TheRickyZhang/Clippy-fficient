package com.example.core.sequence;

import com.example.core.events.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

public class InputSequence {
    private final List<Predicate<InputEvent>> steps;
    private final List<String> tokens;
    private static final Map<String, Predicate<InputEvent>> TOKEN_MAP;

    static {
        var m = new HashMap<String, Predicate<InputEvent>>();

        // keyboard tokens via TEXT_TO_VK
        try {
            for (Field f : java.awt.event.KeyEvent.class.getFields()) {
                if (!f.getName().startsWith("VK_")) continue;
                int code = f.getInt(null);
                String name = java.awt.event.KeyEvent.getKeyText(code).toUpperCase();
                m.put(name, e ->
                        e instanceof KeyEvent ke && ke.keyCode() == code
                );
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // 2) mouse tokens
        m.put("CLICK",      e -> e instanceof MouseEvent me    && me.button()==MouseButton.PRIMARY);
        m.put("RIGHT",      e -> e instanceof MouseEvent me    && me.button()==MouseButton.SECONDARY);
        m.put("MIDDLE",     e -> e instanceof MouseEvent me    && me.button()==MouseButton.MIDDLE);
        m.put("SCROLL_UP",  e -> e instanceof MouseWheelEvent mw && mw.scrollDelta()<0);
        m.put("SCROLL_DOWN",e -> e instanceof MouseWheelEvent mw && mw.scrollDelta()>0);

        TOKEN_MAP = Map.copyOf(m);
        System.out.println("Token map" + TOKEN_MAP);
    }

    public InputSequence(String raw) {
        this.tokens = Arrays.stream(raw.split(">"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        this.steps = tokens.stream().map(s -> {
            var token = TOKEN_MAP.get(s);
            if(token == null) {
                throw new IllegalArgumentException("");
            }
            return token;
        }).toList();
    }


    public static List<String> getTokens() {
        return new ArrayList<>(TOKEN_MAP.keySet());
    }

    public String toString() {
        return String.join(" > ", tokens);
    }



    public boolean matches(Deque<InputEvent> buf) {
        if(buf.size() < steps.size()) return false;
        InputEvent[] arr = buf.toArray(new InputEvent[0]);
        int start = buf.size() - steps.size();
        for(int i = 0; i < steps.size(); i++) {
            Predicate<InputEvent> p = steps.get(i);
            if(!p.test(arr[start + i])) return false;
        }
        return true;
    }

}
