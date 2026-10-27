package com.dfsstudio.dfsoptimization.config;

import java.util.Objects;

/**
 * Settings stored in config/dfsoptimization.json. Gson reads and writes the public fields.
 * Vanilla video options are NOT stored here; they stay in Minecraft's own options.txt.
 */
public final class DfsConfig {
    public static final int CURRENT_VERSION = 1;
    public static final String[] CORNER_LABELS = {"Top Left", "Top Right", "Bottom Left", "Bottom Right"};

    public int configVersion = CURRENT_VERSION;
    /** Name of the last preset that was applied, informational only. */
    public String preset = "CUSTOM";
    public boolean hudEnabled = false;
    public boolean hudShowFps = true;
    public boolean hudShowFrameTime = true;
    public boolean hudShowMemory = true;
    /** 0 top left, 1 top right, 2 bottom left, 3 bottom right. */
    public int hudCorner = 0;

    public static DfsConfig defaults() {
        return new DfsConfig();
    }

    public DfsConfig copy() {
        DfsConfig c = new DfsConfig();
        c.copyFrom(this);
        return c;
    }

    public void copyFrom(DfsConfig o) {
        this.configVersion = o.configVersion;
        this.preset = o.preset;
        this.hudEnabled = o.hudEnabled;
        this.hudShowFps = o.hudShowFps;
        this.hudShowFrameTime = o.hudShowFrameTime;
        this.hudShowMemory = o.hudShowMemory;
        this.hudCorner = o.hudCorner;
    }

    /** Forces every value into a valid range. Returns this for chaining. */
    public DfsConfig sanitize() {
        configVersion = CURRENT_VERSION;
        preset = Preset.fromName(preset).name();
        hudCorner = VideoSettings.clamp(hudCorner, 0, CORNER_LABELS.length - 1);
        return this;
    }

    public static String cornerLabel(int corner) {
        return CORNER_LABELS[VideoSettings.clamp(corner, 0, CORNER_LABELS.length - 1)];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DfsConfig c)) {
            return false;
        }
        return configVersion == c.configVersion
                && hudEnabled == c.hudEnabled
                && hudShowFps == c.hudShowFps
                && hudShowFrameTime == c.hudShowFrameTime
                && hudShowMemory == c.hudShowMemory
                && hudCorner == c.hudCorner
                && Objects.equals(preset, c.preset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configVersion, preset, hudEnabled, hudShowFps,
                hudShowFrameTime, hudShowMemory, hudCorner);
    }
}
