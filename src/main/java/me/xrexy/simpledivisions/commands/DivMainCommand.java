package me.xrexy.simpledivisions.commands;

import me.xrexy.simpledivisions.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DivMainCommand implements CommandInterface {
    @Override
    public String getCommand() {
        return "division";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Utils.sendMultilineMessage((Player) sender, "messages.help");
        return true;
    }
}
