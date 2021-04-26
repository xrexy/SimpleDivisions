package me.xrexy.simpledivisions.commands.subcommands;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.commands.CommandInterface;
import me.xrexy.simpledivisions.players.DivPlayer;
import me.xrexy.simpledivisions.players.PlayerAPI;
import me.xrexy.simpledivisions.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgBal implements CommandInterface {
    private final PlayerAPI playerAPI = SimpleDivisions.getInstance().getPlayerAPI();;
    @Override
    public String getCommand() {
        return "me";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        DivPlayer player = playerAPI.getPlayer((Player) sender);
        Utils.sendMessage((Player) sender, player, "messages.me");
        return true;
    }
}
