package org.achymake.mineableores.listeners;

import org.achymake.mineableores.MineableOres;
import org.achymake.mineableores.listeners.custom.OreBreakEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Random;

public class OreBreak implements Listener {
    private MineableOres getMineableOres() {
        return MineableOres.getInstance();
    }
    private FileConfiguration getConfig() {
        return getMineableOres().getConfig();
    }
    public OreBreak() {
        Bukkit.getPluginManager().registerEvents(this, getMineableOres());
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onOreBreak(OreBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack pickAxe = player.getInventory().getItemInMainHand();
        if (!Tag.ITEMS_PICKAXES.isTagged(pickAxe.getType()))return;
        Location location = block.getLocation();
        Material material = block.getType();
        getMineableOres().getOres().put(location, material);
        Bukkit.getServer().getScheduler().runTaskLater(MineableOres.getInstance(), new Runnable() {
            @Override
            public void run() {
                block.setType(material);
                getMineableOres().getOres().remove(location);
            }
        }, getConfig().getInt("ores." + block.getType() + ".replace-default-in") * 20);
        if (isDeepslateOre(material)) {
            block.setType(Material.COBBLED_DEEPSLATE);
        } else if (isNetherOre(material)) {
            block.setType(Material.NETHERRACK);
        } else {
            block.setType(Material.COBBLESTONE);
        }
        if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
            player.getInventory().addItem(getItem(pickAxe, material));
        } else {
            player.getWorld().dropItem(player.getLocation(), getItem(pickAxe, material));
        }
    }
    private ItemStack getItem(ItemStack pickAxe, Material material) {
        Material givingMaterial = Material.valueOf(getConfig().getString("ores." + material + ".item-giving.type"));
        int min = getConfig().getInt("ores." + material + ".item-giving.min");
        int max = getConfig().getInt("ores." + material + ".item-giving.max");
        Random random = new Random();
        ItemStack itemStack = new ItemStack(givingMaterial, 1);
        if (max == 1) {
            itemStack.setAmount(1);
        } else {
            int result = random.nextInt(min, max);
            double chance = random.nextDouble(0, 1);
            if (chance >= 0.70) {
                itemStack.setAmount(result);
            } else {
                itemStack.setAmount(min);
            }
        }
        if (pickAxe.containsEnchantment(Enchantment.FORTUNE)) {
            int chanceExtra = random.nextInt(0, pickAxe.getEnchantmentLevel(Enchantment.FORTUNE));
            double chance = random.nextDouble(0, 1);
            int result = itemStack.getAmount() + chanceExtra;
            if (chance >= 0.70) {
                itemStack.setAmount(result);
            } else if (chance >= 0.20) {
                if (result > 1) {
                    itemStack.setAmount(result - 1);
                } else {
                    itemStack.setAmount(result);
                }
            } else {
                itemStack.setAmount(min);
            }
        }
        return itemStack;
    }
    private boolean isDeepslateOre(Material material) {
        return material.toString().contains("DEEPSLATE_");
    }
    private boolean isNetherOre(Material material) {
        return material.toString().contains("NETHER_");
    }
}
