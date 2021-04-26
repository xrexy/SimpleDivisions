package me.xrexy.simpledivisions.commands.subcommands;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.commands.CommandInterface;
import me.xrexy.simpledivisions.commands.CommandUtils;
import me.xrexy.simpledivisions.players.DivPlayer;
import me.xrexy.simpledivisions.players.PlayerAPI;
import me.xrexy.simpledivisions.utils.Utils;
import me.xrexy.simpledivisions.utils.mysql.MySQLHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgCheck implements CommandInterface {
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private final MySQLHandler sqlHandler = plugin.getSqlHandler();
    private final PlayerAPI playerAPI = plugin.getPlayerAPI();

    @Override
    public String getCommand() {
        return "check";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;
        try {
            if (args.length < 2) {
                Utils.sendMessage(player, "messages.invalid-args");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);

            if (target == null) {
                DivPlayer divPlayer = CommandUtils.loadDivPlayerFromName(args[1]);

                if (divPlayer != null) Utils.sendMessage(player, divPlayer, "messages.check");
                else Utils.sendMessage(player, "messages.invalid-player");

                return true;
            }
            DivPlayer divPlayer = playerAPI.getPlayer(target);
            if (divPlayer == null) {
                Utils.sendMessage(player, "messages.invalid-player");
                return true;
            }

            Utils.sendMessage(player, divPlayer, "messages.check");
        } catch (Exception e) {
            Utils.sendMessage(player, "messages.invalid-player");
        }

        return true;
    }
}
