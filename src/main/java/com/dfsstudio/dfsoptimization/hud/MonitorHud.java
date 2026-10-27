package com.dfsstudio.dfsoptimization.hud;

import com.dfsstudio.dfsoptimization.DfsOptimizationClient;
import com.dfsstudio.dfsoptimization.config.DfsConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/**
 * Optional on-screen monitor. It is purely informational: it never changes game settings.
 * When it is disabled in the config, render() returns immediately and does no work.
 */
public final class MonitorHud {
    private static final int PADDING = 3;
    private static final int LINE_HEIGHT = 10;
    private static final int MARGIN = 4;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int BACKGROUND_COLOR = 0x90000000;

    private static long lastMemoryCheck;
    private static long usedMb;
    private static long maxMb;

    private MonitorHud() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        DfsConfig cfg = DfsOptimizationClient.config();
        if (!cfg.hudEnabled) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) {
            return;
        }

        long now = System.nanoTime();
        FrameSampler.SHARED.onFrame(now);
        updateMemory(now);

        List<String> lines = buildLines(cfg, FrameSampler.SHARED.snapshot());
        if (lines.isEmpty()) {
            return;
        }

        int textWidth = 0;
        for (String line : lines) {
            textWidth = Math.max(textWidth, mc.font.width(line));
        }
        int boxWidth = textWidth + PADDING * 2;
        int boxHeight = lines.size() * LINE_HEIGHT + PADDING * 2 - 1;

        boolean right = cfg.hudCorner == 1 || cfg.hudCorner == 3;
        boolean bottom = cfg.hudCorner == 2 || cfg.hudCorner == 3;
        int x = right ? graphics.guiWidth() - boxWidth - MARGIN : MARGIN;
        int y = bottom ? graphics.guiHeight() - boxHeight - MARGIN : MARGIN;

        graphics.fill(x, y, x + boxWidth, y + boxHeight, BACKGROUND_COLOR);
        int textY = y + PADDING;
        for (String line : lines) {
            graphics.drawString(mc.font, line, x + PADDING, textY, TEXT_COLOR);
            textY += LINE_HEIGHT;
        }
    }

    private static List<String> buildLines(DfsConfig cfg, FrameSampler.Snapshot snap) {
        List<String> lines = new ArrayList<>(3);
        if (cfg.hudShowFps) {
            lines.add(snap.valid() ? "FPS: " + snap.fps() : "FPS: measuring...");
        }
        if (cfg.hudShowFrameTime) {
            lines.add(snap.valid()
                    ? String.format("Frame: %.1f ms (worst %.1f)", snap.avgFrameMs(), snap.worstFrameMs())
                    : "Frame: measuring...");
        }
        if (cfg.hudShowMemory) {
            lines.add("Memory: " + usedMb + " / " + maxMb + " MB");
        }
        return lines;
    }

    /** Memory numbers are refreshed twice per second, not every frame. */
    private static void updateMemory(long nowNanos) {
        if (lastMemoryCheck != 0 && nowNanos - lastMemoryCheck < 500_000_000L) {
            return;
        }
        lastMemoryCheck = nowNanos;
        Runtime rt = Runtime.getRuntime();
        usedMb = (rt.totalMemory() - rt.freeMemory()) / (1024L * 1024L);
        maxMb = rt.maxMemory() / (1024L * 1024L);
    }

    public static long usedMemoryMb() {
        updateMemory(System.nanoTime());
        return usedMb;
    }

    public static long maxMemoryMb() {
        updateMemory(System.nanoTime());
        return maxMb;
    }
}
