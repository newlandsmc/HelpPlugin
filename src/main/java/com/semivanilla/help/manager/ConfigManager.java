package com.semivanilla.help.manager;

import com.semivanilla.help.HelpPlugin;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ConfigManager {
    @Getter
    private static String[] commands;
    @Getter
    private static String menuName,
            storeLink,
            mapLink,
            voteLink,
            websiteLink,
            discordLink,
            rulesLink;

    @Getter
    private static List<List<String>>
            help = new ArrayList<>(),
            store = new ArrayList<>(),
            map = new ArrayList<>(),
            vote = new ArrayList<>(),
            website = new ArrayList<>(),
            discord = new ArrayList<>(),
            rules = new ArrayList<>();


    public void init() {
        HelpPlugin plugin = HelpPlugin.getInstance();
        if (!new File(plugin.getDataFolder() + "/config.yml").exists()) {
            plugin.saveDefaultConfig();
        }
        loadConfig();
    }

    public void loadConfig() {
        storeLink = getConfig().getString("links.store");
        mapLink = getConfig().getString("links.map");
        voteLink = getConfig().getString("links.vote");
        websiteLink = getConfig().getString("links.website");
        discordLink = getConfig().getString("links.discord");
        rulesLink = getConfig().getString("links.rules");

        commands = getConfig().getStringList("commands").toArray(new String[0]);
        menuName = getConfig().getString("menu-name");
        help = getBook("help");
        store = getBook("store");
        map = getBook("map");
        vote = getBook("vote");
        website = getBook("website");
        discord = getBook("discord");
        rules = getBook("rules");
    }

    public List<List<String>> getBook(String name) {
        List<List<String>> a = new ArrayList<>();
        /*
        int pages = getConfig().getInt("book." + name + ".pages");
        for (int i = 0; i < pages; i++) {
            List<String> list = new ArrayList<>();
            for (String s : getConfig().getStringList("book." + name + "." + i)) {
                list.add(s
                        .replace("%store-link%", storeLink)
                        .replace("%map-link%", mapLink)
                        .replace("%vote-link%", voteLink)
                        .replace("%website-link%", websiteLink)
                        .replace("%discord-link%", discordLink)
                        .replace("%rules-link%", rulesLink));
            }
            a.add(list);
        }
         */
        ConfigurationSection section = getConfig().getConfigurationSection("book." + name);
        for (String key : section.getKeys(false)) {
            List<String> list = new ArrayList<>();
            for (String s : getConfig().getStringList("book." + name + "." + key)) {
                list.add(s
                        .replace("%store-link%", storeLink)
                        .replace("%map-link%", mapLink)
                        .replace("%vote-link%", voteLink)
                        .replace("%website-link%", websiteLink)
                        .replace("%discord-link%", discordLink)
                        .replace("%rules-link%", rulesLink));
            }
            a.add(list);
        }
        return a;
    }

    public FileConfiguration getConfig() {
        return HelpPlugin.getInstance().getConfig();
    }
}
