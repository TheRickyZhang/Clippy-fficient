package com.example.agent.samplers;

import com.example.core.logging.LogService;
import java.util.logging.Logger;

/*
 * Tracks raw-event timestamps and only emits when:
 *   1) gap > GAP_MS AND timeSinceLastEmit >= MIN_INTERVAL_MS, or
 *   2) timeSinceLastEmit >= MAX_INTERVAL_MS
 *
 * On emit it returns the exact delta to use; otherwise null.
 */
public class IntervalTracker {
    static final Logger log = LogService.get().forClass(IntervalTracker.class);

    private long GAP_MS = 50;            // length of no activity to abandon session
    private long MIN_INTERVAL_MS = 100;   // minimum time until we can emit
    private long MAX_INTERVAL_MS = 1000;   // maximum time until we can emit

    private long prevRawMs;         // last raw event
    private long prevEmitMs;        // last emit

    public IntervalTracker(long gap, long mn, long mx) {
        log.info("IntervalTracker created!");
        // Set in reverse order to avoid timing exceptions
        setMaxInterval(mx);
        setMinInterval(mn);
        setGap(gap);

        long nowMs = System.nanoTime() / 1_000_000;
        prevRawMs  = nowMs;
        prevEmitMs = nowMs;
    }

    public void setMinInterval(long mn) throws IllegalArgumentException {
        MIN_INTERVAL_MS = mn;
        checkValues();
    }

    public void setMaxInterval(long mx) throws IllegalArgumentException {
        MAX_INTERVAL_MS = mx;
        checkValues();
    }

    public void setGap(long gap) throws IllegalArgumentException {
        GAP_MS = gap;
        checkValues();
    }

    private void checkValues() throws IllegalArgumentException {
        long buffer = 10;
        if (MIN_INTERVAL_MS < GAP_MS + buffer) {
            MIN_INTERVAL_MS = GAP_MS + buffer;
            throw new IllegalArgumentException(
                "MIN_INTERVAL_MS must be > GAP_MS (got GAP_MS=" + GAP_MS +
                ", MIN_INTERVAL_MS=" + MIN_INTERVAL_MS + ")");
        }
        if (MAX_INTERVAL_MS < MIN_INTERVAL_MS + buffer) {
            MAX_INTERVAL_MS = MIN_INTERVAL_MS + buffer;
            throw new IllegalArgumentException(
                "MAX_INTERVAL_MS must be > MIN_INTERVAL_MS (got MIN_INTERVAL_MS=" +
                MIN_INTERVAL_MS + ", MAX_INTERVAL_MS=" + MAX_INTERVAL_MS + ")");
        }
    }

    /**
     * Call on each raw event.  Returns the time‐delta that should be emitted,
     * or null if nothing should be emitted yet.
     */
    public Long tick() {
        long nowMs      = System.nanoTime() / 1_000_000;
        long gapMs      = nowMs - prevRawMs;
        long intervalMs = nowMs - prevEmitMs;
        prevRawMs       = nowMs;

        // Session abandoned?
        if (gapMs > GAP_MS) {
            // only emit if we've passed the MIN since the last emitting
            if (intervalMs >= MIN_INTERVAL_MS) {
                prevEmitMs = nowMs;
                return intervalMs;
            }
            prevEmitMs = nowMs;
            return null;
        }
        // Forced emit after MAX
        if (intervalMs >= MAX_INTERVAL_MS) {
            prevEmitMs += MAX_INTERVAL_MS;
            return MAX_INTERVAL_MS;
        }
        return null;
    }
}
