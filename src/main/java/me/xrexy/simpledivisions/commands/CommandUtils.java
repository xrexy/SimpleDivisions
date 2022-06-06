package me.xrexy.simpledivisions.commands;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.players.DivPlayer;
import me.xrexy.simpledivisions.utils.mysql.MySQLHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;

public class CommandUtils {
    private static final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private static final MySQLHandler sqlHandler = plugin.getSqlHandler();

    @Nullable
    @SuppressWarnings("deprecation")
    public static DivPlayer loadDivPlayerFromName(String playerName) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (offlinePlayer == null) return null;

        return sqlHandler.loadPlayer(offlinePlayer);
    }
}
