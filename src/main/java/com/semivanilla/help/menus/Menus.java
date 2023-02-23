package com.semivanilla.help.menus;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public enum Menus {
    MAIN("main");

    Menus(String name) {
        this.name = name;
    }
    private final String name;

    private MenuConfig config;

    public void open(Player player) {
        //System.out.println("Opening menu " + name());
        config.open(player);
    }

    public MenuConfig getConfig() {
        return config;
    }

    public void setConfig(MenuConfig config) {
        this.config = config;
    }
}
