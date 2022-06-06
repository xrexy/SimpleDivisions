package me.xrexy.simpledivisions.commands.subcommands;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.commands.CommandInterface;
import me.xrexy.simpledivisions.handlers.Division;
import me.xrexy.simpledivisions.handlers.DivisionHandler;
import me.xrexy.simpledivisions.handlers.InventoryHandler;
import me.xrexy.simpledivisions.players.DivPlayer;
import me.xrexy.simpledivisions.players.PlayerAPI;
import me.xrexy.simpledivisions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ArgRewards implements CommandInterface {
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private final PlayerAPI playerAPI = plugin.getPlayerAPI();
    private final FileConfiguration config = plugin.getConfig();

    private String title;
    private Inventory inventoryTemplate = null;

    public ArgRewards() {
        try {
            int size = plugin.getConfig().getInt("rewards.gui.size");
            title = Utils.colorize(config.getString("rewards.gui.title"));
            inventoryTemplate = Bukkit.createInventory(null, size, title);

            ItemStack item;
            ItemMeta meta;
            item = InventoryHandler.loadGlass(config.getString("rewards.gui.filler"));

            meta = item.getItemMeta();
            meta.setDisplayName(Utils.colorize(config.getString("rewards.gui.filler-name")));
            item.setItemMeta(meta);
            for (int i = 0; i < size; i++) inventoryTemplate.setItem(i, item);
        } catch (Exception e) {
            Utils.info("Couldn't load rewards inventory!");
        }
    }

    @Override
    public String getCommand() {
        return "rewards";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;
        try {
            if (inventoryTemplate == null) {
                Utils.info("Rewards GUI wasn't loaded correctly, please check you config.yml or divisions.yml");
                Utils.sendMessage(player, "message.gui-error");
                return true;
            }
            DivPlayer divPlayer = playerAPI.getPlayer(player);
            if (divPlayer == null) {
                Utils.info("Couldn't load information for player " + player.getName());
                Utils.sendMessage(player, "message.gui-error");
                return true;
            }

            final Inventory inventory = Bukkit.createInventory(player, inventoryTemplate.getSize(), title);
            inventory.setContents(inventoryTemplate.getContents());

            // to-claim > maxDivision is bigger than divisionID and is not in claimed
            // not-reached > maxDivision is smaller than divisionID
            // claimed > already id in player claimed list

            ItemStack item = null;
            ItemMeta meta;
            String status = "";
            for (Division division : DivisionHandler.getDivisions()) {
                if (!division.isShown()) continue;

                if (divPlayer.getClaimed().contains(division.getIndex())) {
                    status = config.getString("messages.status-claimed");
                    item = InventoryHandler.getClaimedItem();
                }
                if (divPlayer.getMaxDivision() >= division.getIndex() && item == null) {
                    status = config.getString("messages.status-available");
                    item = InventoryHandler.getAvailableItem();
                }
                if (item == null) {
                    status = config.getString("messages.status-not-reached");
                    item = InventoryHandler.getNotReachedItem();
                }

                meta = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                String finalStatus = status;
                division.getDescription().forEach(line -> lore.add(Utils.colorize("&f" + line.replace("%status%", finalStatus))));
                meta.setLore(lore);
                meta.setDisplayName(Utils.colorize(division.getTitle()));
                item.setItemMeta(meta);
                inventory.setItem(division.getSlot(), item);
                item = null;
            }

            player.openInventory(inventory);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendMessage(player, "messages.invalid-player");
        }
        return false;
    }
}
