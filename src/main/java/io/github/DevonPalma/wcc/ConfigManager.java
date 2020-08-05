package io.github.DevonPalma.wcc;

import jdk.jfr.Event;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private JavaPlugin plugin;
    private Configuration config;

    public enum EventTypes {
        onJoin,
        onLeave;
    }

    public enum CommandRunnerTypes {
        consolePre,
        player,
        consolePost;
    }

    // worldName, <EventType, <runnerType, commandList>>
    private Map<String, Map<EventTypes, Map<CommandRunnerTypes, List<String>>>> commandMap;


    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reload();
    }

    public void reload() {
        this.plugin.reloadConfig();
        this.config = plugin.getConfig();

        commandMap = new HashMap<>();

        int commandCount = 0;

        for (String worldName : config.getKeys(false)) {
            ConfigurationSection worldConfigSection = config.getConfigurationSection(worldName);
            for (EventTypes eventType : EventTypes.values()) {
                if (!worldConfigSection.contains(eventType.name()))
                    continue;
                ConfigurationSection worldEventConfigSection = worldConfigSection.getConfigurationSection(eventType.name());
                for (CommandRunnerTypes commandRunnerType : CommandRunnerTypes.values()) {
                    if (!worldEventConfigSection.contains(commandRunnerType.name()))
                        continue;

                    List<String> commands = getCommandsGen(worldName, eventType, commandRunnerType);

                    List<String> fileCommands = worldEventConfigSection.getStringList(commandRunnerType.name());
                    for (String fCom : fileCommands) {
                        commands.add(fCom);
                        commandCount += 1;
                    }
                }
            }
        }
        plugin.getLogger().info("Registered " + commandCount + " commands");
    }

    private Map<EventTypes, Map<CommandRunnerTypes, List<String>>> getCommandsGen(String worldName) {
        Map<EventTypes, Map<CommandRunnerTypes, List<String>>> worldCommandMap = commandMap.get(worldName);
        if (worldCommandMap == null) {
            worldCommandMap = new HashMap<>();
            commandMap.put(worldName, worldCommandMap);
        }
        return worldCommandMap;
    }

    private Map<CommandRunnerTypes, List<String>> getCommandsGen(String worldName, EventTypes eventType) {
        Map<EventTypes, Map<CommandRunnerTypes, List<String>>> worldCommandMap = getCommandsGen(worldName);
        Map<CommandRunnerTypes, List<String>> worldEventCommandMap = worldCommandMap.get(eventType);
        if (worldEventCommandMap == null) {
            worldEventCommandMap = new HashMap<>();
            worldCommandMap.put(eventType, worldEventCommandMap);
        }
        return worldEventCommandMap;
    }

    private List<String> getCommandsGen(String worldName, EventTypes eventType, CommandRunnerTypes commandRunnerType) {
        Map<CommandRunnerTypes, List<String>> worldEventCommandMap = getCommandsGen(worldName, eventType);
        List<String> commands = worldEventCommandMap.get(commandRunnerType);
        if (commands == null) {
            commands = new ArrayList<>();
            worldEventCommandMap.put(commandRunnerType, commands);
        }
        return commands;
    }

    public Map<EventTypes, Map<CommandRunnerTypes, List<String>>> getCommands(String worldName) {
        Map<EventTypes, Map<CommandRunnerTypes, List<String>>> worldCommandMap = commandMap.get(worldName);
        return worldCommandMap;
    }

    public Map<CommandRunnerTypes, List<String>> getCommands(String worldName, EventTypes eventType) {
        Map<EventTypes, Map<CommandRunnerTypes, List<String>>> worldCommandMap = getCommands(worldName);
        if (worldCommandMap == null)
            return null;
        Map<CommandRunnerTypes, List<String>> worldEventCommandMap = worldCommandMap.get(eventType);
        return worldEventCommandMap;
    }

    public List<String> getCommands(String worldName, EventTypes eventType, CommandRunnerTypes commandRunnerType) {
        Map<CommandRunnerTypes, List<String>> worldEventCommandMap = getCommands(worldName, eventType);
        if (worldEventCommandMap == null)
            return null;
        List<String> commands = worldEventCommandMap.get(commandRunnerType);
        return commands;
    }
}
