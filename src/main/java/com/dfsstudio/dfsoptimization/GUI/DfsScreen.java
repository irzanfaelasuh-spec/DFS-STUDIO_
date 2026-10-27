package com.dfsstudio.dfsoptimization.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Base class for the category pages. Two columns of widgets, a Reset button and a Back button.
 * Back returns to the main DFS screen without discarding edits; the main screen owns Apply and Cancel.
 */
abstract class DfsScreen extends Screen {
    protected static final int WIDGET_WIDTH = 150;
    protected static final int WIDGET_HEIGHT = 20;
    private static final int COLUMN_GAP = 10;
    private static final int FIRST_ROW_Y = 36;
    private static final int ROW_STEP = 24;

    protected final Screen parent;
    protected final DfsSession session;
    private int nextSlot;

    protected DfsScreen(String title, Screen parent, DfsSession session) {
        super(Component.literal(title));
        this.parent = parent;
        this.session = session;
    }

    /** Add the page's widgets here with the add... helpers below. */
    protected abstract void buildContent();

    /** Restores this category to its default values. Only called when hasReset() is true. */
    protected void resetCategory() {
    }

    protected boolean hasReset() {
        return true;
    }

    @Override
    protected void init() {
        nextSlot = 0;
        buildContent();

        int bottomY = this.height - 28;
        if (hasReset()) {
            addRenderableWidget(Button.builder(Component.literal("Reset Category"), b -> {
                        resetCategory();
                        rebuildWidgets();
                    })
                    .bounds(this.width / 2 - WIDGET_WIDTH - COLUMN_GAP / 2, bottomY, WIDGET_WIDTH, WIDGET_HEIGHT)
                    .tooltip(Tooltip.create(Component.literal("Sets this page back to its default values. Nothing is saved until you press Apply on the main screen.")))
                    .build());
            addRenderableWidget(Button.builder(Component.literal("Back"), b -> onClose())
                    .bounds(this.width / 2 + COLUMN_GAP / 2, bottomY, WIDGET_WIDTH, WIDGET_HEIGHT)
                    .build());
        } else {
            addRenderableWidget(Button.builder(Component.literal("Back"), b -> onClose())
                    .bounds(this.width / 2 - 100, bottomY, 200, WIDGET_HEIGHT)
                    .build());
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Do not call renderBackground here: the screen already draws its own background.
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 14, 0xFFFFFFFF);
    }

    // ----- layout helpers -----

    protected int slotX(int slot) {
        return this.width / 2 - WIDGET_WIDTH - COLUMN_GAP / 2 + (slot % 2) * (WIDGET_WIDTH + COLUMN_GAP);
    }

    protected int slotY(int slot) {
        return FIRST_ROW_Y + (slot / 2) * ROW_STEP;
    }

    /** Y position just below the last widget row, for explanatory text. */
    protected int contentBottomY() {
        int rows = (nextSlot + 1) / 2;
        return FIRST_ROW_Y + rows * ROW_STEP + 4;
    }

    protected void addToggle(String label, String tooltip, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        int slot = nextSlot++;
        addRenderableWidget(Button.builder(toggleText(label, getter.get()), b -> {
                    boolean value = !getter.get();
                    setter.accept(value);
                    b.setMessage(toggleText(label, value));
                })
                .bounds(slotX(slot), slotY(slot), WIDGET_WIDTH, WIDGET_HEIGHT)
                .tooltip(Tooltip.create(Component.literal(tooltip)))
                .build());
    }

    /** A button that steps through values each time it is clicked. */
    protected void addCycle(String label, String tooltip, Supplier<String> getter, Consumer<String> setter,
                            java.util.function.UnaryOperator<String> nextValue,
                            java.util.function.UnaryOperator<String> displayName) {
        int slot = nextSlot++;
        addRenderableWidget(Button.builder(cycleText(label, displayName.apply(getter.get())), b -> {
                    String value = nextValue.apply(getter.get());
                    setter.accept(value);
                    b.setMessage(cycleText(label, displayName.apply(value)));
                })
                .bounds(slotX(slot), slotY(slot), WIDGET_WIDTH, WIDGET_HEIGHT)
                .tooltip(Tooltip.create(Component.literal(tooltip)))
                .build());
    }

    /** A plain action button that occupies the next slot. */
    protected void addAction(String label, String tooltip, Runnable action) {
        int slot = nextSlot++;
        addRenderableWidget(Button.builder(Component.literal(label), b -> action.run())
                .bounds(slotX(slot), slotY(slot), WIDGET_WIDTH, WIDGET_HEIGHT)
                .tooltip(Tooltip.create(Component.literal(tooltip)))
                .build());
    }

    protected void addSlider(String label, String tooltip, int min, int max, int step,
                             Supplier<Integer> getter, Consumer<Integer> setter, IntFunction<String> formatter) {
        int slot = nextSlot++;
        IntSlider slider = new IntSlider(slotX(slot), slotY(slot), WIDGET_WIDTH, WIDGET_HEIGHT,
                label, min, max, step, getter.get(), formatter, setter);
        slider.setTooltip(Tooltip.create(Component.literal(tooltip)));
        addRenderableWidget(slider);
    }

    /** Skip a slot, for example to keep a button in the right column. */
    protected void skipSlot() {
        nextSlot++;
    }

    private static Component toggleText(String label, boolean value) {
        return Component.literal(label + ": " + (value ? "On" : "Off"));
    }

    private static Component cycleText(String label, String value) {
        return Component.literal(label + ": " + value);
    }
}
