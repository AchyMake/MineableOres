package org.achymake.mineableores;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public class UpdateChecker {
    private MineableOres getMineableOres() {
        return MineableOres.getInstance();
    }
    private FileConfiguration getConfig() {
        return getMineableOres().getConfig();
    }
    private Message getMessage() {
        return getMineableOres().getMessage();
    }
    public void getUpdate(Player player) {
        if (getConfig().getBoolean("notify-update")) {
            if (player.hasPermission("mineableores.event.join.update")) {
                Bukkit.getScheduler().runTaskLater(getMineableOres(), new Runnable() {
                    @Override
                    public void run() {
                        getLatest((latest) -> {
                            if (!getMineableOres().version().equals(latest)) {
                                getMessage().send(player, getMineableOres().name() + "&6 has new update:");
                                getMessage().send(player, "-&a https://www.spigotmc.org/resources/118944/");
                            }
                        });
                    }
                }, 5);
            }
        }
    }
    public void getUpdate() {
        if (getConfig().getBoolean("notify-update")) {
            Bukkit.getScheduler().runTaskAsynchronously(getMineableOres(), new Runnable() {
                @Override
                public void run() {
                    getLatest((latest) -> {
                        if (!getMineableOres().version().equals(latest)) {
                            getMessage().sendLog(Level.INFO, getMineableOres().name() + " has new update:");
                            getMessage().sendLog(Level.INFO, "- https://www.spigotmc.org/resources/118944/");
                        }
                    });
                }
            });
        }
    }
    public void getLatest(Consumer<String> consumer) {
        try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 118944).openStream()) {
            Scanner scanner = new Scanner(inputStream);
            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
                scanner.close();
            } else {
                inputStream.close();
            }
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
}