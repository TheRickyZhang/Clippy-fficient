package com.example.core.sequence;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class KeyCodeLookup {
    /**
     * Returns the integer key code for the given short name.
     * E.g. getKeyCode("F7") -> value of NativeKeyEvent.VC_F7.
     * @throws IllegalArgumentException if the constant does not exist or is not an int
     */
    public static int getKeyCode(String shortName) {
        Integer code = KEY_CODES.get(shortName);
        if (code == null) {
            throw new IllegalArgumentException("Unknown key: " + shortName);
        }
        return code;
    }

    // Prepopulate a map of VC_* constants via reflection
    private static final Map<String,Integer> KEY_CODES;
    static {
        Map<String,Integer> map = new HashMap<>();
        try {
            for (Field f : NativeKeyEvent.class.getFields()) {
                if (Modifier.isStatic(f.getModifiers())
                        && f.getType() == int.class
                        && f.getName().startsWith("VC_")) {

                    String name = f.getName().substring(3); // drop "VC_"
                    int val = f.getInt(null);
                    map.put(name, val);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to initialize key codes", e);
        }
        KEY_CODES = Collections.unmodifiableMap(map);
    }
}
