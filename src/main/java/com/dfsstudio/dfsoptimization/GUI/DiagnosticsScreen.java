package com.dfsstudio.dfsoptimization.gui;

import com.dfsstudio.dfsoptimization.hud.Diagnostics;
import com.dfsstudio.dfsoptimization.hud.FrameSampler;
import com.dfsstudio.dfsoptimization.hud.MonitorHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

/** Read-only live numbers. It shows the values the game uses right now, not pending edits. */
final class DiagnosticsScreen extends DfsScreen {
    DiagnosticsScreen(Screen parent) {
        super("Diagnostics", parent, null);
    }

    @Override
    protected boolean hasReset() {
        return false;
    }

    @Override
    protected void buildContent() {
        // No widgets: this page only shows numbers.
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        FrameSampler.SHARED.onFrame(System.nanoTime());
        super.render(graphics, mouseX, mouseY, delta);

        Minecraft mc = Minecraft.getInstance();
        FrameSampler.Snapshot snap = FrameSampler.SHARED.snapshot();
        long used = MonitorHud.usedMemoryMb();
        long max = MonitorHud.maxMemoryMb();
        int renderDistance = mc.options.renderDistance().get();

        List<String> lines = new ArrayList<>();
        if (snap.valid()) {
            lines.add(String.format("FPS: %d   Frame time: %.1f ms   Worst: %.1f ms",
                    snap.fps(), snap.avgFrameMs(), snap.worstFrameMs()));
        } else {
            lines.add("FPS: measuring... (wait half a second)");
        }
        lines.add("Memory: " + used + " / " + max + " MB");
        lines.add("Render distance: " + renderDistance + " chunks   Simulation distance: "
                + mc.options.simulationDistance().get() + " chunks");
        lines.add("Java " + System.getProperty("java.version"));
        lines.add("");
        if (snap.valid()) {
            lines.addAll(Diagnostics.hints(snap.avgFrameMs(), snap.worstFrameMs(), used, max, renderDistance));
        }
        lines.add("");
        lines.add("These hints are estimates from a short sample. They are not a full profiler.");

        int y = 40;
        for (String line : lines) {
            for (String part : wrap(line, this.width - 40)) {
                graphics.drawCenteredString(this.font, part, this.width / 2, y, 0xFFFFFFFF);
                y += 11;
            }
            if (line.isEmpty()) {
                y += 5;
            }
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
