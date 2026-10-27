package com.dfsstudio.dfsoptimization.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Loads and saves config/dfsoptimization.json. A missing or broken file never crashes the game. */
public final class ConfigStore {
    private static final Logger LOGGER = LoggerFactory.getLogger("dfsoptimization");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "dfsoptimization.json";

    private ConfigStore() {
    }

    public static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public static DfsConfig load() {
        Path file = path();
        if (!Files.exists(file)) {
            DfsConfig defaults = DfsConfig.defaults();
            save(defaults);
            return defaults;
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            DfsConfig loaded = GSON.fromJson(reader, DfsConfig.class);
            if (loaded == null) {
                LOGGER.warn("Config file {} is empty, using defaults.", file);
                DfsConfig defaults = DfsConfig.defaults();
                save(defaults);
                return defaults;
            }
            return loaded.sanitize();
        } catch (JsonParseException | IOException e) {
            LOGGER.error("Could not read {} ({}). The broken file is kept as .broken and defaults are used.",
                    file, e.toString());
            backupBrokenFile(file);
            DfsConfig defaults = DfsConfig.defaults();
            save(defaults);
            return defaults;
        }
    }

    public static void save(DfsConfig config) {
        Path file = path();
        Path tmp = file.resolveSibling(FILE_NAME + ".tmp");
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                GSON.toJson(config.copy().sanitize(), writer);
            }
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Could not save {}: {}", file, e.toString());
        }
    }

    private static void backupBrokenFile(Path file) {
        try {
            Files.move(file, file.resolveSibling(FILE_NAME + ".broken"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Could not back up the broken config file: {}", e.toString());
        }
    }
}
