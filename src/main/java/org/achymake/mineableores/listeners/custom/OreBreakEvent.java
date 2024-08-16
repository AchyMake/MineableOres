package org.achymake.mineableores.listeners.custom;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class OreBreakEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Block block;
    public OreBreakEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }
    public Player getPlayer() {
        return player;
    }
    public Block getBlock() {
        return block;
    }
    public @Nonnull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}