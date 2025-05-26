package com.example.core.sequence;

import com.example.core.events.InputEvent;
import com.example.core.events.KeyPressEvent;
import com.example.core.events.KeyReleaseEvent;
import com.example.core.events.KeyTypedEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InputSequenceMatchTest {
    // Helper to build KeyPressEvent
    private KeyPressEvent kp(char keyChar, long ts) {
        int code = KeyCodeLookup.getKeyCode(String.valueOf(keyChar));
        return new KeyPressEvent(code, 0, ts);
    }

    private KeyReleaseEvent kr(char keyChar, long ts) {
        int code = KeyCodeLookup.getKeyCode(String.valueOf(keyChar));
        return new KeyReleaseEvent(code, 0, ts);
    }

    private KeyTypedEvent kt(char keyChar, long ts) {
        return new KeyTypedEvent(keyChar, 0, ts);
    }

    @Test
    void matchesSimpleTyped() throws ParseException {
        InputSequence seq = new InputSequence(DSLParser.parse("A B"));
        Deque<InputEvent> buf = new ArrayDeque<>();
        buf.add(kt('A', 100));
        buf.add(kt('B', 200));
        assertTrue(seq.matches(buf));
    }

    @Test
    void doesNotMatchOutOfOrder() {
        try {
            InputSequence seq = new InputSequence(DSLParser.parse("A B"));
            Deque<InputEvent> buf = new ArrayDeque<>(List.of(
                    kt('B', 100),
                    kt('A', 200)
            ));
            assertFalse(seq.matches(buf));
        } catch (ParseException e) {
            System.out.println(e.toString());
            fail();
        }
    }

    @Test
    void matchesWithNoiseWithinKeyInterval() throws ParseException {
        // sequence: A > B means A then zero-interval alias then B
        InputSequence seq = new InputSequence(DSLParser.parse("A > B"));
        Deque<InputEvent> buf = new ArrayDeque<>(List.of(
                kt('A', 100),
                kp('T', 100),
                kt('T', 150),
                kt('B', 200)
        ));
        // default maxKeys==0 thus noise prevents match
        assertFalse(seq.matches(buf));
    }

    @Test
    void matchesWithAllowedKeyInterval() throws ParseException {
        InputSequence seq = new InputSequence(DSLParser.parse("A {2} B"));
        Deque<InputEvent> buf = new ArrayDeque<>(List.of(
                kt('A', 100),
                kt('X', 150),
                kt('Y', 160),
                kt('B', 200)
        ));
        assertTrue(seq.matches(buf));
    }

    @Test
    void doesNotMatchIfTooManyKeys() throws ParseException {
        InputSequence seq = new InputSequence(DSLParser.parse("A {1} B"));
        Deque<InputEvent> buf = new ArrayDeque<>(List.of(
                kt('A', 100),
                kt('X', 150),
                kt('Y', 160),
                kt('Z', 160),
                kt('B', 200)
        ));
        assertFalse(seq.matches(buf));
    }

    @Test
    void matchesWithTimeInterval() throws ParseException {
        // sequence: A [50,200] B allows time window
        InputSequence seq = new InputSequence(DSLParser.parse("A [50,200] B"));
        Deque<InputEvent> buf = new ArrayDeque<>(List.of(
                kt('A', 100),
                kt('B', 250)
        ));
        assertTrue(seq.matches(buf));
    }

    @Test
    void doesNotMatchIfTooFast() throws ParseException {
        InputSequence seq = new InputSequence(DSLParser.parse("A [50,200] B"));
        Deque<InputEvent> buf = new ArrayDeque<>(List.of(
                kt('A', 100),
                kt('B', 120)
        ));
        assertFalse(seq.matches(buf));
    }

    @Test
    void doesNotMatchIfTooSlow() throws ParseException {
        InputSequence seq = new InputSequence(DSLParser.parse("A [50,200] B"));
        Deque<InputEvent> buf = new ArrayDeque<>(List.of(
                kt('A', 100),
                kt('B', 350)
        ));
        assertFalse(seq.matches(buf));
    }
}
