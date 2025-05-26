package com.example.core.sequence;

import com.example.core.events.InputEvent;
import com.example.core.events.KeyPressEvent;
import com.example.core.events.KeyReleaseEvent;
import com.example.core.events.KeyTypedEvent;

import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class InputSequence {
    private final List<SequenceElement> steps;

    /*
     * @Precondition: Call parse to make sure that this is valid.
     *
     * try {
     *   var steps = DSLParser.parse(raw)
     *   x = new InputSequence(steps)
     * } catch ParserError e {
     *    for (var c: corrections)
     * }
     */
    public InputSequence(List<SequenceElement> steps) {
        this.steps = steps;
    }

    public String toString() {
        return steps.stream().map(SequenceElement::toString).collect(Collectors.joining());
    }

    public boolean matches(Deque<InputEvent> buf) {
        if (buf.size() < steps.size()) return false;
        InputEvent[] arr = buf.toArray(new InputEvent[0]);
        int n = arr.length;

        int j = 0;
        long prevTime = 0, earliestTime = 0, latestTime = Long.MAX_VALUE;
        int minKeys = 0, maxKeys = -1, currKeys = 0;

        for (int i = 0; i < n && j < steps.size(); i++) {
            var step = steps.get(j);

            switch (step) {
                case KeyPressAction(int nativeModifiers, int nativeKeyCode) -> {
                    for (int k = i; k < n; k++) {
                        if ((maxKeys >= 0 && currKeys > maxKeys) || arr[k].timestamp() > latestTime) return false;
                        if (k > 0 && arr[k] instanceof KeyPressEvent ek
                                && arr[k - 1] instanceof KeyPressEvent pk
                                && ek.keyCode() != pk.keyCode()) {
                            currKeys++;
                        }
                        if (currKeys < minKeys || arr[k].timestamp() < earliestTime) continue;
                        if (arr[k] instanceof KeyPressEvent(int keyCode, int modifiers, long timestamp)
                                && keyCode == nativeKeyCode
                                && (modifiers & nativeModifiers) == nativeModifiers) {
                            i = k;
                            j++;
                            prevTime = timestamp;
                            earliestTime = 0;
                            latestTime = Long.MAX_VALUE;
                            minKeys = 0;
                            maxKeys = -1;
                            currKeys = 0;
                            break;
                        }
                    }
                }
                case KeyReleaseAction(int nativeModifiers, int nativeKeyCode) -> {
                    for (int k = i; k < n; k++) {
                        if ((maxKeys >= 0 && currKeys > maxKeys) || arr[k].timestamp() > latestTime) return false;
                        if (k > 0 && arr[k] instanceof KeyReleaseEvent ek
                                && arr[k - 1] instanceof KeyReleaseEvent pk
                                && ek.keyCode() != pk.keyCode()) {
                            currKeys++;
                        }
                        if (currKeys < minKeys || arr[k].timestamp() < earliestTime) continue;
                        if (arr[k] instanceof KeyReleaseEvent(int keyCode, int modifiers, long timestamp)
                                && keyCode == nativeKeyCode
                                && (modifiers & nativeModifiers) == nativeModifiers) {
                            i = k;
                            j++;
                            prevTime = timestamp;
                            earliestTime = 0;
                            latestTime = Long.MAX_VALUE;
                            minKeys = 0;
                            maxKeys = -1;
                            currKeys = 0;
                            break;
                        }
                    }
                }
                case KeyTypedAction(char character) -> {
                    for (int k = i; k < n; k++) {
                        if (arr[k].timestamp() > latestTime) return false;
                        if (arr[k] instanceof KeyTypedEvent kt && kt.keyChar() == character) {
                            i = k;
                            j++;
                            prevTime = kt.timestamp();
                            earliestTime = 0;
                            latestTime = Long.MAX_VALUE;
                            minKeys = 0;
                            maxKeys = -1;
                            currKeys = 0;
                            break;
                        }
                    }
                }
                case PauseKeyInterval(int min, int max) -> {
                    minKeys = min;
                    maxKeys = max;
                    j++;
                    i--;
                }
                case PauseTimeInterval(int min, int max) -> {
                    earliestTime = prevTime + min;
                    latestTime = prevTime + max;
                    j++;
                    i--;
                }
                case null, default -> throw new IllegalStateException("Unexpected step type");
            }
        }
        return j == steps.size();
    }
}
