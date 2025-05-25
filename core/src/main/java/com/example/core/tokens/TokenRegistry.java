package com.example.core.tokens;

import com.example.core.events.InputEvent;
import com.example.core.events.KeyPressEvent;
import com.example.core.events.KeyReleaseEvent;
import com.example.core.events.KeyTypedEvent;
import com.example.core.events.MouseClickEvent;
import com.example.core.events.MouseDragEvent;
import com.example.core.events.MouseMoveEvent;
import com.example.core.events.MousePressEvent;
import com.example.core.events.MouseReleaseEvent;
import com.example.core.events.MouseWheelEvent;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

public final class TokenRegistry {
    private TokenRegistry() {}

    public static final Map<String, Predicate<InputEvent>> TOKEN_MAP;
    public static final List<TokenMeta> TOKEN_METAS;

    static {
        Map<String, Predicate<InputEvent>> m = new HashMap<>();
        List<TokenMeta> metas = new ArrayList<>();

        // Helper maps to link JNativeHook event type IDs to your new specific event classes
        Map<Integer, Class<? extends InputEvent>> nativeIdToKeyEventClass = new HashMap<>();
        nativeIdToKeyEventClass.put(NativeKeyEvent.NATIVE_KEY_PRESSED, KeyPressEvent.class);
        nativeIdToKeyEventClass.put(NativeKeyEvent.NATIVE_KEY_RELEASED, KeyReleaseEvent.class);
        nativeIdToKeyEventClass.put(NativeKeyEvent.NATIVE_KEY_TYPED, KeyTypedEvent.class);

        Map<Integer, Class<? extends InputEvent>> nativeIdToMouseEventClass = new HashMap<>();
        nativeIdToMouseEventClass.put(NativeMouseEvent.NATIVE_MOUSE_PRESSED, MousePressEvent.class);
        nativeIdToMouseEventClass.put(NativeMouseEvent.NATIVE_MOUSE_RELEASED, MouseReleaseEvent.class);
        nativeIdToMouseEventClass.put(NativeMouseEvent.NATIVE_MOUSE_CLICKED, MouseClickEvent.class);
        nativeIdToMouseEventClass.put(NativeMouseEvent.NATIVE_MOUSE_MOVED, MouseMoveEvent.class);
        nativeIdToMouseEventClass.put(NativeMouseEvent.NATIVE_MOUSE_DRAGGED, MouseDragEvent.class);
        nativeIdToMouseEventClass.put(NativeMouseEvent.NATIVE_MOUSE_WHEEL, MouseWheelEvent.class);

        try {
            // PRESSED, RELEASED keys
            for (Field f : NativeKeyEvent.class.getFields()) {
                if (Modifier.isStatic(f.getModifiers()) && f.getType() == int.class && f.getName().startsWith("VC_")) {
                    int keyCode = f.getInt(null);
                    String tokenName = f.getName().substring("VC_".length());
                    m.put(tokenName + "*", e -> e instanceof KeyPressEvent kpe && kpe.keyCode() == keyCode);
                    m.put(tokenName + "^", e -> e instanceof KeyReleaseEvent kpe && kpe.keyCode() == keyCode);
                    metas.add(new TokenMeta(tokenName + "*", KeyPressEvent.class, NativeKeyEvent.NATIVE_KEY_PRESSED, keyCode, null, null, null));
                    metas.add(new TokenMeta(tokenName + "^", KeyPressEvent.class, NativeKeyEvent.NATIVE_KEY_RELEASED, keyCode, null, null, null));
                }
            }

            // TYPED Keys
            for (char c = 32; c < 127; c++) {
                String tokenName = String.valueOf(c);
                final char finalC = c;
                m.put(tokenName,  e -> e instanceof KeyTypedEvent kte && kte.keyChar() == finalC);
                metas.add(new TokenMeta(tokenName, KeyTypedEvent.class, NativeKeyEvent.NATIVE_KEY_TYPED, null, finalC, null, null));
            }

            // Mouse Actions (ex. Clicked)
            for (Field f : NativeMouseEvent.class.getFields()) {
                if (Modifier.isStatic(f.getModifiers()) && f.getType() == int.class && f.getName().startsWith("NATIVE_MOUSE_")) {
                    if (f.getName().equals("NATIVE_MOUSE_ENTRY") || f.getName().equals("NATIVE_MOUSE_EXIT")) continue; // Optional: Skip if not needed

                    int nativeId = f.getInt(null);
                    String tokenName = f.getName().substring("NATIVE_".length());

                    Class<? extends InputEvent> specificEventClass = nativeIdToMouseEventClass.get(nativeId);
                    if (specificEventClass != null) {
                        Predicate<InputEvent> p = specificEventClass::isInstance;
                        m.put(tokenName, p);
                        metas.add(new TokenMeta(tokenName, specificEventClass, nativeId, null, null, null, null));
                    }
                }
            }

            // Mouse Button Tokens (TODO: Inspect if this is really correct / necessary? Seems like it might need an exception in syntax handling for MousePressed(Button))
            for (Field f : NativeMouseEvent.class.getFields()) {
                if (Modifier.isStatic(f.getModifiers()) && f.getType() == int.class &&
                        f.getName().startsWith("BUTTON") && !f.getName().equals("BUTTON_NOBUTTON") &&
                        f.getInt(null) >= NativeMouseEvent.BUTTON1 && f.getInt(null) <= NativeMouseEvent.BUTTON5) {
                    int button = f.getInt(null);
                    String tokenName = f.getName();
                    Predicate<InputEvent> p = e -> e instanceof MousePressEvent mpe && mpe.button() == button;
                    m.put(tokenName, p);
                    metas.add(new TokenMeta(tokenName, MousePressEvent.class, NativeMouseEvent.NATIVE_MOUSE_PRESSED, null, null, button, null));
                }
            }

            // Wheel Scrolling
            int nativeMouseWheelEventType = NativeMouseEvent.NATIVE_MOUSE_WHEEL;

            Predicate<InputEvent> scrollUp = e -> e instanceof MouseWheelEvent mwe && mwe.cumScroll() < 0;
            m.put("SCROLL_UP", scrollUp);
            metas.add(new TokenMeta("SCROLL_UP", MouseWheelEvent.class, nativeMouseWheelEventType, null, null, null, -1));

            Predicate<InputEvent> scrollDown = e -> e instanceof MouseWheelEvent mwe && mwe.cumScroll() > 0;
            m.put("SCROLL_DOWN", scrollDown);
            metas.add(new TokenMeta("SCROLL_DOWN", MouseWheelEvent.class, nativeMouseWheelEventType, null, null, null, 1));

        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Failed to access JNativeHook public static fields", ex);
        }

        TOKEN_MAP = Map.copyOf(m);
        TOKEN_METAS = List.copyOf(metas);
    }

    /**
     * Returns a list of entries, where each entry maps a token string
     * to a list of its properties. This is suitable for display or configuration.
     * The properties list typically includes:
     * - Simple name of the event class (e.g., "KeyPressEvent")
     * - Native event type (e.g., "type=1400" for NATIVE_KEY_PRESSED)
     * - Key code, if applicable (e.g., "code=65" for VC_A)
     * - Key character, if applicable (e.g., "char=A")
     * - Mouse button, if applicable (e.g., "button=1" for BUTTON1)
     * - Wheel rotation, if applicable (e.g., "wheel=-1" for SCROLL_UP)
     */
    public static List<Map.Entry<String, List<String>>> getTokenMapValues() {
        List<Map.Entry<String, List<String>>> res = new ArrayList<>(TOKEN_METAS.size());

        for (TokenMeta meta : TOKEN_METAS) {
            List<String> values = new ArrayList<>();
            values.add(meta.eventClass().getSimpleName());
            values.add("type=" + meta.eventType()); // Native event type ID
            if (meta.keyCode()       != null) values.add("code="   + meta.keyCode());
            if (meta.keyChar()       != null) values.add("char="   + meta.keyChar()); // For display, consider Character.toString() if it's just 'c'
            if (meta.button()        != null) values.add("button=" + meta.button());
            if (meta.wheelRotation() != null) values.add("wheel="  + meta.wheelRotation());

            res.add(new AbstractMap.SimpleEntry<>(meta.token(), Collections.unmodifiableList(values)));
        }
        return Collections.unmodifiableList(res);
    }
}
