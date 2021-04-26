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

import java.util.ArrayList;

public class ArgReset implements CommandInterface {
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private final MySQLHandler sqlHandler = plugin.getSqlHandler();
    private final PlayerAPI playerAPI = plugin.getPlayerAPI();

    @Override
    public String getCommand() {
        return "reset";
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

            DivPlayer divPlayer;
            if (target == null) { // player is offline
                divPlayer = CommandUtils.loadDivPlayerFromName(args[1]); // tries to get it as offline player, if not checks database
                if (divPlayer == null) {
                    Utils.sendMessage(player, "messages.invalid-player");
                    return true;
                }
                reset(divPlayer);
                sqlHandler.savePlayerInDatabase(divPlayer);
                Utils.sendMessage(player, divPlayer, "messages.reset");
                return true;
            }
            divPlayer = playerAPI.getPlayer(target);
            if (divPlayer == null) {
                Utils.sendMessage(player, "messages.invalid-player");
                return true;
            }

            reset(divPlayer);
            playerAPI.savePlayer(divPlayer);
            Utils.sendMessage(player, divPlayer, "messages.reset");
        } catch (Exception e) {
            Utils.sendMessage(player, "messages.invalid-player");
        }
        return true;
    }

    private void reset(DivPlayer player) {
        player.setDivisionIndex(0);
        player.setScore(0);
        player.setMaxDivision(0);
        player.setClaimed(new ArrayList<>());
    }
}
