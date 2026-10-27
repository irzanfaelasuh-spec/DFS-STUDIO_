package com.dfsstudio.dfsoptimization.hud;

/**
 * Measures frame times from timestamps supplied by the caller. It only does a few
 * additions per frame and publishes a new snapshot roughly every half second.
 * Not thread safe: call it from the render thread only.
 */
public final class FrameSampler {
    /** Shared instance used by the HUD and the diagnostics screen. */
    public static final FrameSampler SHARED = new FrameSampler();

    private static final long WINDOW_NANOS = 500_000_000L;
    /** Two calls closer than this are treated as the same frame (HUD plus a screen). */
    private static final long MIN_FRAME_NANOS = 200_000L;
    /** A longer gap means the game was paused or the HUD hidden, so the window restarts. */
    private static final long MAX_GAP_NANOS = 2_000_000_000L;

    public record Snapshot(boolean valid, int fps, double avgFrameMs, double worstFrameMs) {
        public static final Snapshot EMPTY = new Snapshot(false, 0, 0.0, 0.0);
    }

    private long lastFrame;
    private int frames;
    private long sumNanos;
    private long worstNanos;
    private Snapshot snapshot = Snapshot.EMPTY;

    public void onFrame(long nowNanos) {
        if (lastFrame != 0) {
            long dt = nowNanos - lastFrame;
            if (dt < MIN_FRAME_NANOS) {
                return;
            }
            if (dt > MAX_GAP_NANOS) {
                resetWindow();
            } else {
                frames++;
                sumNanos += dt;
                worstNanos = Math.max(worstNanos, dt);
            }
        }
        lastFrame = nowNanos;
        if (sumNanos >= WINDOW_NANOS && frames > 0) {
            double avgNanos = (double) sumNanos / frames;
            snapshot = new Snapshot(true, (int) Math.round(1_000_000_000.0 / avgNanos),
                    avgNanos / 1_000_000.0, worstNanos / 1_000_000.0);
            resetWindow();
        }
    }

    public Snapshot snapshot() {
        return snapshot;
    }

    private void resetWindow() {
        frames = 0;
        sumNanos = 0;
        worstNanos = 0;
    }
}
