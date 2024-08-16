package org.achymake.mineableores.commands;

import org.achymake.mineableores.Message;
import org.achymake.mineableores.MineableOres;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MineableOresCommand implements CommandExecutor, TabCompleter {
    private MineableOres getMineableOres() {
        return MineableOres.getInstance();
    }
    private Message getMessage() {
        return getMineableOres().getMessage();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                getMessage().send(player, "&6" + getMineableOres().getDescription().getName() + " " + getMineableOres().getDescription().getVersion());
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    getMineableOres().reload();
                    getMessage().send(player, "&6MineableOres:&f reloaded");
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 0) {
                getMessage().send(consoleCommandSender, getMineableOres().getDescription().getName() + " " + getMineableOres().getDescription().getVersion());
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    getMineableOres().reload();
                    getMessage().send(consoleCommandSender, "MineableOres: reloaded");
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                commands.add("reload");
            }
        }
        return commands;
    }
}