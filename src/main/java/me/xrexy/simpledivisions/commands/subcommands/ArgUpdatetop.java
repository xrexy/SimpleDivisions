package me.xrexy.simpledivisions.commands.subcommands;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.commands.CommandInterface;
import me.xrexy.simpledivisions.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgUpdatetop implements CommandInterface {
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    @Override
    public String getCommand() {
        return "updatetop";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Utils.sendMessage((Player) sender, "messages.updatetop");
        plugin.getSqlHandler().updateTopPlayersAndStartTimer();
        return true;
    }
}
