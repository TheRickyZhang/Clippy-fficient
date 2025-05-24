package com.example.agent.samplers;

import com.example.agent.utils.MouseWheelSamplerReturn;

public class MouseWheelSampler {
    private final IntervalTracker tracker;

    private int SCROLL_THRESHOLD;
    private int prevX = 0, prevY = 0;
    private int cumScroll = 0;

    public MouseWheelSampler() {
        this(50, 100, 1000, 2);
    }
    public MouseWheelSampler(long gap, long mn, long mx, int scroll_threshold) {
        tracker = new IntervalTracker(gap, mn, mx);
        SCROLL_THRESHOLD = scroll_threshold;
    }
    public void setScrollThreshold(int s) {
        SCROLL_THRESHOLD = s;
    }

    public MouseWheelSamplerReturn report(int scroll, int x, int y) {
        cumScroll += scroll;
        Long delta = tracker.tick();
        MouseWheelSamplerReturn r = null;
        if (delta != null && Math.abs(cumScroll) >= SCROLL_THRESHOLD) {
            r = new MouseWheelSamplerReturn(prevX, prevY, delta, cumScroll);
            prevX = x; prevY = y;
            cumScroll = 0;
        }
        return r;
    }
}