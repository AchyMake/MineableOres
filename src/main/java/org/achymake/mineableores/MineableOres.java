package org.achymake.mineableores;

import org.achymake.mineableores.commands.MineableOresCommand;
import org.achymake.mineableores.listeners.BlockBreak;
import org.achymake.mineableores.listeners.BlockDropItem;
import org.achymake.mineableores.listeners.PlayerJoin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public final class MineableOres extends JavaPlugin {
    private final Map<Location, Material> ores = new HashMap<>();
    private static MineableOres instance;
    private static Message message;
    private static UpdateChecker updateChecker;
    @Override
    public void onEnable() {
        instance = this;
        message = new Message();
        updateChecker = new UpdateChecker();
        getCommand("mineableores").setExecutor(new MineableOresCommand());
        new BlockBreak();
        new BlockDropItem();
        new PlayerJoin();
        reload();
        getMessage().sendLog(Level.INFO, "Enabled " + name() + " " + version());
        getUpdateChecker().getUpdate();
    }
    @Override
    public void onDisable() {
        if (!getOres().isEmpty()) {
            getMessage().sendLog(Level.INFO, "Replacing default blocks");
            getOres().forEach((location, material) -> location.getBlock().setType(material));
            getMessage().sendLog(Level.INFO, "Default blocks has been placed");
        }
        getMessage().sendLog(Level.INFO, "Disabled " + name() + " " + version());
    }
    public void reload() {
        File file = new File(getDataFolder(), "config.yml");
        if (file.exists()) {
            try {
                getConfig().load(file);
            } catch (IOException | InvalidConfigurationException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        } else {
            getConfig().options().copyDefaults(true);
            try {
                getConfig().save(file);
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
    public Message getMessage() {
        return message;
    }
    public static MineableOres getInstance() {
        return instance;
    }
    public Map<Location, Material> getOres() {
        return ores;
    }
    public String name() {
        return getDescription().getName();
    }
    public String version() {
        return getDescription().getVersion();
    }
}