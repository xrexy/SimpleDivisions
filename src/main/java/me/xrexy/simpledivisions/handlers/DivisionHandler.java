package me.xrexy.simpledivisions.handlers;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.players.DivPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DivisionHandler {
    private static final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private static Division[] divisions;

    public static Division[] getDivisions() {
        if (divisions == null) {
            loadDivisions();
        }

        return divisions;
    }

    public static void loadDivisions() {
        FileConfiguration divisionsFile = plugin.getFileAPI().getFile("divisions.yml");
        ConfigurationSection section = divisionsFile.getConfigurationSection("divisions");
        Set<String> keys = section.getKeys(false);

        divisions = new Division[keys.size()];

        AtomicInteger i = new AtomicInteger(1);
        divisions[0] = new Division(section.getString("default.display"), section.getInt("default.score"), 0, getSuitSlot(section, "default", 0), new ArrayList<>(), section.getString("default.gui.title"), section.getStringList("default.gui.description"), section.getBoolean("default.gui.show"), section.getString("default.display_chat"));
        AtomicReference<Division> division = new AtomicReference<>();
        keys.forEach(key -> {
            if (!key.equals("default")) {
                division.set(new Division(key, section.getInt(key + ".score"), i.get(), getSuitSlot(section, key, i.get()), section.getStringList(key + ".rewards"), section.getString(key + ".gui.title"), section.getStringList(key + ".gui.description"), section.getBoolean(key + ".gui.show"), section.getString(key + ".display_chat")));
                divisions[i.get()] = division.get();
                InventoryHandler.mapDivision(division.get().getSlot(), division.get());
                i.getAndIncrement();
            }
        });
    }

    public static Division getDivision(int index) {
        if (index >= divisions.length) return divisions[divisions.length - 1];
        return divisions[index];
    }

    public static int recalculateDivision(DivPlayer player) {
        int divisionID = player.getDivisionIndex();

//        if (divisionID > divisions.length - 1) return divisionID;
        if (divisionID < 0) return 0;
//        if (player.getScore() > divisions[divisions.length - 1].getScore()) return divisions.length - 1;

        int playerScore = player.getScore();
        int o = divisionID;

        for (int i = 0; i < divisions.length - 1; i++) {
            o = divisions[i].getScore();
            int m = (o + divisions[i + 1].getScore()) / 2;

            if (Math.abs(playerScore - m) <= (Math.abs(o - m))) {
                o = i;
                break;
            }
        }
        return o;
    }

    private static int getSuitSlot(ConfigurationSection section, String key, int i) {
        try {
            return Integer.parseInt(section.get(key + ".gui.slot").toString());
        } catch (Exception e) {
            return i;
        }
    }
}