package com.example.core.sequence;

import com.example.core.events.InputEvent;
import com.example.core.events.KeyPressEvent;
import com.example.core.events.KeyReleaseEvent;
import com.example.core.events.KeyTypedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class InputSequence {
    private final List<SequenceElement> steps;

    private long earliestTime;
    private long latestTime;
    private int minKeys, maxKeys, currKeys;

    private void resetState() {
        earliestTime = 0;
        latestTime   = Long.MAX_VALUE;
        minKeys      = 0;
        maxKeys      = Integer.MAX_VALUE;
        currKeys     = 0;
    }

    public InputSequence(List<SequenceElement> steps) {
        // reverse the pattern so rev.get(0) is the last original step
        var rev = new ArrayList<>(steps);
        Collections.reverse(rev);
        this.steps = rev;
    }

    @Override
    public String toString() {
        return steps.stream()
                .map(SequenceElement::toString)
                .collect(Collectors.joining());
    }

    public boolean matches(Deque<InputEvent> buf) {
        // assume buf is newest→oldest
        InputEvent[] arr = buf.toArray(new InputEvent[0]);
        int n = arr.length;
        if (n == 0) return steps.isEmpty();

        // match the reversed-first step on the most recent event
        if (!matchesOne(arr[0], steps.getFirst())) return false;
        int j = 1;
        long prevTime = arr[0].timestamp();
        resetState();

        // forward‐scan the rest of arr against reversed steps
        for (int i = 1; i < n && j < steps.size(); i++) {
            var step = steps.get(j);
            switch (step) {
                case KeyPressAction(int nativeModifiers, int nativeKeyCode) -> {
                    for (int k = i; k < n; k++) {
                        var e = arr[k];
                        long ts = e.timestamp();
                        if ((maxKeys >= 0 && currKeys > maxKeys) || ts > latestTime)
                            return false;
                        if (currKeys < minKeys || ts < earliestTime)
                            continue;
                        if (e instanceof KeyPressEvent(int code, int mods, long t)
                                && code == nativeKeyCode
                                && (mods & nativeModifiers) == nativeModifiers) {
                            i = k;
                            j++;
                            prevTime = t;
                            resetState();
                            break;
                        }
                    }
                }
                case KeyReleaseAction(int nativeModifiers, int nativeKeyCode) -> {
                    for (int k = i; k < n; k++) {
                        var e = arr[k];
                        long ts = e.timestamp();
                        if ((maxKeys >= 0 && currKeys > maxKeys) || ts > latestTime)
                            return false;
                        if (currKeys < minKeys || ts < earliestTime)
                            continue;
                        if (e instanceof KeyReleaseEvent(int code, int mods, long t)
                                && code == nativeKeyCode
                                && (mods & nativeModifiers) == nativeModifiers) {
                            i = k;
                            j++;
                            prevTime = t;
                            resetState();
                            break;
                        }
                    }
                }
                case KeyTypedAction(char character) -> {
                    currKeys = 0;  // reset noise count
                    for (int k = i; k < n; k++) {
                        var e = arr[k];
                        long ts = e.timestamp();
                        if (ts > latestTime)
                            return false;
                        if (e instanceof KeyTypedEvent kt && kt.keyChar() != character) {
                            if (++currKeys > maxKeys)
                                return false;
                            continue;
                        }
                        if (e instanceof KeyTypedEvent) {
                            if (ts < earliestTime)
                                continue;
                            i = k;
                            j++;
                            prevTime = ts;
                            resetState();
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
                    // on reversed pattern, flip the time window:
                    // next match must fall in [prevTime-max, prevTime-min]
                    latestTime   = prevTime - min;
                    earliestTime = prevTime - max;
                    j++;
                    i--;
                }
                default -> throw new IllegalStateException("Unexpected step type");
            }
        }

        return j == steps.size();
    }

    private boolean matchesOne(InputEvent ev, SequenceElement step) {
        return switch (step) {
            case KeyPressAction(int mods, int key) -> ev instanceof KeyPressEvent kpe
                    && kpe.keyCode() == key
                    && (kpe.modifiers() & mods) == mods;
            case KeyReleaseAction(int mods, int key) -> ev instanceof KeyReleaseEvent kre
                    && kre.keyCode() == key
                    && (kre.modifiers() & mods) == mods;
            case KeyTypedAction(char c) -> ev instanceof KeyTypedEvent kte
                    && kte.keyChar() == c;
            default -> false;
        };
    }
}
