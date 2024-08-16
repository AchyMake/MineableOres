package org.achymake.mineableores.listeners;

import org.achymake.mineableores.MineableOres;
import org.achymake.mineableores.listeners.custom.OreBreakEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

public class BlockBreak implements Listener {
    private MineableOres getMineableOres() {
        return MineableOres.getInstance();
    }
    private FileConfiguration getConfig() {
        return getMineableOres().getConfig();
    }
    public BlockBreak() {
        Bukkit.getPluginManager().registerEvents(this, getMineableOres());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (player.isOp())return;
        if (!getConfig().getStringList("worlds").contains(block.getWorld().getName()))return;
        if (!getMineableOres().getConfig().getBoolean("ores." + block.getType() + ".enable"))return;
        if (!getMineableOres().isAllowedCollect(block))return;
        if (!player.hasPermission("mineableores.event.block-break.collect"))return;
        if (!player.getGameMode().equals(GameMode.SURVIVAL))return;
        ItemStack pickAxe = player.getInventory().getItemInMainHand();
        if (!Tag.ITEMS_PICKAXES.isTagged(pickAxe.getType()))return;
        if (!isOre(block.getType()))return;
        event.setCancelled(true);
        Bukkit.getPluginManager().callEvent(new OreBreakEvent(player, block));
    }
    private boolean isOre(Material material) {
        return Tag.COAL_ORES.isTagged(material) || Tag.COPPER_ORES.isTagged(material)
                || Tag.IRON_ORES.isTagged(material) || Tag.GOLD_ORES.isTagged(material)
                || Tag.REDSTONE_ORES.isTagged(material) || Tag.EMERALD_ORES.isTagged(material)
                || Tag.LAPIS_ORES.isTagged(material) || Tag.DIAMOND_ORES.isTagged(material)
                || material.equals(Material.NETHER_GOLD_ORE) || material.equals(Material.NETHER_QUARTZ_ORE);
    }
}