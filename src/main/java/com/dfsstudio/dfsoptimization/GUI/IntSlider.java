package com.dfsstudio.dfsoptimization.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.IntFunction;

/** Slider for whole numbers with a minimum, maximum and step. */
final class IntSlider extends AbstractSliderButton {
    private final String label;
    private final int min;
    private final int max;
    private final int step;
    private final IntFunction<String> formatter;
    private final Consumer<Integer> onChange;

    IntSlider(int x, int y, int width, int height, String label, int min, int max, int step,
              int current, IntFunction<String> formatter, Consumer<Integer> onChange) {
        super(x, y, width, height, Component.empty(), toRatio(current, min, max));
        this.label = label;
        this.min = min;
        this.max = max;
        this.step = step;
        this.formatter = formatter;
        this.onChange = onChange;
        updateMessage();
    }

    private static double toRatio(int value, int min, int max) {
        if (max <= min) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, (value - min) / (double) (max - min)));
    }

    private int currentValue() {
        int raw = min + (int) Math.round(this.value * (max - min));
        int snapped = min + Math.round((raw - min) / (float) step) * step;
        return Math.max(min, Math.min(max, snapped));
    }

    @Override
    protected void updateMessage() {
        // Called by the parent constructor before the fields exist, so guard against null.
        if (formatter == null) {
            return;
        }
        setMessage(Component.literal(label + ": " + formatter.apply(currentValue())));
    }

    @Override
    protected void applyValue() {
        if (onChange != null) {
            onChange.accept(currentValue());
        }
    }
}
