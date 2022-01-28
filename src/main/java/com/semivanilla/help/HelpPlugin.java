package com.semivanilla.help;

import com.semivanilla.help.manager.CommandManager;
import com.semivanilla.help.manager.ConfigManager;
import com.semivanilla.help.menus.BookManager;
import lombok.Getter;
import net.badbird5907.blib.bLib;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class HelpPlugin extends JavaPlugin {
    @Getter
    private static HelpPlugin instance;
    @Getter
    private static ConfigManager configManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        bLib.create(this);
        //bLib.getCommandFramework().registerCommandsInPackage("com.semivanilla.help.commands");
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        configManager = new ConfigManager();
        configManager.init();
        for (String command : ConfigManager.getCommands()) {
            CommandManager.getInstance().registerCommand(command);
        }
        BookManager.init();
    }

    private FileConfiguration config = null;

    @Override
    public @NotNull FileConfiguration getConfig() {
        if (config == null) {
            config = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/config.yml"));
        }
        return config;
    }
}
