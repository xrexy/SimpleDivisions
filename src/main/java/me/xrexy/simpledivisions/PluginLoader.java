package me.xrexy.simpledivisions;

import me.xrexy.simpledivisions.commands.CommandManager;
import me.xrexy.simpledivisions.commands.DivMainCommand;
import me.xrexy.simpledivisions.commands.subcommands.*;
import me.xrexy.simpledivisions.files.FileAPI;
import me.xrexy.simpledivisions.handlers.ChatHandler;
import me.xrexy.simpledivisions.handlers.DivisionHandler;
import me.xrexy.simpledivisions.handlers.InventoryHandler;
import me.xrexy.simpledivisions.handlers.PlayerHandler;
import me.xrexy.simpledivisions.players.PlayerAPI;
import me.xrexy.simpledivisions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Level;

public class PluginLoader {
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private final PluginManager pluginManager = Bukkit.getPluginManager();
    private final FileAPI fileAPI = plugin.getFileAPI();

    public void load() {
        loadFiles();
        plugin.getSqlHandler().updateSettings().startSQLConnection();
        if (!plugin.getSqlHandler().isOnline) {
            Utils.log(Level.SEVERE, "Couldn't connect to database, disabling plugin...");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }

        loadCommands();
        pluginManager.registerEvents(new PlayerHandler(), plugin);
        pluginManager.registerEvents(new InventoryHandler(), plugin);
        pluginManager.registerEvents(new ChatHandler(), plugin);
        DivisionHandler.loadDivisions();

        PlayerAPI.killXP = plugin.getConfig().getInt("kill_xp");
        PlayerAPI.deathPenalty = plugin.getConfig().getInt("death_penalty");

        Bukkit.getOnlinePlayers().forEach(player -> plugin.getPlayerAPI().savePlayer(plugin.getSqlHandler().loadPlayer(player)));

        plugin.getSqlHandler().updateTopPlayersAndStartTimer();
    }

    void loadCommands() {
        final DivMainCommand divisionsCommand = new DivMainCommand();
        final PluginCommand divisionsPluginCommand = plugin.getCommand(divisionsCommand.getCommand());
        final CommandManager divisionsCommandManager = new CommandManager(divisionsCommand.getCommand());

        if (divisionsPluginCommand == null) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled, couldn't load division command!", plugin.getDescription().getName()));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        divisionsPluginCommand.setExecutor(divisionsCommandManager);
        // TODO autocompleter

        divisionsCommandManager.register(divisionsCommand.getCommand(), divisionsCommand);

        ArgCheck check = new ArgCheck();
        divisionsCommandManager.register(check.getCommand(), check);

        ArgBal bal = new ArgBal();
        divisionsCommandManager.register(bal.getCommand(), bal);

        ArgGive give = new ArgGive();
        divisionsCommandManager.register(give.getCommand(), give);

        ArgReset reset = new ArgReset();
        divisionsCommandManager.register(reset.getCommand(), reset);

        ArgRewards rewards = new ArgRewards();
        divisionsCommandManager.register(rewards.getCommand(), rewards);

        ArgTop top = new ArgTop();
        divisionsCommandManager.register(top.getCommand(), top);

        ArgUpdatetop updatetop = new ArgUpdatetop();
        divisionsCommandManager.register(updatetop.getCommand(), updatetop);

        ArgInfo info = new ArgInfo();
        divisionsCommandManager.register(info.getCommand(), info);

        divisionsCommandManager.registerTabCompleters();
    }

    void loadFiles() {
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();

        fileAPI.loadFile("divisions.yml", true);
    }
}
