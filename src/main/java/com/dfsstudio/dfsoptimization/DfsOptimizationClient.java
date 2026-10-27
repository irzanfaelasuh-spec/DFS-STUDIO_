package com.dfsstudio.dfsoptimization;

import com.dfsstudio.dfsoptimization.config.ConfigStore;
import com.dfsstudio.dfsoptimization.config.DfsConfig;
import com.dfsstudio.dfsoptimization.gui.MainScreen;
import com.dfsstudio.dfsoptimization.hud.MonitorHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Client entrypoint. The mod is client-only and has no server code and no mixins. */
public final class DfsOptimizationClient implements ClientModInitializer {
    public static final String MOD_ID = "dfsoptimization";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static DfsConfig config = DfsConfig.defaults();

    @Override
    public void onInitializeClient() {
        config = ConfigStore.load();

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "monitor"),
                MonitorHud::render);

        ScreenEvents.AFTER_INIT.register(DfsOptimizationClient::onScreenInit);
        LOGGER.info("DFS Optimization {} loaded.", version());
    }

    /** Adds a "DFS Optimization" button to the vanilla Options screen (top left corner). */
    private static void onScreenInit(Minecraft client, Screen screen, int scaledWidth, int scaledHeight) {
        if (screen instanceof OptionsScreen) {
            Button button = Button.builder(Component.literal("DFS Optimization"),
                            b -> client.setScreen(new MainScreen(screen)))
                    .bounds(6, 6, 120, 20)
                    .tooltip(Tooltip.create(Component.literal("Open the DFS Optimization settings.")))
                    .build();
            Screens.getButtons(screen).add(button);
        }
    }

    public static DfsConfig config() {
        return config;
    }

    /** Replaces the active config with a sanitized copy and writes it to disk. */
    public static void updateConfig(DfsConfig newConfig) {
        config = newConfig.copy().sanitize();
        ConfigStore.save(config);
    }

    public static String version() {
        return FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
    }
}
