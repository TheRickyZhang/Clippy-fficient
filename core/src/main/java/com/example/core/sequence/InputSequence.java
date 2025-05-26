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

    private long earliestTime;
    private long latestTime;
    private int minKeys, maxKeys, currKeys;

    private void resetState() {
        earliestTime = 0;
        latestTime = Long.MAX_VALUE;
        minKeys = 0;
        maxKeys = Integer.MAX_VALUE;
        currKeys = 0;
    }
    // --------------------------------------------------------------

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

    @Override
    public String toString() {
        return steps.stream()
                .map(SequenceElement::toString)
                .collect(Collectors.joining());
    }

    public boolean matches(Deque<InputEvent> buf) {
        InputEvent[] arr = buf.toArray(new InputEvent[0]);
        int n = arr.length;
        if (n == 0) return steps.isEmpty();

//        System.out.println("Calling matches on " + Arrays.toString(arr));

        // Immediately check for 1st match to not have one pattern trigger multiple actions
        var first = steps.getFirst();
        if (!matchesOne(arr[0], first)) return false;
        int j = 1;
        long prevTime = 0;
        resetState();


        for (int i = 1; i < n && j < steps.size(); i++) {
            var step = steps.get(j);

            switch (step) {
                case KeyPressAction(int nativeModifiers, int nativeKeyCode) -> {
                    for (int k = i; k < n; k++) {
                        if ((maxKeys >= 0 && currKeys > maxKeys) || arr[k].timestamp() > latestTime)
                            return false;
                        if (currKeys < minKeys || arr[k].timestamp() < earliestTime)
                            continue;
                        if (arr[k] instanceof KeyPressEvent(int keyCode, int modifiers, long timestamp)
                                && keyCode == nativeKeyCode
                                && (modifiers & nativeModifiers) == nativeModifiers) {
                            i = k;
                            j++;
                            prevTime = timestamp;
                            resetState();
                            break;
                        }
                    }
                }
                case KeyReleaseAction(int nativeModifiers, int nativeKeyCode) -> {
                    for (int k = i; k < n; k++) {
                        if ((maxKeys >= 0 && currKeys > maxKeys) || arr[k].timestamp() > latestTime)
                            return false;
                        if (currKeys < minKeys || arr[k].timestamp() < earliestTime)
                            continue;
                        if (arr[k] instanceof KeyReleaseEvent(int keyCode, int modifiers, long timestamp)
                                && keyCode == nativeKeyCode
                                && (modifiers & nativeModifiers) == nativeModifiers) {
                            i = k;
                            j++;
                            prevTime = timestamp;
                            resetState();
                            break;
                        }
                    }
                }
                case KeyTypedAction(char character) -> {
                    currKeys = 0;  // reset noise count
                    for (int k = i; k < n; k++) {
                        InputEvent ev = arr[k];
                        long ts = ev.timestamp();

                        if (ts > latestTime)
                            return false;
                        if (ev instanceof KeyTypedEvent ktNoise && ktNoise.keyChar() != character) {
                            if (++currKeys > maxKeys)
                                return false;
                            continue;
                        }
                        if (ev instanceof KeyTypedEvent) {
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
                    earliestTime = prevTime + min;
                    latestTime = prevTime + max;
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
