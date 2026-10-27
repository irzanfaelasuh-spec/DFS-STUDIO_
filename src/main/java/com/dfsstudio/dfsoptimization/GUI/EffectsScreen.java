package com.dfsstudio.dfsoptimization.gui;

import com.dfsstudio.dfsoptimization.config.VideoSettings;
import net.minecraft.client.gui.screens.Screen;

final class EffectsScreen extends DfsScreen {
    EffectsScreen(Screen parent, DfsSession session) {
        super("Particles & Effects", parent, session);
    }

    @Override
    protected void buildContent() {
        VideoSettings v = session.pendingVideo;
        addCycle("Particles", "How many particles the game spawns. Minimal is the lightest; "
                        + "some gameplay cues such as damage hearts may look sparser.",
                () -> v.particles, value -> v.particles = value,
                current -> VideoSettings.next(current, VideoSettings.PARTICLE_VALUES),
                VideoSettings::particleLabel);
        addCycle("Clouds", "Off is the cheapest, Fast draws flat clouds, Fancy draws 3D clouds.",
                () -> v.clouds, value -> v.clouds = value,
                current -> VideoSettings.next(current, VideoSettings.CLOUD_VALUES),
                VideoSettings::cloudLabel);
        addToggle("Entity Shadows", "Draws a soft shadow under mobs and players. Turning it off saves "
                        + "a little work in crowded areas.",
                () -> v.entityShadows, value -> v.entityShadows = value);
    }

    @Override
    protected void resetCategory() {
        VideoSettings d = VideoSettings.defaults();
        VideoSettings v = session.pendingVideo;
        v.particles = d.particles;
        v.clouds = d.clouds;
        v.entityShadows = d.entityShadows;
    }
}
