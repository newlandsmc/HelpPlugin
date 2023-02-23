package com.semivanilla.help;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.semivanilla.help.manager.CommandManager;
import com.semivanilla.help.manager.ConfigManager;
import com.semivanilla.help.manager.MenuManager;
import com.semivanilla.help.menus.Action;
import com.semivanilla.help.manager.BookManager;
import com.semivanilla.help.menus.Condition;
import com.semivanilla.help.menus.MenuConfig;
import com.semivanilla.help.util.ItemStackGsonSerializer;
import lombok.Getter;
import net.badbird5907.blib.bLib;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class HelpPlugin extends JavaPlugin {
    @Getter
    private static HelpPlugin instance;
    @Getter
    private static ConfigManager configManager;
    @Getter
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ItemStack.class, new ItemStackGsonSerializer())
            .registerTypeAdapter(Action.class, new Action.ActionSerializer())
            .registerTypeAdapter(Condition.class, new Condition.ConditionSerializer())
            .registerTypeAdapter(MenuConfig.MenuItemConfig.class, new MenuConfig.MenuItemConfig.Serializer())
            .create();
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
        MenuManager.getInstance().init();
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
