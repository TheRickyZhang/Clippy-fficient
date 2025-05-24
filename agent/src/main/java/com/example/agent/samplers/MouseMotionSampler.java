package com.example.agent.samplers;

import com.example.agent.utils.MouseMotionSamplerReturn;

public class MouseMotionSampler {
    private final IntervalTracker tracker;
    private int prevX = 0, prevY = 0;

    public MouseMotionSampler() {
        this(50, 100, 1000);
    }
    public MouseMotionSampler(long gap, long mn, long mx) {
        tracker = new IntervalTracker(gap, mn, mx);
    }

    public MouseMotionSamplerReturn report(int x, int y) {
        Long delta = tracker.tick();
        MouseMotionSamplerReturn r = null;
        if (delta != null) {
            r = new MouseMotionSamplerReturn(prevX, prevY, delta);
            prevX = x; prevY = y;
        }
        return r;
    }
}
