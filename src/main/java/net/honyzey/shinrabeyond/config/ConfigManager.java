package net.honyzey.shinrabeyond.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.honyzey.shinrabeyond.ShinraBeyond;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "shinrabeyond.json";

    private static ConfigData config;

    public static ConfigData getConfig() {
        return config;
    }

    public static void loadConfig(Path configDir) {
        try {
            File file = configDir.resolve(FILE_NAME).toFile();

            if (!file.exists()) {
                ShinraBeyond.LOGGER.info("Config not found, generating default...");
                config = ConfigData.createDefault();
                saveConfig(file);
            } else {
                FileReader reader = new FileReader(file);
                config = GSON.fromJson(reader, ConfigData.class);
                reader.close();
                ShinraBeyond.LOGGER.info("Config loaded successfully.");
            }
        } catch (Exception e) {
            ShinraBeyond.LOGGER.error("Failed to load config, using defaults!", e);
            config = ConfigData.createDefault();
        }
    }

    public static void saveConfig(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);
        } catch (Exception e) {
            ShinraBeyond.LOGGER.error("Failed to save config!", e);
        }
    }
}
