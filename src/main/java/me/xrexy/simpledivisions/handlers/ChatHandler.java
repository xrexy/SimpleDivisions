package me.xrexy.simpledivisions.handlers;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.players.DivPlayer;
import me.xrexy.simpledivisions.players.PlayerAPI;
import me.xrexy.simpledivisions.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatHandler implements Listener {
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private final PlayerAPI playerAPI = plugin.getPlayerAPI();

    @EventHandler
    void chat(AsyncPlayerChatEvent e) {
        DivPlayer divPlayer = playerAPI.getPlayer(e.getPlayer());
        String division;
        if (divPlayer == null) division = plugin.getConfig().getString("messages.unknown-division");
        else division = DivisionHandler.getDivision(divPlayer.getDivisionIndex()).getDisplayChat();

        e.setFormat(e.getFormat().replace("{division}", Utils.colorize(division)));
    }
}
