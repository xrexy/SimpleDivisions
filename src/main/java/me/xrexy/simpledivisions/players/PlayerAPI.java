package me.xrexy.simpledivisions.players;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.utils.mysql.MySQLHandler;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerAPI {
    public final HashMap<String, DivPlayer> onlinePlayers = new HashMap<>();
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private final MySQLHandler sqlHandler = plugin.getSqlHandler();

    public static int killXP;
    public static int deathPenalty;

    @Nullable
    public DivPlayer getPlayer(Player player) {
        DivPlayer output = onlinePlayers.get(player.getUniqueId().toString());
        if(output == null) { // not saved as online player
            output = sqlHandler.loadPlayer(player); // loads from database
            if(output == null) { // jesus fucking christ... how did we get here (player isnt in database too??)
                // adding the player to the db.
                output = new DivPlayer(0, player.getName(), 0, player.getUniqueId().toString(), 0, new ArrayList<>());
                sqlHandler.savePlayerInDatabase(output);
                onlinePlayers.put(player.getUniqueId().toString(), output);
            }
        }
        return output;
    }

    public void savePlayer(DivPlayer player) {
        onlinePlayers.put(player.getUuid(), player);
    }

    public void removePlayer(Player player) { onlinePlayers.remove(player.getUniqueId().toString()); }
}
