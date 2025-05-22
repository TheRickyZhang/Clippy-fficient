package com.example.agent.samplers;

import com.example.core.utils.Pair;

/*
 * Determines if a continuous event has been triggering over the last INTERVAL_MS by polling every POLL_INTERVAL_MS
 *
 * All times are in MS
 */
public class MouseEventSampler {
    private final long INTERVAL_MS;    // how frequently we send back event responses
    private final long GAP_MS;         // How long until we consider a movement abandoned

    // Refresh on "volatile" keyword - synchronizes accesses for concurrent reads
    private volatile long prevRawMs;   // When we last received any event
    private volatile long prevEmitMs;  // When we last received an event with no event before it

    private volatile int prevX = 0;
    private volatile int prevY = 0;


    public MouseEventSampler() {
        this(500, 50);
    }
    public MouseEventSampler(long interval_ms, long gap_ms) {
        INTERVAL_MS = interval_ms;
        GAP_MS = gap_ms;
        long nowMs = System.nanoTime() / 1_000_000;
        this.prevRawMs = nowMs;
        this.prevEmitMs = nowMs;
    }

    public Pair report(int x, int y) {
        long nowMs = System.nanoTime() / 1_000_000;
        long gapMs = nowMs - prevRawMs;
        prevRawMs = nowMs;

        // It has been too long since previous detection
        if (gapMs > GAP_MS) {
            prevX = x;
            prevY = y;
            prevEmitMs = nowMs;
            return null;
        }
        if (nowMs - prevEmitMs >= INTERVAL_MS) {
            prevEmitMs = nowMs;
            int rx = prevX, ry = prevY;
            prevX = x;
            prevY = y;
            return new Pair(rx, ry);
        }
        return null;
    }
}
