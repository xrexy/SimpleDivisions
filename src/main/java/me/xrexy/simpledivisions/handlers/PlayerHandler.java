package me.xrexy.simpledivisions.handlers;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.players.DivPlayer;
import me.xrexy.simpledivisions.players.PlayerAPI;
import me.xrexy.simpledivisions.utils.Utils;
import me.xrexy.simpledivisions.utils.mysql.MySQLHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerHandler implements Listener {
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private final MySQLHandler mySQLHandler = plugin.getSqlHandler();
    private final PlayerAPI playerAPI = plugin.getPlayerAPI();

    @EventHandler
    void join(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.hasPlayedBefore()) {
            DivPlayer divPlayer = mySQLHandler.loadPlayer(player);
            if (divPlayer == null) {
                divPlayer = new DivPlayer(0, player.getName(), 0, player.getUniqueId().toString(), 0, new ArrayList<>());
            }
            playerAPI.savePlayer(divPlayer);
            return;
        }
        playerAPI.savePlayer(new DivPlayer(0, player.getName(), 0, player.getUniqueId().toString(), 0, new ArrayList<>()));
    }

    @EventHandler
    void quit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        mySQLHandler.savePlayerInDatabase(playerAPI.getPlayer(player));
        playerAPI.removePlayer(player);
    }

    @EventHandler
    void kill(PlayerDeathEvent e) {
        Player _killed = e.getEntity();
        DivPlayer killed = playerAPI.getPlayer(_killed);
        if (killed == null) {
            Utils.log(Level.SEVERE, "Couldn't load information for killed player " + _killed.getName());
            return;
        }
        DivPlayer killer = playerAPI.getPlayer(_killed.getKiller());
        if (killer == null) {
            Utils.log(Level.SEVERE, "Couldn't load information for killer " + _killed.getKiller().getName());
            return;
        }

        killer.addScore(PlayerAPI.killXP);
        handleNewScore(killer);

        killed.removeScore(PlayerAPI.deathPenalty);
        handleNewScore(killed);
    }

    private void handleNewScore(DivPlayer player) {
        int divID = player.getDivisionIndex();
        int divAfter = DivisionHandler.recalculateDivision(player);
        player.setDivisionIndex(divAfter);

        Player player_ = Bukkit.getPlayer(UUID.fromString(player.getUuid()));

        if (player_ != null) {
            if (divID < divAfter) {
                Utils.sendMessage(player_, player, "messages.rankup");
            } else if (divID > divAfter) {
                Utils.sendMessage(player_, player, "messages.rankdown");
            }
        }
        if (divAfter > player.getMaxDivision()) player.setMaxDivision(divAfter);
    }
}
