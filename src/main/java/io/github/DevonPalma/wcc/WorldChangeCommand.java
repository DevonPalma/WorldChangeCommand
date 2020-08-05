package io.github.DevonPalma.wcc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class WorldChangeCommand extends JavaPlugin {

    private ConfigManager configManager;
    private EventListener eventListener;


    @Override
    public void onEnable() {
        super.onEnable();
        configManager = new ConfigManager(this);
        eventListener = new EventListener(this);
        this.getCommand("worldchangecommand").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getLabel().equals("worldchangecommand")) {
            if (sender instanceof Player){
                Player player = (Player) sender;
                if (!player.hasPermission("worldchangecommand.admin")) {
                    return false;
                }
            }
            if (args.length == 0){
                sender.sendMessage("Welcome to version 1.0 of WorldChangeCommand");
                return true;
            }
            if (args.length == 1) {
                if (args[0].equals("reload")) {
                    if (!(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage("Reloading Command Manager Config");
                    }
                    getLogger().info("Reloading Command Manager Config");
                    configManager.reload();
                    return true;
                }
                if (args[0].equals("debug")) {
                    String message = "Toggling debug mode " + (eventListener.debug ? "off" : "on");
                    if (!(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(message);
                    }
                    getLogger().info(message);
                    eventListener.debug = !eventListener.debug;

                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> returnVals = new ArrayList<>();
        if (command.getLabel().equals("worldchangecommand")) {
            if (sender instanceof Player){
                Player player = (Player) sender;
                if (!player.hasPermission("worldchangecommand.admin")) {
                    return returnVals;
                }
            }
            if (args.length == 1) {
                if ("reload".startsWith(args[0]))
                    returnVals.add("reload");
                if ("debug".startsWith(args[0]))
                    returnVals.add("debug");
            }
        }
        return returnVals;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
