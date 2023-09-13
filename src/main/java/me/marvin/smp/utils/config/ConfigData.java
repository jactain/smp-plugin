package me.marvin.smp.utils.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public final class ConfigData {
    private final JavaPlugin plugin;
    private final String name;

    private final YamlConfiguration configuration;
    private final File file;

    public ConfigData(@NotNull JavaPlugin plugin, @NotNull String name) {
        this.plugin = plugin;
        this.name = name;
        this.file = new File(plugin.getDataFolder() + File.separator + name + ".yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public ConfigData saveDefault() {
        boolean ignored = plugin.getDataFolder().mkdirs();
        if (!Objects.requireNonNull(file, "ConfigData#file was null").exists()) {
            plugin.saveResource(name + ".yml", false);
        }
        load();
        return this;
    }

    public ConfigData load() {
        try {
            Objects.requireNonNull(configuration, "ConfigData#configuration was null").load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public ConfigData save() {
        try {
            Objects.requireNonNull(configuration, "ConfigData#configuration was null").save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public String name() {
        return name;
    }

    public YamlConfiguration configuration() {
        return configuration;
    }

    public File file() {
        return file;
    }
}
