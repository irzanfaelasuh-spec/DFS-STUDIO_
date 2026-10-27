package com.dfsstudio.dfsoptimization;

import com.dfsstudio.dfsoptimization.config.DfsConfig;
import com.dfsstudio.dfsoptimization.config.Preset;
import com.dfsstudio.dfsoptimization.config.VideoSettings;
import com.dfsstudio.dfsoptimization.hud.Diagnostics;
import com.dfsstudio.dfsoptimization.hud.FrameSampler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for the pure logic. No Minecraft classes are needed. */
class LogicTest {

    @Test
    void everyPresetIsDetectedAfterItIsApplied() {
        for (Preset p : new Preset[]{Preset.LOW, Preset.BALANCED, Preset.HIGH}) {
            VideoSettings s = VideoSettings.defaults();
            p.applyTo(s);
            assertEquals(p, Preset.detect(s), "preset " + p);
        }
    }

    @Test
    void presetValuesAreAlreadyValid() {
        for (Preset p : new Preset[]{Preset.LOW, Preset.BALANCED, Preset.HIGH}) {
            VideoSettings s = VideoSettings.defaults();
            p.applyTo(s);
            VideoSettings before = s.copy();
            assertEquals(before, s.sanitize(), "preset " + p + " must not need correction");
        }
    }

    @Test
    void presetDoesNotTouchFramerateOrVsync() {
        VideoSettings s = VideoSettings.defaults();
        s.maxFps = 75;
        s.vsync = false;
        Preset.LOW.applyTo(s);
        assertEquals(75, s.maxFps);
        assertFalse(s.vsync);
    }

    @Test
    void manualEditSwitchesToCustom() {
        VideoSettings s = VideoSettings.defaults();
        Preset.BALANCED.applyTo(s);
        s.renderDistance += 1;
        assertEquals(Preset.CUSTOM, Preset.detect(s));
    }

    @Test
    void customPresetKeepsUserValues() {
        VideoSettings s = VideoSettings.defaults();
        s.renderDistance = 21;
        s.particles = "MINIMAL";
        VideoSettings before = s.copy();
        Preset.CUSTOM.applyTo(s);
        assertEquals(before, s);
    }

    @Test
    void presetCycleOrder() {
        assertEquals(Preset.BALANCED, Preset.LOW.next());
        assertEquals(Preset.HIGH, Preset.BALANCED.next());
        assertEquals(Preset.LOW, Preset.HIGH.next());
        assertEquals(Preset.LOW, Preset.CUSTOM.next());
    }

    @Test
    void presetNameParsingIsSafe() {
        assertEquals(Preset.HIGH, Preset.fromName("high"));
        assertEquals(Preset.CUSTOM, Preset.fromName("nonsense"));
        assertEquals(Preset.CUSTOM, Preset.fromName(null));
    }

    @Test
    void sanitizeClampsAndRepairs() {
        VideoSettings s = new VideoSettings();
        s.renderDistance = 999;
        s.simulationDistance = -4;
        s.maxFps = 0;
        s.biomeBlend = 50;
        s.particles = "LOTS";
        s.clouds = null;
        s.sanitize();
        assertEquals(VideoSettings.MAX_RENDER_DISTANCE, s.renderDistance);
        assertEquals(VideoSettings.MIN_SIMULATION_DISTANCE, s.simulationDistance);
        assertEquals(VideoSettings.MIN_MAX_FPS, s.maxFps);
        assertEquals(VideoSettings.MAX_BIOME_BLEND, s.biomeBlend);
        assertEquals("ALL", s.particles);
        assertEquals("FANCY", s.clouds);
    }

    @Test
    void diffListsOnlyRealChanges() {
        VideoSettings a = VideoSettings.defaults();
        VideoSettings b = a.copy();
        assertTrue(a.diff(b).isEmpty());
        b.renderDistance = 8;
        b.entityShadows = false;
        List<String> d = a.diff(b);
        assertEquals(2, d.size());
        assertTrue(d.contains("Render Distance: 12 -> 8"));
        assertTrue(d.contains("Entity Shadows: On -> Off"));
    }

    @Test
    void copyIsIndependent() {
        VideoSettings a = VideoSettings.defaults();
        VideoSettings b = a.copy();
        b.renderDistance = 3;
        assertEquals(12, a.renderDistance);
    }

    @Test
    void cycleWrapsAround() {
        assertEquals("DECREASED", VideoSettings.next("ALL", VideoSettings.PARTICLE_VALUES));
        assertEquals("ALL", VideoSettings.next("MINIMAL", VideoSettings.PARTICLE_VALUES));
        assertEquals("ALL", VideoSettings.next("unknown", VideoSettings.PARTICLE_VALUES));
    }

    @Test
    void configSanitizeRepairsBadValues() {
        DfsConfig c = new DfsConfig();
        c.hudCorner = 77;
        c.preset = "???";
        c.configVersion = 0;
        c.sanitize();
        assertEquals(DfsConfig.CORNER_LABELS.length - 1, c.hudCorner);
        assertEquals("CUSTOM", c.preset);
        assertEquals(DfsConfig.CURRENT_VERSION, c.configVersion);
    }

    @Test
    void configDefaultsKeepHudOff() {
        assertFalse(DfsConfig.defaults().hudEnabled);
        assertEquals(DfsConfig.defaults(), DfsConfig.defaults().copy());
    }

    @Test
    void frameSamplerMeasuresSteadyFrames() {
        FrameSampler sampler = new FrameSampler();
        long t = 1_000_000_000L;
        for (int i = 0; i < 120; i++) {
            sampler.onFrame(t);
            t += 10_000_000L; // 10 ms per frame = 100 FPS
        }
        FrameSampler.Snapshot s = sampler.snapshot();
        assertTrue(s.valid());
        assertEquals(100, s.fps());
        assertEquals(10.0, s.avgFrameMs(), 0.01);
        assertEquals(10.0, s.worstFrameMs(), 0.01);
    }

    @Test
    void frameSamplerReportsWorstFrame() {
        FrameSampler sampler = new FrameSampler();
        long t = 1_000_000_000L;
        for (int i = 0; i < 100; i++) {
            sampler.onFrame(t);
            t += (i == 50) ? 80_000_000L : 10_000_000L;
        }
        assertTrue(sampler.snapshot().valid());
        assertEquals(80.0, sampler.snapshot().worstFrameMs(), 0.01);
    }

    @Test
    void frameSamplerIgnoresDuplicateCallsInTheSameFrame() {
        FrameSampler sampler = new FrameSampler();
        long t = 1_000_000_000L;
        for (int i = 0; i < 120; i++) {
            sampler.onFrame(t);
            sampler.onFrame(t + 1_000); // second caller a microsecond later
            t += 10_000_000L;
        }
        assertEquals(100, sampler.snapshot().fps());
    }

    @Test
    void frameSamplerRestartsAfterLongPause() {
        FrameSampler sampler = new FrameSampler();
        sampler.onFrame(1_000_000_000L);
        sampler.onFrame(10_000_000_000L); // 9 s gap must not count as one giant frame
        assertFalse(sampler.snapshot().valid());
    }

    @Test
    void diagnosticsGivesHintsOnlyWhenRelevant() {
        assertEquals(1, Diagnostics.hints(8.0, 12.0, 1000, 4096, 12).size());
        assertTrue(Diagnostics.hints(8.0, 12.0, 1000, 4096, 12).get(0).startsWith("No obvious"));
        assertTrue(Diagnostics.hints(8.0, 12.0, 3800, 4096, 12).get(0).startsWith("Memory use is high"));
        assertTrue(Diagnostics.hints(30.0, 35.0, 1000, 4096, 16).get(0).startsWith("Frame times are high"));
    }
}
