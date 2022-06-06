package me.xrexy.simpledivisions.handlers;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.players.DivPlayer;
import me.xrexy.simpledivisions.players.PlayerAPI;
import me.xrexy.simpledivisions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryHandler implements Listener {
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private final FileConfiguration config = plugin.getConfig();
    private final PlayerAPI playerAPI = plugin.getPlayerAPI();
    private static final FileConfiguration configStatic = SimpleDivisions.getInstance().getConfig();
    private static final HashMap<Integer, Division> mappedDivision = new HashMap<>();

    @EventHandler
    void click(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();
        if (inventory != null && e.getView().getTitle().equals(Utils.colorize(config.getString("rewards.gui.title")))) {
            e.setCancelled(true);
            Player clickedPlayer = (Player) e.getWhoClicked();
            DivPlayer player = playerAPI.getPlayer(clickedPlayer);
            if (player == null) return;

            int slot = e.getRawSlot();
            if (slot < config.getInt("rewards.gui.size")) {
                Division clicked = mappedDivision.get(slot);
                if (clicked == null) return;

                if (player.getMaxDivision() >= clicked.getIndex() && !player.getClaimed().contains(clicked.getIndex())) {
                    player.getClaimed().add(clicked.getIndex());

                    clicked.getRewards().forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", clickedPlayer.getName())));

                    e.getWhoClicked().closeInventory();
                }
            }
        }
    }

    public static void mapDivision(int slot, Division division) {
        if(!division.isShown()) return;
        mappedDivision.put(slot, division);
    }

    public static ItemStack getClaimedItem() {
        return loadGlass(configStatic.getString("rewards.gui.claimed"));
    }

    public static ItemStack getAvailableItem() {
        return loadGlass(configStatic.getString("rewards.gui.available"));
    }

    public static ItemStack getNotReachedItem() {
        return loadGlass(configStatic.getString("rewards.gui.not-reached"));
    }

    public static ItemStack loadGlass(String input) {
        String[] arr = input.split(":");
        try {
            if (arr.length > 1) return new ItemStack(Material.valueOf(arr[0]), 1, (short) Integer.parseInt(arr[1]));
            else return new ItemStack(Material.valueOf(arr[0]));  // no id provided
        } catch (Exception e) {
            return new ItemStack(Material.valueOf(arr[0]));
        }
    }
}
