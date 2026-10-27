package com.dfsstudio.dfsoptimization.hud;

import java.util.ArrayList;
import java.util.List;

/** Very small rule based hints. These are estimates, not a replacement for a profiler. */
public final class Diagnostics {
    private Diagnostics() {
    }

    public static List<String> hints(double avgFrameMs, double worstFrameMs,
                                     long usedMb, long maxMb, int renderDistance) {
        List<String> out = new ArrayList<>();
        if (maxMb > 0 && usedMb * 100 / maxMb >= 85) {
            out.add("Memory use is high (" + (usedMb * 100 / maxMb)
                    + "%). Lower the render distance or give the game more RAM in your launcher.");
        }
        if (avgFrameMs > 25.0 && renderDistance > 12) {
            out.add("Frame times are high. Lowering the render distance is usually the most effective change.");
        }
        if (avgFrameMs > 0.0 && worstFrameMs > avgFrameMs * 4.0 && worstFrameMs > 40.0) {
            out.add("Short frame spikes were measured. Chunk loading, garbage collection or other mods can cause them.");
        }
        if (out.isEmpty()) {
            out.add("No obvious problem in this short sample.");
        }
        return out;
    }
}
