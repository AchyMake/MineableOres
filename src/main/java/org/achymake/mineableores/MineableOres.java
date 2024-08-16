package org.achymake.mineableores;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.achymake.mineableores.commands.MineableOresCommand;
import org.achymake.mineableores.listeners.BlockBreak;
import org.achymake.mineableores.listeners.OreBreak;
import org.achymake.mineableores.listeners.PlayerJoin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
    public static StateFlag FLAG_COLLECT_ORE;
    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flagClaim = new StateFlag("mineable-ore", false);
            FLAG_COLLECT_ORE = flagClaim;
            registry.register(flagClaim);
        } catch (FlagConflictException ignored) {
            Flag<?> existingClaim = registry.get("mineable-ore");
            if (existingClaim instanceof StateFlag) {
                FLAG_COLLECT_ORE = (StateFlag) existingClaim;
            }
        } catch (Exception e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    @Override
    public void onEnable() {
        instance = this;
        message = new Message();
        updateChecker = new UpdateChecker();
        reload();
        getCommand("mineableores").setExecutor(new MineableOresCommand());
        new BlockBreak();
        new OreBreak();
        new PlayerJoin();
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
    public StateFlag getFlagCollectOre() {
        return FLAG_COLLECT_ORE;
    }
    public boolean isAllowedCollect(Block block) {
        try {
            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(block.getWorld()));
            if (regionManager != null) {
                ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion("_", BlockVector3.at(block.getX(), block.getY(), block.getZ()), BlockVector3.at(block.getX(), block.getY(), block.getZ()));
                for (ProtectedRegion regionIn : regionManager.getApplicableRegions(protectedCuboidRegion)) {
                    StateFlag.State flag = regionIn.getFlag(getFlagCollectOre());
                    if (flag == StateFlag.State.ALLOW) {
                        return true;
                    } else if (flag == StateFlag.State.DENY) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
            return false;
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