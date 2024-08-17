package org.achymake.mineableores.listeners;

import org.achymake.mineableores.MineableOres;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

public class BlockDropItem implements Listener {
    private MineableOres getMineableOres() {
        return MineableOres.getInstance();
    }
    private FileConfiguration getConfig() {
        return getMineableOres().getConfig();
    }
    private Map<Location, Material> getOres() {
        return getMineableOres().getOres();
    }
    public BlockDropItem() {
        Bukkit.getPluginManager().registerEvents(this, getMineableOres());
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockDropItem(BlockDropItemEvent event) {
        Block block = event.getBlock();
        if (!getOres().containsKey(event.getBlock().getLocation()))return;
        if (!getConfig().getStringList("worlds").contains(block.getWorld().getName()))return;
        if (!getMineableOres().getConfig().getBoolean("ores." + getOres().get(block.getLocation()) + ".enable"))return;
        Material material = getOres().get(block.getLocation());
        if (event.isCancelled())return;
        event.getItems().forEach(item -> {
            giveItem(event.getPlayer(), item.getItemStack());
            item.remove();
        });
        if (isDeepslateOre(material)) {
            block.setType(Material.COBBLED_DEEPSLATE);
        } else if (isNetherOre(material)) {
            block.setType(Material.NETHERRACK);
        } else {
            block.setType(Material.COBBLESTONE);
        }
    }
    private void giveItem(Player player, ItemStack itemStack) {
        if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
            player.getInventory().addItem(itemStack);
        } else {
            player.getWorld().dropItem(player.getLocation(), itemStack);
        }
    }
    private boolean isDeepslateOre(Material material) {
        return material.toString().contains("DEEPSLATE_");
    }
    private boolean isNetherOre(Material material) {
        return material.toString().contains("NETHER_");
    }
}