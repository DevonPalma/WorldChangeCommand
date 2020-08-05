package io.github.DevonPalma.wcc;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static io.github.DevonPalma.wcc.ConfigManager.CommandRunnerTypes;
import static io.github.DevonPalma.wcc.ConfigManager.EventTypes;

import java.util.List;

public class EventListener implements Listener {

    public boolean debug = false;

    private WorldChangeCommand plugin;

    public EventListener(WorldChangeCommand plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        runCommands(world, player, EventTypes.onJoin, CommandRunnerTypes.consolePre);
        runCommands(world, player, EventTypes.onJoin, CommandRunnerTypes.player);
        runCommands(world, player, EventTypes.onJoin, CommandRunnerTypes.consolePost);
    }


    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        runCommands(world, player, EventTypes.onLeave, CommandRunnerTypes.consolePre);
        runCommands(world, player, EventTypes.onLeave, CommandRunnerTypes.player);
        runCommands(world, player, EventTypes.onLeave, CommandRunnerTypes.consolePost);
    }


    @EventHandler
    public void worldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World fromWorld = event.getFrom();
        World toWorld = player.getWorld();

        runCommands(fromWorld, player, EventTypes.onLeave, CommandRunnerTypes.consolePre);
        runCommands(toWorld, player, EventTypes.onJoin, CommandRunnerTypes.consolePre);

        runCommands(fromWorld, player, EventTypes.onLeave, CommandRunnerTypes.player);
        runCommands(toWorld, player, EventTypes.onJoin, CommandRunnerTypes.player);

        runCommands(fromWorld, player, EventTypes.onLeave, CommandRunnerTypes.consolePost);
        runCommands(toWorld, player, EventTypes.onJoin, CommandRunnerTypes.consolePost);
    }


    private void runCommands(World world, Player player, EventTypes eventType, CommandRunnerTypes commandRunnerType) {
        List<String> commands = plugin.getConfigManager().getCommands(world.getName(), eventType, commandRunnerType);
        if (commands == null) {
            if (debug)
                plugin.getLogger().warning("Could not find commands for (" +
                    world.getName() + ", " +
                    player.getDisplayName() + ", " +
                    eventType.name() + ", " +
                    commandRunnerType + ")");
            return;
        }

        CommandSender commandSender = null;
        switch (commandRunnerType) {
            case player:
                commandSender = player;
                break;
            case consolePre:
            case consolePost:
                commandSender = Bukkit.getConsoleSender();
                break;
        }

        for (String command : commands) {
            String papiCommand = PlaceholderAPI.setPlaceholders(player, command);
            plugin.getServer().dispatchCommand(commandSender, papiCommand);
        }
    }

}
