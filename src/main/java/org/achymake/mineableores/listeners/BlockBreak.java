package org.achymake.mineableores.listeners;

import org.achymake.mineableores.MineableOres;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class BlockBreak implements Listener {
    private MineableOres getMineableOres() {
        return MineableOres.getInstance();
    }
    private FileConfiguration getConfig() {
        return getMineableOres().getConfig();
    }
    private Map<Location, Material> getOres() {
        return getMineableOres().getOres();
    }
    public BlockBreak() {
        Bukkit.getPluginManager().registerEvents(this, getMineableOres());
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (!getConfig().getStringList("worlds").contains(block.getWorld().getName()))return;
        if (!getMineableOres().getConfig().getBoolean("ores." + block.getType() + ".enable"))return;
        if (!player.hasPermission("mineableores.event.block-break.collect"))return;
        if (!player.getGameMode().equals(GameMode.SURVIVAL))return;
        ItemStack pickAxe = player.getInventory().getItemInMainHand();
        if (!Tag.ITEMS_PICKAXES.isTagged(pickAxe.getType()))return;
        if (getOres().containsKey(block.getLocation())) {
            event.setCancelled(true);
        } else {
            if (event.isCancelled())return;
            getOres().put(block.getLocation(), block.getType());
            startScheduler(block);
        }
    }
    public void startScheduler(Block block) {
        Location location = block.getLocation();
        Material material = block.getType();
        Bukkit.getServer().getScheduler().runTaskLater(MineableOres.getInstance(), new Runnable() {
            @Override
            public void run() {
                block.setType(getMineableOres().getOres().get(location));
                getMineableOres().getOres().remove(location);
            }
        }, getConfig().getInt("ores." + material + ".replace-default-in") * 20);
    }
}