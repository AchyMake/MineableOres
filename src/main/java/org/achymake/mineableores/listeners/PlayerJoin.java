package org.achymake.mineableores.listeners;

import org.achymake.mineableores.MineableOres;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    private MineableOres getMineableOres() {
        return MineableOres.getInstance();
    }
    public PlayerJoin() {
        Bukkit.getPluginManager().registerEvents(this, getMineableOres());
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        getMineableOres().getUpdateChecker().getUpdate(event.getPlayer());
    }
}