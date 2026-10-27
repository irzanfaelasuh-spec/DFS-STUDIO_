package com.dfsstudio.dfsoptimization.gui;

import com.dfsstudio.dfsoptimization.config.DfsConfig;
import com.dfsstudio.dfsoptimization.config.Preset;
import com.dfsstudio.dfsoptimization.config.VideoSettings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

final class GeneralScreen extends DfsScreen {
    GeneralScreen(Screen parent, DfsSession session) {
        super("General", parent, session);
    }

    @Override
    protected void buildContent() {
        addAction("Preset: " + Preset.detect(session.pendingVideo).label(),
                "Cycles Low, Balanced and High. A preset changes particles, clouds, entity shadows, "
                        + "render distance, simulation distance and biome blend. It does not touch the "
                        + "framerate limit or VSync. Nothing is applied until you press Apply on the main screen.",
                () -> {
                    Preset next = Preset.detect(session.pendingVideo).next();
                    next.applyTo(session.pendingVideo);
                    rebuildWidgets();
                });
        addAction("Load Minecraft Defaults",
                "Loads the default Minecraft values for every option managed by this mod. "
                        + "Nothing is applied until you press Apply on the main screen.",
                () -> {
                    session.pendingVideo.copyFrom(VideoSettings.defaults());
                    rebuildWidgets();
                });
    }

    @Override
    protected void resetCategory() {
        session.pendingVideo.copyFrom(VideoSettings.defaults());
        session.pendingConfig.copyFrom(DfsConfig.defaults());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        int y = contentBottomY() + 6;
        Preset current = Preset.detect(session.pendingVideo);
        graphics.drawCenteredString(this.font, "Current values match: " + current.label(), this.width / 2, y, 0xFFFFFFFF);
        y += 14;
        for (Preset p : new Preset[]{Preset.LOW, Preset.BALANCED, Preset.HIGH}) {
            graphics.drawCenteredString(this.font, p.label() + ": " + shortSummary(p), this.width / 2, y, 0xFFAAAAAA);
            y += 11;
        }
        graphics.drawCenteredString(this.font, "Reset Category here also resets the HUD settings.",
                this.width / 2, y + 4, 0xFFAAAAAA);
    }

    private static String shortSummary(Preset p) {
        VideoSettings s = new VideoSettings();
        p.applyTo(s);
        return "render " + s.renderDistance + ", simulation " + s.simulationDistance
                + ", particles " + VideoSettings.particleLabel(s.particles)
                + ", clouds " + VideoSettings.cloudLabel(s.clouds);
    }
}
