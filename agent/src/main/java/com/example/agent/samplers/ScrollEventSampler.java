package com.example.agent.samplers;

import com.example.core.utils.ScrollDelta;

/*
 * Determines if a continuous event has been triggering over the last INTERVAL_MS by polling every POLL_INTERVAL_MS
 *
 * All times are in MS
 */
public class ScrollEventSampler {
    private long INTERVAL_MS;    // how frequently we send back event responses
    private final long GAP_MS;         // How long until we consider a movement abandoned
    private final int SCROLL_THRESHOLD;       // The cumulative scroll must be >= threshold to return

    // Refresh on "volatile" keyword - synchronizes accesses for concurrent reads
    private volatile long prevRawMs;   // When we last received any event
    private volatile long prevEmitMs;  // When we last received an event with no event before it

    private volatile int prevX = 0;
    private volatile int prevY = 0;
    private int cumScroll = 0;             // The total scroll units in the GAP_MS window

    public long getINTERVAL_MS() {
        return INTERVAL_MS;
    }
    public void setINTERVAL_MS(long interval_ms) {
        INTERVAL_MS = interval_ms;
    }

    // TODO: Is 40 a good amount?
    public ScrollEventSampler() {
        this(500, 50, 40);
    }
    public ScrollEventSampler(long interval_ms, long gap_ms, int scroll_threshold) {
        INTERVAL_MS = interval_ms;
        GAP_MS = gap_ms;
        SCROLL_THRESHOLD = scroll_threshold;
        long nowMs = System.nanoTime() / 1_000_000;
        this.prevRawMs = nowMs;
        this.prevEmitMs = nowMs;
    }

    public ScrollDelta report(int scroll, int x, int y) {
        long nowMs = System.nanoTime() / 1_000_000;
        long gapMs = nowMs - prevRawMs;
        prevRawMs = nowMs;
        cumScroll += scroll;

        // It has been too long since previous detection
        if (gapMs > GAP_MS) {
            prevX = x;
            prevY = y;
            cumScroll = 0;
            prevEmitMs = nowMs;
            return null;
        }
        // Make sure to handle the edge case so that cumScroll resets if we have cleared INTERVAL_MS but haven't cleared SCROLL_THRESHOL
        if (nowMs - prevEmitMs >= INTERVAL_MS) {
            if (Math.abs(cumScroll) >= SCROLL_THRESHOLD) {
                ScrollDelta out = new ScrollDelta(prevX, prevY, cumScroll);
                prevEmitMs = nowMs;
                prevX = x; prevY = y;
                cumScroll = 0;
                return out;
            }
            prevEmitMs = nowMs;
            prevX = x; prevY = y;
            cumScroll = 0;
        }
        return null;
    }
}
