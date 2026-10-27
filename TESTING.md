# Testing report (factual)

## Done in the authoring environment

| Check | Result |
|---|---|
| Pure logic classes (`VideoSettings`, `Preset`, `DfsConfig`, `FrameSampler`, `Diagnostics`) compile with JDK 21 | OK, no warnings with `-Xlint:all` |
| 18 unit tests in `src/test/.../LogicTest.java` run against those real classes | 18 passed, 0 failed (ran with a tiny stand-in for the JUnit annotations, because Maven Central was unreachable) |
| `fabric.mod.json` is valid JSON, id/version/author/icon/entrypoint present | OK |
| `icon.png` is a valid 128x128 PNG, fully opaque | OK, viewed by eye |
| ZIP created and listed | OK |

## NOT done (no network, no Minecraft/Fabric downloads available)

| Check | Status |
|---|---|
| `gradle build` with Loom, remapped production jar | **Not run** |
| Compilation of the classes that use Minecraft/Fabric APIs (`gui/*`, `video/VanillaBridge`, `hud/MonitorHud`, `DfsOptimizationClient`, `config/ConfigStore`) | **Not compiled** |
| Gradle wrapper (`gradlew`, wrapper jar) | **Not generated** |
| Loading in a real Minecraft 1.21.11 client, opening the menu, applying settings, restart persistence | **Not run** |
| GUI at several window sizes | **Not run** |
| FPS benchmark | **Not run, no numbers are claimed** |

The Minecraft-facing code was written against the official Fabric 1.21.11 documentation (HUD API, widgets,
`Identifier`, Mojang mappings). The riskiest calls, checked first if the build complains:

1. `Screens.getButtons(screen)` and `ScreenEvents.AFTER_INIT` in `DfsOptimizationClient`
2. `HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, ...)` in `DfsOptimizationClient`
3. `AbstractSliderButton` constructor and `value` field in `gui/IntSlider`
4. `ConfirmScreen` constructor in `gui/MainScreen`
5. Option accessors in `video/VanillaBridge` (`particles()`, `cloudStatus()`, `entityShadows()`,
   `renderDistance()`, `simulationDistance()`, `framerateLimit()`, `enableVsync()`, `biomeBlendRadius()`)

## Manual runtime checklist (after a successful build)

1. Start 1.21.11 with Fabric API and the jar. The game reaches the title screen.
2. Options shows a "DFS Optimization" button top left, from the title screen and from the pause menu.
3. Each category opens and Back returns. Escape on the main screen discards changes.
4. Choose Low, press Apply & Close, accept the confirmation. Check in Options > Video Settings that
   particles, clouds, shadows, render distance, simulation distance and biome blend match the Low preset.
5. Restart the game. The values are still set (they live in `options.txt`).
6. Turn on the HUD monitor and Apply. The panel shows FPS, frame time and memory; each toggle hides its line.
7. Delete `config/dfsoptimization.json`: it is recreated. Put garbage in it: it is renamed to `.broken` and the
   game still starts.
8. Resize the window and use GUI scale 1 to 4: no button is cut off.
