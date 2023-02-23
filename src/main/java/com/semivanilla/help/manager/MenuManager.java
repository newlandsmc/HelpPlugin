package com.semivanilla.help.manager;

import com.semivanilla.help.HelpPlugin;
import com.semivanilla.help.menus.MenuConfig;
import com.semivanilla.help.menus.Menus;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;

public class MenuManager {
    @Getter
    private static final MenuManager instance = new MenuManager();

    //private final Map<Menus, MenuConfig> menuConfigs = new HashMap<>();
    private MenuManager() {

    }

    public void open(Menus menu, Player player) {
        menu.open(player);
    }

    @SneakyThrows
    public void init() {
        HelpPlugin.getInstance().getLogger().info("Loading menus...");
        File folder = new File(HelpPlugin.getInstance().getDataFolder(), "menus");
        for (Menus menu : Menus.values()) {
            HelpPlugin.getInstance().getLogger().info(" - Loading menu: " + menu);
            String menuName = menu.getName();
            File file = new File(folder, menuName + ".json");
            if (!file.exists()) {
                HelpPlugin.getInstance().saveResource("menus/" + menuName + ".json", false);
            }
            String data = new String(Files.readAllBytes(file.toPath()));
            MenuConfig menuConfig = HelpPlugin.getGson().fromJson(data, MenuConfig.class);
            HelpPlugin.getInstance().getLogger().info(" - Loaded menu " + menu + "! ");
            menu.setConfig(menuConfig);
        }

    }
}
