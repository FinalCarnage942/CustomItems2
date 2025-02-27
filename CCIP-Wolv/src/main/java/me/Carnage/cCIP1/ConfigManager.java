package me.Carnage.cCIP1;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDefaultCooldown() {
        return config.getInt("general.default-cooldown");
    }

    public int getDefaultEffectDuration() {
        return config.getInt("general.default-effect-duration");
    }

    public double getFallingBlockSpeed() {
        return config.getDouble("general.falling-block.speed");
    }

    public int getFallingBlockWidth() {
        return config.getInt("general.falling-block.width");
    }

    public String getItemName(Material material) {
        return config.getString("items." + material.name() + ".name");
    }

    public String[] getItemDescription(Material material) {
        return config.getStringList("items." + material.name() + ".description").toArray(new String[0]);
    }

    public int getItemCooldown(Material material) {
        return config.getInt("items." + material.name() + ".cooldown");
    }

    public String getItemEffectMessage(Material material, String effectType) {
        return config.getString("items." + material.name() + ".effects." + effectType + ".message");
    }

    public int getItemEffectDuration(Material material, String effectType) {
        return config.getInt("items." + material.name() + ".effects." + effectType + ".duration");
    }

    public int getItemEffectAmplifier(Material material, String effectType) {
        return config.getInt("items." + material.name() + ".effects." + effectType + ".amplifier");
    }

    public double getItemEffectStrength(Material material, String effectType) {
        return config.getDouble("items." + material.name() + ".effects." + effectType + ".strength");
    }
}
