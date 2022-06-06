package me.xrexy.simpledivisions.commands;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor {
    public final HashMap<String, CommandInterface> commands = new HashMap<>();
    public void register(String name, CommandInterface cmd) {
        commands.put(name, cmd);
    }

    public void registerTabCompleters() {
        commands.forEach((base, subcommands) -> Optional.ofNullable(SimpleDivisions.getInstance().getServer().getPluginCommand(base))
            .ifPresent((command) -> command.setTabCompleter((sender, command1, label, args) ->
                args.length == 1 ? commands.keySet().stream().filter((s) -> s.startsWith(args[0])).collect(Collectors.toList()) : null)));
    }

    public CommandInterface getExecutor(String name) {
        return commands.get(name);
    }

    private final String mainCommand;

    public CommandManager(String mainCommand) {
        this.mainCommand = mainCommand;
    }

    @Override
    @SuppressWarnings("all")
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            if (!sender.hasPermission("simpledivisions.use")) {
                Utils.sendMessage(((Player) sender), "messages.no-permission");
                return true;
            }

            if (args.length == 0) {
                execute(mainCommand, sender, cmd, commandLabel, args);
                return true;
            }

            if (args.length > 0) {
                if (commands.containsKey(args[0])) {
                    if (sender.hasPermission("simpledivisions." + getExecutor(args[0]).getCommand()))
                        execute(args[0], sender, cmd, commandLabel, args);
                    else
                        Utils.sendMessage(((Player) sender), "messages.no-permission");
                } else
                    Utils.sendMessage(((Player) sender), "messages.invalid-args");

                return true;
            }

        } else {
            sender.sendMessage(Utils.process("%prefix% &cOnly players can execute this command!"));
            return true;
        }
        return false;
    }

    private void execute(String command, CommandSender sender, Command cmd, String commandLabel, String[] args) {
        getExecutor(command).onCommand(sender, cmd, commandLabel, args);
    }
}
