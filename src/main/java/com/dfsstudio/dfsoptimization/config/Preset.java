package com.dfsstudio.dfsoptimization.config;

/**
 * Quality presets. A preset only touches these options: particles, clouds, entity
 * shadows, render distance, simulation distance and biome blend. Max framerate and
 * VSync are personal choices and are never changed by a preset.
 * CUSTOM means "whatever the values currently are".
 */
public enum Preset {
    LOW("Low", "MINIMAL", "OFF", false, 6, 5, 0),
    BALANCED("Balanced", "DECREASED", "FAST", true, 10, 8, 2),
    HIGH("High", "ALL", "FANCY", true, 16, 12, 4),
    CUSTOM("Custom", null, null, false, 0, 0, 0);

    private final String label;
    private final String particles;
    private final String clouds;
    private final boolean entityShadows;
    private final int renderDistance;
    private final int simulationDistance;
    private final int biomeBlend;

    Preset(String label, String particles, String clouds, boolean entityShadows,
           int renderDistance, int simulationDistance, int biomeBlend) {
        this.label = label;
        this.particles = particles;
        this.clouds = clouds;
        this.entityShadows = entityShadows;
        this.renderDistance = renderDistance;
        this.simulationDistance = simulationDistance;
        this.biomeBlend = biomeBlend;
    }

    public String label() {
        return label;
    }

    /** Writes this preset's values into the given settings. CUSTOM changes nothing. */
    public void applyTo(VideoSettings s) {
        if (this == CUSTOM) {
            return;
        }
        s.particles = particles;
        s.clouds = clouds;
        s.entityShadows = entityShadows;
        s.renderDistance = renderDistance;
        s.simulationDistance = simulationDistance;
        s.biomeBlend = biomeBlend;
    }

    public boolean matches(VideoSettings s) {
        if (this == CUSTOM) {
            return false;
        }
        return particles.equals(s.particles)
                && clouds.equals(s.clouds)
                && entityShadows == s.entityShadows
                && renderDistance == s.renderDistance
                && simulationDistance == s.simulationDistance
                && biomeBlend == s.biomeBlend;
    }

    /** Returns the preset whose values equal the settings, otherwise CUSTOM. */
    public static Preset detect(VideoSettings s) {
        for (Preset p : values()) {
            if (p.matches(s)) {
                return p;
            }
        }
        return CUSTOM;
    }

    /** Cycle order Low, Balanced, High, Low... CUSTOM starts the cycle at Low. */
    public Preset next() {
        return switch (this) {
            case LOW -> BALANCED;
            case BALANCED -> HIGH;
            case HIGH, CUSTOM -> LOW;
        };
    }

    public String describe() {
        if (this == CUSTOM) {
            return "Your own values. Changing any option by hand switches to Custom.";
        }
        return label + ": particles " + VideoSettings.particleLabel(particles)
                + ", clouds " + VideoSettings.cloudLabel(clouds)
                + ", entity shadows " + VideoSettings.onOff(entityShadows)
                + ", render distance " + renderDistance
                + ", simulation distance " + simulationDistance
                + ", biome blend " + biomeBlend + ".";
    }

    public static Preset fromName(String name) {
        if (name != null) {
            for (Preset p : values()) {
                if (p.name().equalsIgnoreCase(name)) {
                    return p;
                }
            }
        }
        return CUSTOM;
    }
}
