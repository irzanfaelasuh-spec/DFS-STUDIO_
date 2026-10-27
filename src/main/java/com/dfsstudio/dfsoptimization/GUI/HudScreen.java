package com.dfsstudio.dfsoptimization.gui;

import com.dfsstudio.dfsoptimization.config.DfsConfig;
import net.minecraft.client.gui.screens.Screen;

final class HudScreen extends DfsScreen {
    HudScreen(Screen parent, DfsSession session) {
        super("HUD / Monitoring", parent, session);
    }

    @Override
    protected void buildContent() {
        DfsConfig c = session.pendingConfig;
        addToggle("Show Monitor", "Shows a small panel on screen. It only displays numbers and does not "
                        + "make the game faster. When off, the mod does no HUD work at all.",
                () -> c.hudEnabled, value -> c.hudEnabled = value);
        addToggle("Show FPS", "Frames per second, averaged over about half a second.",
                () -> c.hudShowFps, value -> c.hudShowFps = value);
        addToggle("Show Frame Time", "Average and worst frame time in milliseconds in the same window.",
                () -> c.hudShowFrameTime, value -> c.hudShowFrameTime = value);
        addToggle("Show Memory", "Java heap memory used and the maximum the game may use.",
                () -> c.hudShowMemory, value -> c.hudShowMemory = value);
        addCycle("Position", "Which screen corner the panel uses. If it overlaps the F3 debug screen, pick another corner.",
                () -> Integer.toString(c.hudCorner),
                value -> c.hudCorner = Integer.parseInt(value),
                current -> Integer.toString((Integer.parseInt(current) + 1) % DfsConfig.CORNER_LABELS.length),
                current -> DfsConfig.cornerLabel(Integer.parseInt(current)));
    }

    @Override
    protected void resetCategory() {
        DfsConfig d = DfsConfig.defaults();
        DfsConfig c = session.pendingConfig;
        c.hudEnabled = d.hudEnabled;
        c.hudShowFps = d.hudShowFps;
        c.hudShowFrameTime = d.hudShowFrameTime;
        c.hudShowMemory = d.hudShowMemory;
        c.hudCorner = d.hudCorner;
    }
}
