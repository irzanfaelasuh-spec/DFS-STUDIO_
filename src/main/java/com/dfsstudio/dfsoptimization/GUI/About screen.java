package com.dfsstudio.dfsoptimization.gui;

import com.dfsstudio.dfsoptimization.DfsOptimizationClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

final class AboutScreen extends DfsScreen {
    private static final String DESCRIPTION =
            "A configurable client-side performance mod for Minecraft: Java Edition. DFS Optimization "
                    + "provides practical graphics and rendering controls designed to reduce unnecessary "
                    + "visual workload. Actual performance gains depend on your hardware, settings, world, "
                    + "and other installed mods.";
    private static final String FEATURES =
            "Features: Low, Balanced and High presets, particles, clouds, entity shadows, render and "
                    + "simulation distance, biome blend, VSync, framerate limit, an optional FPS, frame time "
                    + "and memory monitor, and a small diagnostics page. Every option changes a real Minecraft "
                    + "setting or the monitor.";

    AboutScreen(Screen parent) {
        super("About", parent, null);
    }

    @Override
    protected boolean hasReset() {
        return false;
    }

    @Override
    protected void buildContent() {
        // Text only.
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        int y = 36;
        graphics.drawCenteredString(this.font, "DFS Optimization " + DfsOptimizationClient.version(),
                this.width / 2, y, 0xFFFFFFFF);
        y += 12;
        graphics.drawCenteredString(this.font, "Creator: DFSstudio", this.width / 2, y, 0xFFFFFFFF);
        y += 18;
        for (String line : wrap(DESCRIPTION, this.width - 40)) {
            graphics.drawCenteredString(this.font, line, this.width / 2, y, 0xFFAAAAAA);
            y += 11;
        }
        y += 8;
        for (String line : wrap(FEATURES, this.width - 40)) {
            graphics.drawCenteredString(this.font, line, this.width / 2, y, 0xFFAAAAAA);
            y += 11;
        }
    }

    private List<String> wrap(String text, int maxWidth) {
        List<String> out = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : text.split(" ")) {
            String candidate = current.length() == 0 ? word : current + " " + word;
            if (this.font.width(candidate) > maxWidth && current.length() > 0) {
                out.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }
        if (current.length() > 0) {
            out.add(current.toString());
        }
        return out;
    }
}
