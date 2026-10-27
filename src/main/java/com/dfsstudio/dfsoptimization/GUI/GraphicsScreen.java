package com.dfsstudio.dfsoptimization.gui;

import com.dfsstudio.dfsoptimization.config.VideoSettings;
import net.minecraft.client.gui.screens.Screen;

final class GraphicsScreen extends DfsScreen {
    GraphicsScreen(Screen parent, DfsSession session) {
        super("Graphics", parent, session);
    }

    @Override
    protected void buildContent() {
        VideoSettings v = session.pendingVideo;
        addSlider("Max Framerate", "Limits how many frames per second the game renders. Unlimited can raise "
                        + "power use and heat; a limit near your monitor's refresh rate saves work.",
                VideoSettings.MIN_MAX_FPS, VideoSettings.UNLIMITED_FPS, 10,
                () -> v.maxFps, value -> v.maxFps = value, VideoSettings::maxFpsLabel);
        addToggle("VSync", "Syncs frames to your monitor. It removes tearing but can add input delay.",
                () -> v.vsync, value -> v.vsync = value);
        addSlider("Biome Blend", "Smooths grass and water colors between biomes. Higher values cost more "
                        + "when chunks are built. 0 is the cheapest.",
                VideoSettings.MIN_BIOME_BLEND, VideoSettings.MAX_BIOME_BLEND, 1,
                () -> v.biomeBlend, value -> v.biomeBlend = value, value -> Integer.toString(value));
    }

    @Override
    protected void resetCategory() {
        VideoSettings d = VideoSettings.defaults();
        VideoSettings v = session.pendingVideo;
        v.maxFps = d.maxFps;
        v.vsync = d.vsync;
        v.biomeBlend = d.biomeBlend;
    }
}
