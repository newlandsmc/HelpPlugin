package com.semivanilla.help.manager;

import com.semivanilla.help.HelpPlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ConfigManager {
    @Getter
    private static String[] guiCommands;
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
            help = new ArrayList<>();
    /*
            store = new ArrayList<>(),
            map = new ArrayList<>(),
            vote = new ArrayList<>(),
            website = new ArrayList<>(),
            discord = new ArrayList<>(),
            rules = new ArrayList<>();
     */

    @Getter
    private static List<Component> store = new ArrayList<>(),
            map = new ArrayList<>(),
            vote = new ArrayList<>(),
            website = new ArrayList<>(),
            discord = new ArrayList<>(),
            rules = new ArrayList<>();

    @Getter // Command, Book Name
    private static Map<String, String> commandBookMap = new HashMap<>();
    @Getter
    private static String bookAuthor;


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

        guiCommands = getConfig().getStringList("commands.gui").toArray(new String[0]);
        ConfigurationSection booksSection = getConfig().getConfigurationSection("commands.books");
        for (String key : booksSection.getKeys(false)) {
            for (String command : booksSection.getStringList(key)) {
                commandBookMap.put(command, key);
            }
        }
        menuName = getConfig().getString("menu-name");
        help = getBook("help");
        bookAuthor = getConfig().getString("book-author");
        /*
        store = getBook("store");
        map = getBook("map");
        vote = getBook("vote");
        website = getBook("website");
        discord = getBook("discord");
        rules = getBook("rules");
         */
        store = getComponentList("store");
        map = getComponentList("map");
        vote = getComponentList("vote");
        website = getComponentList("website");
        discord = getComponentList("discord");
        rules = getComponentList("rules");
    }
    public List<Component> getComponentList(String name) {
        List<Component> list = new ArrayList<>();
        ConfigurationSection section = getConfig().getConfigurationSection("book." + name);
        for (String key : section.getKeys(false)) {
            List<String> stringList = section.getStringList(key);
            for (String s : stringList) {
                list.add(BookManager.getMiniMessage().deserialize(s
                        .replace("%store-link%", storeLink)
                        .replace("%map-link%", mapLink)
                        .replace("%vote-link%", voteLink)
                        .replace("%website-link%", websiteLink)
                        .replace("%discord-link%", discordLink)
                        .replace("%rules-link%", rulesLink)));
            }
        }
        return list;
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
            // System.out.println(key);
            if (section.isString(key) || section.isInt(key)) continue;
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
    public static List<Component> getComponentsByName(String name) {
        if (name == null) return null;
        switch (name.toLowerCase()) {
            case "store":
                return store;
            case "map":
                return map;
            case "vote":
                return vote;
            case "website":
                return website;
            case "discord":
                return discord;
            case "rules":
                return rules;
            default:
                return null;
        }
    }
    public FileConfiguration getConfig() {
        return HelpPlugin.getInstance().getConfig();
    }
}
