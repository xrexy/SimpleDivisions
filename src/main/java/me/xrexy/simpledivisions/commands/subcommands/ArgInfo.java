package me.xrexy.simpledivisions.commands.subcommands;

import me.xrexy.simpledivisions.commands.CommandInterface;
import me.xrexy.simpledivisions.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgInfo implements CommandInterface {
    @Override
    public String getCommand() {
        return "info";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Utils.sendMultilineMessage((Player) sender, "messages.info");
        return true;
    }
}
