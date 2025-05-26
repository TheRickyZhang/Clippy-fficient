package com.example.core.tokens;

import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public final class TokenRegistry {
    private TokenRegistry() {}

    /** Maps a token string (e.g. "A*", "VC_ENTER^", "BUTTON1", "SCROLL_UP") */
    // Have this contain *, ^, ,
    public static final Map<String, Integer> KEY_MAP;
    public static final Map<String, Integer> MODIFIER_MAP;
    // TODO
//    public static final Map<String, Integer> MOUSE_ACTION_MAP;

    public static List<Map.Entry<String, Integer>> getTokenMapValues() {
        return Stream.of(KEY_MAP, MODIFIER_MAP)
                .filter(Objects::nonNull)
                .flatMap(m -> m.entrySet().stream())
                .toList();
    }

    static {
        Map<String, Integer> tokenMap = new HashMap<>();
        Map<String,Integer> maskMap = new HashMap<>();

        try {
            // Key Pressed / Released
            for (Field f : NativeKeyEvent.class.getFields()) {
                if (Modifier.isStatic(f.getModifiers())
                        && f.getType() == int.class
                        && f.getName().startsWith("VC_")) {
                    // Retrieve the static fields via reflection with null
                    int keyCode = f.getInt(null);
                    String name = f.getName().substring("VC_".length());
                    tokenMap.put(name + "*", keyCode);
                    tokenMap.put(name + "^", keyCode);
                }
            }

            // Key Typed (printable ASCII)
            for (char c = 32; c < 127; c++) {
                tokenMap.put(String.valueOf(c), (int) c);
            }

            for (Field f : NativeInputEvent.class.getFields()) {
                int mods = f.getModifiers();
                if (Modifier.isStatic(mods)
                        && f.getType() == int.class
                        && f.getName().endsWith("_MASK")) {
                    int maskVal = f.getInt(null);
                    String key = f.getName().substring(0, f.getName().length() - "_MASK".length());
                    maskMap.put(key, maskVal);
                }
            }
            KEY_MAP = Collections.unmodifiableMap(tokenMap);
            MODIFIER_MAP = Collections.unmodifiableMap(maskMap);



            // TODO: Mouse
//            // Mouse event classes mapping
//            Map<Integer, Class<? extends InputEvent>> mouseMap = Map.of(
//                    NativeMouseEvent.NATIVE_MOUSE_PRESSED,  MousePressEvent.class,
//                    NativeMouseEvent.NATIVE_MOUSE_RELEASED, MouseReleaseEvent.class,
//                    NativeMouseEvent.NATIVE_MOUSE_CLICKED,  MouseClickEvent.class,
//                    NativeMouseEvent.NATIVE_MOUSE_MOVED,    MouseMoveEvent.class,
//                    NativeMouseEvent.NATIVE_MOUSE_DRAGGED,  MouseDragEvent.class,
//                    NativeMouseEvent.NATIVE_MOUSE_WHEEL,    MouseWheelEvent.class
//            );
//
//            // Mouse action tokens
//            for (Field f : NativeMouseEvent.class.getFields()) {
//                if (Modifier.isStatic(f.getModifiers())
//                        && f.getType() == int.class
//                        && f.getName().startsWith("NATIVE_MOUSE_")) {
//                    int nativeId = f.getInt(null);
//                    Class<? extends InputEvent> cls = mouseMap.get(nativeId);
//                    if (cls != null) {
//                        String token = f.getName().substring("NATIVE_".length());
//                        InputEvent proto = cls.getDeclaredConstructor().newInstance();
//                        m.put(token, proto);
//                    }
//                }
//            }
//
//            // Mouse buttons
//            for (Field f : NativeMouseEvent.class.getFields()) {
//                if (Modifier.isStatic(f.getModifiers())
//                        && f.getType() == int.class
//                        && f.getName().startsWith("BUTTON")
//                        && !f.getName().equals("BUTTON_NOBUTTON")) {
//
//                    int button = f.getInt(null);
//                    String token = f.getName();
//                    // MousePressEvent holds button()
//                    m.put(token, new MousePressEvent(button));
//                }
//            }
//
//            // Scroll up/down
//            m.put("SCROLL_UP",   new MouseWheelEvent(-1));
//            m.put("SCROLL_DOWN", new MouseWheelEvent( 1));
//
        } catch (Exception e) {
            throw new RuntimeException("Failed to build token → event map", e);
        }
    }
}
