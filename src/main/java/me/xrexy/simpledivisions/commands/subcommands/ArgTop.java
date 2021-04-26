package me.xrexy.simpledivisions.commands.subcommands;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.commands.CommandInterface;
import me.xrexy.simpledivisions.utils.Utils;
import me.xrexy.simpledivisions.utils.mysql.DivPlayerDummy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class ArgTop implements CommandInterface {
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private final FileConfiguration config = plugin.getConfig();

    @Override
    public String getCommand() {
        return "top";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            StringBuilder message = new StringBuilder();
            String format = config.getString("messages.top-player-format");

            config.getStringList("messages.top").forEach(line -> {
                message.append(line).append("\n");
            });

            for (DivPlayerDummy player : plugin.getSqlHandler().getTopPlayers()) {
                if (player == null) continue;

                message.append(format
                        .replace("%username%", player.getUsername())
                        .replace("%score%", player.getScore() + "")
                        .replace("%place%", (player.getIndex() + 1) + ""))
                        .append("\n");
            }

            sender.sendMessage(Utils.colorize(message.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
