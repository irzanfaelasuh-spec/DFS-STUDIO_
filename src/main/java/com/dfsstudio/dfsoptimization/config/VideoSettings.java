package com.dfsstudio.dfsoptimization.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Plain data holder for the vanilla video options that DFS Optimization manages.
 * It contains no Minecraft classes so the logic can be unit tested without the game.
 * Enum-like values are stored by constant name (for example "MINIMAL").
 */
public final class VideoSettings {
    public static final String[] PARTICLE_VALUES = {"ALL", "DECREASED", "MINIMAL"};
    public static final String[] CLOUD_VALUES = {"OFF", "FAST", "FANCY"};

    public static final int MIN_RENDER_DISTANCE = 2;
    public static final int MAX_RENDER_DISTANCE = 32;
    public static final int MIN_SIMULATION_DISTANCE = 5;
    public static final int MAX_SIMULATION_DISTANCE = 32;
    public static final int MIN_MAX_FPS = 10;
    /** Vanilla treats 260 as "Unlimited". */
    public static final int UNLIMITED_FPS = 260;
    public static final int MIN_BIOME_BLEND = 0;
    public static final int MAX_BIOME_BLEND = 7;

    // Defaults match vanilla Minecraft defaults.
    public String particles = "ALL";
    public String clouds = "FANCY";
    public boolean entityShadows = true;
    public int renderDistance = 12;
    public int simulationDistance = 12;
    public int maxFps = 120;
    public boolean vsync = true;
    public int biomeBlend = 2;

    public static VideoSettings defaults() {
        return new VideoSettings();
    }

    public VideoSettings copy() {
        VideoSettings c = new VideoSettings();
        c.copyFrom(this);
        return c;
    }

    public void copyFrom(VideoSettings o) {
        this.particles = o.particles;
        this.clouds = o.clouds;
        this.entityShadows = o.entityShadows;
        this.renderDistance = o.renderDistance;
        this.simulationDistance = o.simulationDistance;
        this.maxFps = o.maxFps;
        this.vsync = o.vsync;
        this.biomeBlend = o.biomeBlend;
    }

    /** Forces every value into a valid range. Returns this for chaining. */
    public VideoSettings sanitize() {
        if (!contains(PARTICLE_VALUES, particles)) {
            particles = "ALL";
        }
        if (!contains(CLOUD_VALUES, clouds)) {
            clouds = "FANCY";
        }
        renderDistance = clamp(renderDistance, MIN_RENDER_DISTANCE, MAX_RENDER_DISTANCE);
        simulationDistance = clamp(simulationDistance, MIN_SIMULATION_DISTANCE, MAX_SIMULATION_DISTANCE);
        maxFps = clamp(maxFps, MIN_MAX_FPS, UNLIMITED_FPS);
        biomeBlend = clamp(biomeBlend, MIN_BIOME_BLEND, MAX_BIOME_BLEND);
        return this;
    }

    /** Human readable list of what differs between this (before) and other (after). */
    public List<String> diff(VideoSettings other) {
        List<String> out = new ArrayList<>();
        if (!Objects.equals(particles, other.particles)) {
            out.add("Particles: " + particleLabel(particles) + " -> " + particleLabel(other.particles));
        }
        if (!Objects.equals(clouds, other.clouds)) {
            out.add("Clouds: " + cloudLabel(clouds) + " -> " + cloudLabel(other.clouds));
        }
        if (entityShadows != other.entityShadows) {
            out.add("Entity Shadows: " + onOff(entityShadows) + " -> " + onOff(other.entityShadows));
        }
        if (renderDistance != other.renderDistance) {
            out.add("Render Distance: " + renderDistance + " -> " + other.renderDistance);
        }
        if (simulationDistance != other.simulationDistance) {
            out.add("Simulation Distance: " + simulationDistance + " -> " + other.simulationDistance);
        }
        if (maxFps != other.maxFps) {
            out.add("Max Framerate: " + maxFpsLabel(maxFps) + " -> " + maxFpsLabel(other.maxFps));
        }
        if (vsync != other.vsync) {
            out.add("VSync: " + onOff(vsync) + " -> " + onOff(other.vsync));
        }
        if (biomeBlend != other.biomeBlend) {
            out.add("Biome Blend: " + biomeBlend + " -> " + other.biomeBlend);
        }
        return out;
    }

    public static String next(String current, String[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(current)) {
                return values[(i + 1) % values.length];
            }
        }
        return values[0];
    }

    public static String particleLabel(String v) {
        return switch (v) {
            case "ALL" -> "All";
            case "DECREASED" -> "Decreased";
            case "MINIMAL" -> "Minimal";
            default -> v;
        };
    }

    public static String cloudLabel(String v) {
        return switch (v) {
            case "OFF" -> "Off";
            case "FAST" -> "Fast";
            case "FANCY" -> "Fancy";
            default -> v;
        };
    }

    public static String maxFpsLabel(int v) {
        return v >= UNLIMITED_FPS ? "Unlimited" : v + " FPS";
    }

    public static String onOff(boolean v) {
        return v ? "On" : "Off";
    }

    public static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static boolean contains(String[] values, String v) {
        if (v == null) {
            return false;
        }
        for (String s : values) {
            if (s.equals(v)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VideoSettings s)) {
            return false;
        }
        return entityShadows == s.entityShadows
                && renderDistance == s.renderDistance
                && simulationDistance == s.simulationDistance
                && maxFps == s.maxFps
                && vsync == s.vsync
                && biomeBlend == s.biomeBlend
                && Objects.equals(particles, s.particles)
                && Objects.equals(clouds, s.clouds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(particles, clouds, entityShadows, renderDistance,
                simulationDistance, maxFps, vsync, biomeBlend);
    }
}
