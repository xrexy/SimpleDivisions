package me.xrexy.simpledivisions.utils;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.handlers.DivisionHandler;
import me.xrexy.simpledivisions.players.DivPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Level;

public class Utils {
    private static final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private static final FileConfiguration config = plugin.getConfig();

    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void sendRaw(Player player, String msg) {
        player.sendMessage(msg);
    }

    public static void log(Level level, String msg) {
        plugin.getLogger().log(level, colorize(msg));
    }

    public static void info(String msg) {
        log(Level.INFO, msg);
    }

    public static void sendMultilineMessage(Player p, String path) {
        StringBuilder message = new StringBuilder();
        for (String s : config.getStringList(path)) {
            message.append(colorize(process(s))).append("\n");
        }
        p.sendMessage(message.toString());
    }

    public static String process(String toProcess) {
        return colorize(toProcess.replace("%prefix%", getString("prefix")));
    }

    public static String getString(String path) {
        return config.getString(path);
    }

    public static void sendMessage(Player p, String path) {
        p.sendMessage(process(getString(path)));
    }

    public static void sendMessage(Player p, DivPlayer target, String path) {
        p.sendMessage(process(getString(path), target));
    }

    public static String process(String toProcess, DivPlayer target) {
        OfflinePlayer targetAsPlayer = Bukkit.getOfflinePlayer(UUID.fromString(target.getUuid()));
        return colorize(toProcess
                .replace("%prefix%", getString("prefix"))
                .replace("%score%", target.getScore() + "")
                .replace("%division%", DivisionHandler.getDivision(target.getDivisionIndex()).getDisplayName())
                .replace("%target%", targetAsPlayer == null ? "" : targetAsPlayer.getName())
        );
    }


    public static void exception(Exception e, Level level, String msg) {
        if (config.getBoolean("debug")) e.printStackTrace();
        Utils.log(level, msg);
    }
}
