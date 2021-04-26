package me.xrexy.simpledivisions.commands.subcommands;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.commands.CommandInterface;
import me.xrexy.simpledivisions.commands.CommandUtils;
import me.xrexy.simpledivisions.handlers.DivisionHandler;
import me.xrexy.simpledivisions.players.DivPlayer;
import me.xrexy.simpledivisions.players.PlayerAPI;
import me.xrexy.simpledivisions.utils.Utils;
import me.xrexy.simpledivisions.utils.mysql.MySQLHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ArgGive implements CommandInterface {
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private final MySQLHandler sqlHandler = plugin.getSqlHandler();
    private final PlayerAPI playerAPI = plugin.getPlayerAPI();

    @Override
    public String getCommand() {
        return "give";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;
        try {
            if (args.length < 3) {
                Utils.sendMessage(player, "messages.invalid-args");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            int toAdd = Integer.parseInt(args[2]);

            if (target == null) {
                DivPlayer divPlayer = CommandUtils.loadDivPlayerFromName(args[1]);

                if (divPlayer != null) {
                    divPlayer.addScore(toAdd);
                    divPlayer.setDivisionIndex(DivisionHandler.recalculateDivision(divPlayer));
                    sqlHandler.savePlayerInDatabase(divPlayer);
                    Utils.sendRaw(player, processMessage(player, divPlayer, toAdd));
                } else {
                    Utils.sendMessage(player, "messages.invalid-player");
                }
                return true;
            }
            DivPlayer divPlayer = playerAPI.getPlayer(target);
            if (divPlayer == null) {
                Utils.sendMessage(player, "messages.invalid-player");
                return true;
            }

            divPlayer.addScore(toAdd);

            int divAfter = DivisionHandler.recalculateDivision(divPlayer);
            if (divAfter > divPlayer.getMaxDivision()) divPlayer.setMaxDivision(divAfter);

            divPlayer.setDivisionIndex(divAfter);

            playerAPI.savePlayer(divPlayer);

            Utils.sendRaw(player, processMessage(player, divPlayer, toAdd));

            if (target.isOnline() && plugin.getConfig().getBoolean("messages.give.notify-toggle")) {
                Utils.sendRaw(target, processMessage(player, divPlayer, toAdd, "messages.give.notify"));
            }
        } catch (NumberFormatException e) {
            Utils.sendMessage(player, "messages.give.invalid-number");
        } catch (Exception e) {
            Utils.sendMessage(player, "messages.invalid-player");
        }
        return true;
    }

    private String processMessage(Player sender, DivPlayer target, int toAdd) {
        return processMessage(sender, target, toAdd, "messages.give.msg");
    }

    private String processMessage(Player sender, DivPlayer target, int toAdd, String path) {
        Player target_ = Bukkit.getPlayer(UUID.fromString(target.getUuid()));
        return Utils.process(Utils.getString(path), target)
                .replace("%added%", toAdd + "")
                .replace("%target%", target_ == null ? "" : target_.getName())
                .replace("%sender%", sender.getName());
    }
}
