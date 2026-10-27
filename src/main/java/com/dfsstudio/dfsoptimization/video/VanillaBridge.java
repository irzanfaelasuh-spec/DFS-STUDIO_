package com.dfsstudio.dfsoptimization.video;

import com.dfsstudio.dfsoptimization.DfsOptimizationClient;
import com.dfsstudio.dfsoptimization.config.VideoSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;

import java.util.List;

/**
 * Reads and writes the real vanilla video options through Minecraft's own OptionInstance
 * objects, so every callback vanilla runs when you change a value in Video Settings also runs here.
 */
public final class VanillaBridge {
    private VanillaBridge() {
    }

    public static VideoSettings read(Minecraft mc) {
        Options o = mc.options;
        VideoSettings s = new VideoSettings();
        s.particles = o.particles().get().name();
        s.clouds = o.cloudStatus().get().name();
        s.entityShadows = o.entityShadows().get();
        s.renderDistance = o.renderDistance().get();
        s.simulationDistance = o.simulationDistance().get();
        s.maxFps = o.framerateLimit().get();
        s.vsync = o.enableVsync().get();
        s.biomeBlend = o.biomeBlendRadius().get();
        return s.sanitize();
    }

    /**
     * Sets an enum-valued option by constant name. The enum class is taken from the option's
     * current value, so this code does not depend on which package the enum lives in.
     */
    private static <E extends Enum<E>> void setEnumByName(OptionInstance<E> option, String name) {
        E current = option.get();
        option.set(Enum.valueOf(current.getDeclaringClass(), name));
    }

    /** Writes the settings to the vanilla options and saves options.txt. */
    public static void apply(Minecraft mc, VideoSettings wanted) {
        VideoSettings s = wanted.copy().sanitize();
        Options o = mc.options;
        setEnumByName(o.particles(), s.particles);
        setEnumByName(o.cloudStatus(), s.clouds);
        o.entityShadows().set(s.entityShadows);
        o.renderDistance().set(s.renderDistance);
        o.simulationDistance().set(s.simulationDistance);
        o.framerateLimit().set(s.maxFps);
        o.enableVsync().set(s.vsync);
        o.biomeBlendRadius().set(s.biomeBlend);
        o.save();

        // Verify what the game really holds now. Vanilla may reject a value (for example a
        // 32 chunk render distance on a 32-bit Java), and that must not be hidden from the log.
        List<String> mismatches = s.diff(read(mc));
        if (!mismatches.isEmpty()) {
            DfsOptimizationClient.LOGGER.warn("Minecraft did not accept every requested value: {}", mismatches);
        }
    }
}
