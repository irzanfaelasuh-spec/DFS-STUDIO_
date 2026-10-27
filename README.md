# DFS Optimization

**Creator:** DFSstudio  
**Minecraft:** Java Edition 1.21.11 | **Loader:** Fabric | **Side:** client only | **Java:** 21

A configurable client-side performance mod for Minecraft: Java Edition. DFS Optimization provides practical
graphics and rendering controls designed to reduce unnecessary visual workload. Actual performance gains depend
on your hardware, settings, world, and other installed mods.

DFS Optimization is a standalone settings mod. It is **not** a replacement for rendering engines such as Sodium,
and it makes no FPS promises.

## Status of this release (please read)

The source code in this project was written without access to the Minecraft and Fabric downloads, so **the jar
has not been built or run yet**. See [TESTING.md](TESTING.md) for exactly what was and was not verified.
Build the jar with one of the two methods below. If the build prints an error, send the log to the author
instead of guessing.

## Features

Every option changes a real Minecraft setting (through the game's own option objects) or the monitor panel.
Nothing is a placeholder.

| Page | Options |
|---|---|
| General | Presets Low / Balanced / High (Custom is detected automatically), Load Minecraft Defaults |
| Graphics | Max framerate, VSync, biome blend |
| Particles & Effects | Particles (All / Decreased / Minimal), clouds (Off / Fast / Fancy), entity shadows |
| Performance | Render distance, simulation distance, live diagnostics page |
| HUD / Monitoring | Optional panel: FPS, frame time, Java memory; corner selection |
| About | Version, creator, description, feature summary |

Behavior you can rely on:

* Nothing is changed silently. Edits stay in a pending copy until you press **Apply & Close**.
  If vanilla video settings would change, a confirmation screen lists every old and new value first.
* **Cancel**, **Back** on the main screen, and Escape discard unapplied edits.
* Presets change only: particles, clouds, entity shadows, render distance, simulation distance, biome blend.
  They never touch the framerate limit or VSync.
* The HUD monitor is off by default. When off, the mod does no per-frame work.
* No network access, no telemetry, no mixins, no server code.

### Preset values

| Preset | Particles | Clouds | Shadows | Render | Simulation | Biome blend |
|---|---|---|---|---|---|---|
| Low | Minimal | Off | Off | 6 | 5 | 0 |
| Balanced | Decreased | Fast | On | 10 | 8 | 2 |
| High | All | Fancy | On | 16 | 12 | 4 |

## Installation (players)

1. Install Fabric Loader for Minecraft 1.21.11 (see the Fabric installation guide).
2. Install **Fabric API** for 1.21.11 (required).
3. Copy `dfs-optimization-1.21.11-fabric.jar` into your `mods` folder.
4. Start the game. Open **Options** (from the title screen or the pause menu). The **DFS Optimization** button is
   in the top left corner.

## Configuration

* Mod settings: `config/dfsoptimization.json` (HUD options and the last applied preset name).
* Video options are stored by Minecraft itself in `options.txt`; DFS Optimization does not reapply them at startup.
* A missing or empty file is recreated with defaults. A file that cannot be parsed is renamed to
  `dfsoptimization.json.broken` and defaults are used. Invalid values are corrected on load.

## Building the jar

### Method A: GitHub Actions (no local setup)

1. Create a new GitHub repository and upload the contents of this project folder (keep `.github/workflows/build.yml`).
2. Open the **Actions** tab and run **Build DFS Optimization** (it also runs on every push).
3. When it finishes, download the artifact **dfs-optimization-1.21.11-fabric**. It contains
   `dfs-optimization-1.21.11-fabric.jar`.

### Method B: on your computer

Requirements: JDK 21 and Gradle 9.2.1 (or newer 9.x if Loom requires it).

```
gradle wrapper --gradle-version 9.2.1     # once, creates gradlew / gradlew.bat
./gradlew build                           # Windows: gradlew.bat build
```

The installable jar is `build/libs/dfs-optimization-1.21.11-fabric.jar`. Ignore the `*-dev.jar` and
`*-sources.jar` files in the same folder. Run `./gradlew test` for the unit tests and `./gradlew runClient`
to start a development client.

The Gradle wrapper files (`gradlew`, `gradle/wrapper/gradle-wrapper.jar`) are not included because they could not
be generated offline. `gradle wrapper` creates them.

## Versions used

| Component | Version | Where it was checked |
|---|---|---|
| Minecraft | 1.21.11 | Fabric blog post for 1.21.11 |
| Fabric Loader | 0.18.1 | Fabric blog post for 1.21.11 (stable at the time) |
| Fabric Loom | 1.14-SNAPSHOT | Fabric blog post for 1.21.11 (recommends Loom 1.14) |
| Fabric API | 0.139.5+1.21.11 | Official CurseForge file page |
| Mappings | Official Mojang mappings | Fabric recommends them; 1.21.11 is the last obfuscated version |
| Gradle (CI) | 9.2.1 | Chosen for Loom 1.14, not verified by a build |
| Java | 21 | Fabric developer docs |

Newer 0.141.x builds of Fabric API also exist for 1.21.11. To use one, change `fabric_api_version` in
`gradle.properties`. Compiling against an older Fabric API does not stop the mod from running with a newer one.

## Troubleshooting

* **Gradle cannot find a dependency version:** update the matching line in `gradle.properties` using the
  official Fabric develop page.
* **Compile error mentioning a Minecraft or Fabric class or method:** the 1.21.11 API changed from older
  tutorials. Send the full error text so the exact line can be fixed.
* **Mod does not appear in the mod list:** check that Fabric API is installed and that you used the file
  without `-dev` or `-sources` in its name.
* **No DFS Optimization button in Options:** the button is added through Fabric's screen events. If another
  mod rebuilds that screen after DFS Optimization, it can disappear. Report it with your mod list.
* **A value did not change after Apply:** Minecraft can refuse a value. The game log then contains the line
  "Minecraft did not accept every requested value".

## Limitations

* Gains come only from lowering the load that vanilla itself creates. Results depend on hardware, drivers,
  world, resolution, shaders and other mods.
* If another mod changes the same vanilla options, the last change wins.
* The HUD panel can overlap the F3 debug screen; choose another corner.
* Diagnostics hints are rough estimates from a half second sample, not a profiler.

## License

MIT. See [LICENSE](LICENSE). The icon is an original, programmatically drawn image.
