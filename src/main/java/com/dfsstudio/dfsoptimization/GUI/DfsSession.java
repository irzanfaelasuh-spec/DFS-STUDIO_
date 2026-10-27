package com.dfsstudio.dfsoptimization.gui;

import com.dfsstudio.dfsoptimization.DfsOptimizationClient;
import com.dfsstudio.dfsoptimization.config.DfsConfig;
import com.dfsstudio.dfsoptimization.config.Preset;
import com.dfsstudio.dfsoptimization.config.VideoSettings;
import com.dfsstudio.dfsoptimization.video.VanillaBridge;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * Holds the edits made in the DFS Optimization screens. Nothing reaches Minecraft or the
 * config file until apply() is called; discarding the session simply drops the copies.
 */
final class DfsSession {
    private final VideoSettings baselineVideo;
    final VideoSettings pendingVideo;
    private final DfsConfig baselineConfig;
    final DfsConfig pendingConfig;

    DfsSession(Minecraft mc) {
        this.baselineVideo = VanillaBridge.read(mc);
        this.pendingVideo = baselineVideo.copy();
        this.baselineConfig = DfsOptimizationClient.config().copy();
        this.pendingConfig = baselineConfig.copy();
    }

    boolean videoDirty() {
        return !baselineVideo.equals(pendingVideo);
    }

    boolean isDirty() {
        return videoDirty() || !baselineConfig.equals(pendingConfig);
    }

    List<String> describeVideoChanges() {
        return baselineVideo.diff(pendingVideo);
    }

    void apply(Minecraft mc) {
        if (videoDirty()) {
            VanillaBridge.apply(mc, pendingVideo);
        }
        pendingConfig.preset = Preset.detect(pendingVideo).name();
        DfsOptimizationClient.updateConfig(pendingConfig);
    }
}
