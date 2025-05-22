package com.example.core.events;

/*
 * Our unified application representation of any key event, derived from JNativeHook's NativeKeyEvent
 *
 * @eventType: NATIVE_KEY_PRESSED, NATIVE_KEY_RELEASED, NATIVE_KEY_TYPED,
 * @keyCode: Specific key used (VC_XXX constant)
 * @keyChar: The specific character typed (ONLY exists on NATIVE_KEY_TYPED)
 * @modifiers: Bitset of Ctrl, Shift, Alt, etc.
 * @timestamp:
 */
public record KeyEvent(int eventType, int keyCode, int keyChar, int modifiers, long timestamp) implements InputEvent {
}
