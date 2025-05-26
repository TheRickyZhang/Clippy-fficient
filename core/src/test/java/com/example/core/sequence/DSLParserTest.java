package com.example.core.sequence;

import com.github.kwhat.jnativehook.NativeInputEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DSLParserTest {
    @Test
    void simpleKeySequence() throws ParseException {
        List<SequenceElement> res = DSLParser.parse("A > B* > B^");
        assertEquals(5, res.size());
        assertTrue(res.get(0) instanceof KeyTypedAction(char character) && character == 'A');
        assertTrue(res.get(1) instanceof PauseKeyInterval(int min, int max) && min == 0 && max == 0);
        assertTrue(res.get(2) instanceof KeyPressAction kp && kp.nativeKeyCode() == KeyCodeLookup.getKeyCode("B"));
        assertTrue(res.get(3) instanceof PauseKeyInterval(int min, int max) && min == 0 && max == 0);
        assertTrue(res.get(4) instanceof KeyReleaseAction kr && kr.nativeKeyCode() == KeyCodeLookup.getKeyCode("B"));
    }

    @Test
    void timeInterval() throws ParseException {
        List<SequenceElement> res = DSLParser.parse("X [20] Y [10,1000] Z");
        assertEquals(5, res.size());
        assertTrue(res.get(0) instanceof KeyTypedAction(char character) && character == 'X');
        assertTrue(res.get(1) instanceof PauseTimeInterval(int min, int max) && min == 0 && max == 20);
        assertTrue(res.get(2) instanceof KeyTypedAction(char character) && character == 'Y');
        assertTrue(res.get(3) instanceof PauseTimeInterval(int min, int max) && min == 10 && max == 1000);
        assertTrue(res.get(4) instanceof KeyTypedAction(char character) && character == 'Z');
    }

    @Test
    void keyInterval() throws ParseException {
        List<SequenceElement> res = DSLParser.parse("X {20} Y {10,1000} Z");
        assertEquals(5, res.size());
        assertTrue(res.get(0) instanceof KeyTypedAction(char character) && character == 'X');
        assertTrue(res.get(1) instanceof PauseKeyInterval(int min, int max) && min == 0 && max == 20);
        assertTrue(res.get(2) instanceof KeyTypedAction(char character) && character == 'Y');
        assertTrue(res.get(3) instanceof PauseKeyInterval(int min, int max) && min == 10 && max == 1000);
        assertTrue(res.get(4) instanceof KeyTypedAction(char character) && character == 'Z');
    }

    @Test
    void keyModifiers() {
        List<SequenceElement> res = null;
        try {
            res = DSLParser.parse("CTRL+Z* > ALT+SHIFT+F1*");
        } catch (ParseException e) {
            for(String s : e.messages) {
                System.out.println(s);
            }
            return;
        }
        assertEquals(3, res.size());
        assertTrue(res.get(0) instanceof KeyPressAction(
                int nativeModifiers, int nativeKeyCode
        ) && nativeKeyCode == KeyCodeLookup.getKeyCode("Z") && nativeModifiers == NativeInputEvent.CTRL_MASK);
        assertTrue(res.get(1) instanceof PauseKeyInterval(int min, int max) && min == 0 && max == 0);
        assertTrue(res.get(2) instanceof KeyPressAction(
                int nativeModifiers, int nativeKeyCode
        ) && nativeKeyCode == KeyCodeLookup.getKeyCode("F1") && nativeModifiers == (NativeInputEvent.ALT_MASK | NativeInputEvent.SHIFT_MASK));
    }

    @Test
    void escapedOperatorsRemainLiteral() throws ParseException {
        List<SequenceElement> res = DSLParser.parse("\\> > \\} > \\]");
        assertEquals(5, res.size());
        assertTrue(res.get(0) instanceof KeyTypedAction(char c) && c == '>');
        assertTrue(res.get(2) instanceof KeyTypedAction(char c) && c == '}');
        assertTrue(res.get(4) instanceof KeyTypedAction(char c) && c == ']');
    }
}
