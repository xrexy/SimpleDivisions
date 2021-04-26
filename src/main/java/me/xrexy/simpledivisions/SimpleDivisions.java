package me.xrexy.simpledivisions;

import me.xrexy.simpledivisions.files.FileAPI;
import me.xrexy.simpledivisions.players.PlayerAPI;
import me.xrexy.simpledivisions.utils.mysql.MySQLHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleDivisions extends JavaPlugin {
    private static SimpleDivisions instance;

    private MySQLHandler sqlHandler;
    private PlayerAPI playerAPI;
    private FileAPI fileAPI;

    @Override
    public void onEnable() {
        instance = this;
        sqlHandler = new MySQLHandler();
        playerAPI = new PlayerAPI();
        fileAPI = new FileAPI();
        new PluginLoader().load();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            sqlHandler.savePlayerInDatabase(playerAPI.getPlayer(player));
        });

        sqlHandler.closeConnection();
    }

    public FileAPI getFileAPI() {
        return fileAPI;
    }

    public PlayerAPI getPlayerAPI() {
        return playerAPI;
    }

    public MySQLHandler getSqlHandler() {
        return sqlHandler;
    }

    public static SimpleDivisions getInstance() {
        return instance;
    }
}
