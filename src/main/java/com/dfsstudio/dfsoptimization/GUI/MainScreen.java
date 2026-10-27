package com.dfsstudio.dfsoptimization.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

/** Main DFS Optimization menu. Owns the edit session and the Apply / Cancel buttons. */
public final class MainScreen extends Screen {
    private static final int WIDGET_WIDTH = 150;
    private static final int WIDGET_HEIGHT = 20;

    private final Screen parent;
    private final DfsSession session;

    public MainScreen(Screen parent) {
        super(Component.literal("DFS Optimization"));
        this.parent = parent;
        this.session = new DfsSession(Minecraft.getInstance());
    }

    @Override
    protected void init() {
        addCategory(0, "General", "Presets and restoring Minecraft defaults.", () -> new GeneralScreen(this, session));
        addCategory(1, "Graphics", "Biome blend, VSync and the framerate limit.", () -> new GraphicsScreen(this, session));
        addCategory(2, "Particles & Effects", "Particles, clouds and entity shadows.", () -> new EffectsScreen(this, session));
        addCategory(3, "Performance", "Render distance, simulation distance and diagnostics.", () -> new PerformanceScreen(this, session));
        addCategory(4, "HUD / Monitoring", "Optional on-screen FPS, frame time and memory display.", () -> new HudScreen(this, session));
        addCategory(5, "About", "Version, creator and feature summary.", () -> new AboutScreen(this));

        int bottomY = this.height - 28;
        addRenderableWidget(Button.builder(Component.literal("Apply & Close"), b -> applyAndClose())
                .bounds(this.width / 2 - WIDGET_WIDTH - 5, bottomY, WIDGET_WIDTH, WIDGET_HEIGHT)
                .tooltip(Tooltip.create(Component.literal("Applies your changes to Minecraft, saves them and closes this menu.")))
                .build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> onClose())
                .bounds(this.width / 2 + 5, bottomY, WIDGET_WIDTH, WIDGET_HEIGHT)
                .tooltip(Tooltip.create(Component.literal("Closes this menu and discards every change you have not applied.")))
                .build());
    }

    private void addCategory(int slot, String label, String tooltip, Supplier<Screen> target) {
        int x = this.width / 2 - WIDGET_WIDTH - 5 + (slot % 2) * (WIDGET_WIDTH + 10);
        int y = 40 + (slot / 2) * 26;
        addRenderableWidget(Button.builder(Component.literal(label),
                        b -> Minecraft.getInstance().setScreen(target.get()))
                .bounds(x, y, WIDGET_WIDTH, WIDGET_HEIGHT)
                .tooltip(Tooltip.create(Component.literal(tooltip)))
                .build());
    }

    private void applyAndClose() {
        Minecraft mc = Minecraft.getInstance();
        if (session.videoDirty()) {
            // Changing vanilla settings is never done silently: show exactly what will change.
            List<String> changes = session.describeVideoChanges();
            String message = "These Minecraft video settings will change:\n"
                    + String.join("\n", changes)
                    + "\nThe new values are saved to your Minecraft options.";
            mc.setScreen(new ConfirmScreen(confirmed -> {
                if (confirmed) {
                    session.apply(mc);
                    mc.setScreen(parent);
                } else {
                    mc.setScreen(this);
                }
            }, Component.literal("Apply DFS Optimization settings?"), Component.literal(message)));
        } else {
            session.apply(mc);
            mc.setScreen(parent);
        }
    }

    /** Escape and the Cancel button both leave without applying. */
    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 14, 0xFFFFFFFF);
        if (session.isDirty()) {
            graphics.drawCenteredString(this.font, "You have changes that are not applied yet.",
                    this.width / 2, this.height - 42, 0xFFFFFF55);
        }
    }
}
