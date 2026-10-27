package com.dfsstudio.dfsoptimization.gui;

import com.dfsstudio.dfsoptimization.config.VideoSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

final class PerformanceScreen extends DfsScreen {
    PerformanceScreen(Screen parent, DfsSession session) {
        super("Performance", parent, session);
    }

    @Override
    protected void buildContent() {
        VideoSettings v = session.pendingVideo;
        addSlider("Render Distance", "How many chunks are drawn around you. This is usually the biggest "
                        + "lever for FPS. Singleplayer worlds and servers may limit it.",
                VideoSettings.MIN_RENDER_DISTANCE, VideoSettings.MAX_RENDER_DISTANCE, 1,
                () -> v.renderDistance, value -> v.renderDistance = value, value -> value + " chunks");
        addSlider("Simulation Distance", "How far away mobs, crops and redstone are updated. Lower values "
                        + "reduce game logic work. It never goes above what the world allows.",
                VideoSettings.MIN_SIMULATION_DISTANCE, VideoSettings.MAX_SIMULATION_DISTANCE, 1,
                () -> v.simulationDistance, value -> v.simulationDistance = value, value -> value + " chunks");
        addAction("Open Diagnostics",
                "Shows live FPS, frame time and memory with a few simple hints. This is an estimate, not a profiler.",
                () -> Minecraft.getInstance().setScreen(new DiagnosticsScreen(this)));
    }

    @Override
    protected void resetCategory() {
        VideoSettings d = VideoSettings.defaults();
        VideoSettings v = session.pendingVideo;
        v.renderDistance = d.renderDistance;
        v.simulationDistance = d.simulationDistance;
    }
}
