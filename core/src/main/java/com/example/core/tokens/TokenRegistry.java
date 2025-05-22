package com.example.core.tokens;

import com.example.core.events.InputEvent;
import com.example.core.events.KeyEvent;
import com.example.core.events.MouseEvent;
import com.example.core.events.MouseWheelEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

public final class TokenRegistry {
    private TokenRegistry() {}
    public static final Map<String, Predicate<InputEvent>> TOKEN_MAP;
    public static final List<TokenMeta> TOKEN_METAS;

    static {
        Map<String, Predicate<InputEvent>> m = new HashMap<>();
        List<TokenMeta> metas = new ArrayList<>();
        try {
            // 1) key event types (pressed, released, typed)
            for (Field f : NativeKeyEvent.class.getFields()) {
                if (!f.getName().startsWith("NATIVE_KEY_")) continue;
                int id = f.getInt(null);
                String name = f.getName().substring("NATIVE_".length());
                Predicate<InputEvent> p = e -> e instanceof KeyEvent ke && ke.eventType() == id;
                m.put(name, p);
                metas.add(new TokenMeta(name, KeyEvent.class,
                        id, null, null, null, null));
            }
            // 2) key codes (A, B, DIGIT1, F1…)
            for (Field f : NativeKeyEvent.class.getFields()) {
                if (!f.getName().startsWith("VC_")) continue;
                int code = f.getInt(null);
                String name = f.getName().substring(3);
                Predicate<InputEvent> p = e -> e instanceof KeyEvent ke && ke.keyCode() == code;
                m.put(name, p);
                metas.add(new TokenMeta(name, KeyEvent.class,
                        NativeKeyEvent.NATIVE_KEY_PRESSED, code, null, null, null));
            }
            for (char c = 32; c < 127; c++) {
                String tok = String.valueOf(c);
                char finalC = c;
                Predicate<InputEvent> p = e -> e instanceof KeyEvent ke &&
                        ke.eventType() == NativeKeyEvent.NATIVE_KEY_TYPED && ke.keyChar() == finalC;
                m.put(tok, p);
                metas.add(new TokenMeta(tok, KeyEvent.class,
                        NativeKeyEvent.NATIVE_KEY_TYPED, null, c, null, null));
            }
            // 4) mouse event types
            for (Field f : NativeMouseEvent.class.getFields()) {
                if (!f.getName().startsWith("NATIVE_MOUSE_")) continue;
                int id = f.getInt(null);
                String name = f.getName().substring("NATIVE_".length());
                Predicate<InputEvent> p = e ->
                        e instanceof MouseEvent me && me.eventType() == id;
                m.put(name, p);
                metas.add(new TokenMeta(name, MouseEvent.class, id, null, null, null, null));
            }
            // 5) mouse buttons
            for (Field f : NativeMouseEvent.class.getFields()) {
                if (!f.getName().startsWith("BUTTON")) continue;
                int btn = f.getInt(null);
                String name = f.getName();
                Predicate<InputEvent> p = e ->
                        e instanceof MouseEvent me && me.button() == btn;
                m.put(name, p);
                metas.add(new TokenMeta(name, MouseEvent.class,
                        NativeMouseEvent.NATIVE_MOUSE_PRESSED,
                        null, null, btn, null));
            }
            // 6) wheel up/down
            int wheelId = NativeMouseEvent.NATIVE_MOUSE_WHEEL;
            Predicate<InputEvent> up = e ->
                    e instanceof MouseWheelEvent we && we.eventType() == wheelId && we.wheelRotation() < 0;
            Predicate<InputEvent> dn = e ->
                    e instanceof MouseWheelEvent we && we.eventType() == wheelId && we.wheelRotation() > 0;
            m.put("SCROLL_UP", up);
            metas.add(new TokenMeta("SCROLL_UP", MouseWheelEvent.class,
                    wheelId, null, null, null, -1));
            m.put("SCROLL_DOWN", dn);
            metas.add(new TokenMeta("SCROLL_DOWN", MouseWheelEvent.class,
                    wheelId, null, null, null, +1));
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

        TOKEN_MAP = Map.copyOf(m);
        TOKEN_METAS = List.copyOf(metas);
    }

    // Usage of meta and building the token map values might be replaceable with NativeInputEvent.paramString()
    // Though it's probably not worth the effort and risk to change
    public static List<Map.Entry<String, List<String>>> getTokenMapValues() {
        List<Map.Entry<String, List<String>>> res = new ArrayList<>();

        for (TokenMeta meta : TOKEN_METAS) {
            // build the properties list
            List<String> values = new ArrayList<>();
            values.add(meta.eventClass().getSimpleName());
            values.add("type=" + meta.eventType());
            if (meta.keyCode()       != null) values.add("code="   + meta.keyCode());
            if (meta.keyChar()       != null) values.add("char="   + meta.keyChar());
            if (meta.button()        != null) values.add("button=" + meta.button());
            if (meta.wheelRotation() != null) values.add("wheel="  + meta.wheelRotation());

            // NOTE: we declare the entry against the interface Map.Entry<…,List<…>>
            Map.Entry<String, List<String>> entry =
                new AbstractMap.SimpleEntry<>(meta.token(), values);

            res.add(entry);
        }

        return res;
    }

}
