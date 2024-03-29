package com.semivanilla.help.manager;

import com.semivanilla.help.HelpPlugin;
import com.semivanilla.help.manager.ConfigManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class BookManager {
    @Getter
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Getter
    private static Book help;
    /*
            store,
            map,
            vote,
            website,
            discord,
            rules;
     */

    public static void init() {
        help = createBook("Help", HelpPlugin.getInstance().getConfig().getString("book-name", "Help Book"));
        /*
        store = createBook("Store");
        map = createBook("Map");
        vote = createBook("Vote");
        website = createBook("Website");
        discord = createBook("Discord");
        rules = createBook("Rules");
         */
    }

    public static Book getBookByName(String name) {
        switch (name.toLowerCase()) {
            case "help":
                return help;
                /*
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
                 */
            default:
                return null;
        }
    }

    public static Book createBook(String name, String title) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta = meta.title(miniMessage.deserialize(title));
        meta.setAuthor(ConfigManager.getBookAuthor());
        List<List<String>> list;
        try {
            list = (List<List<String>>) ConfigManager.class.getDeclaredMethod("get" + name).invoke(ConfigManager.class);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Bukkit.getLogger().severe("Error while creating book " + name);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        List<StringBuilder> builders = new ArrayList<>();
        for (List<String> strings : list) { //every page
            StringBuilder builder = new StringBuilder();
            for (String string : strings) { //every line
                builder.append(string).append("\n");
            }
            builders.add(builder);
        }
        List<Component> components = new ArrayList<>();
        for (StringBuilder builder : builders) {
            components.add(miniMessage.deserialize(builder.toString()));
        }
        return meta.pages(new ArrayList<>(components));
        //return book;
    }

    public static List<Component> parsePage(List<List<String>> l) {
        if (false) {
            List<Component> components = new ArrayList<>();
            for (List<String> strings : l) {
                Component component = Component.text("");
                for (String string : strings) {
                    component = component.append(Component.text("\n" + string));
                }
                components.add(component);
            }
            return components;
        }
        List<Component> textComponents = new ArrayList<>();
        for (List<String> list : l) { //loop through all pages
            TextComponent component = Component.text("");
            for (String s : list) { // every line in the page
                //parse markdown links eg: [link](https://example.com)
                //ugly code but it works
                if (s.contains("[") && s.contains("]") && s.contains("(") && s.contains(")") && s.contains(".")) {
                    int i = 0;
                    while (i < s.length()) {
                        if (s.charAt(i) == '[') {
                            int j = i + 1;
                            while (j < s.length() && s.charAt(j) != ']') {
                                j++;
                            }
                            if (j < s.length()) {
                                String text = s.substring(i + 1, j);
                                int k = j + 1;
                                while (k < s.length() && s.charAt(k) != ')') {
                                    k++;
                                }
                                if (k < s.length()) {
                                    String link = s.substring(j + 2, k);
                                    component = component.append(Component.text(text).clickEvent(ClickEvent.openUrl(link)));
                                    //System.out.println("Link: " + link);
                                    i = k + 1;
                                }
                            }
                        }
                        if (i < s.length()) {
                            component = component.append(Component.text(s.charAt(i)));
                            i++;
                        } else component = component.append(Component.text("\n"));
                    }
                } else component = component.append(Component.text("\n" + s));
                //component = component.append(Component.text("\n"));
            }
            textComponents.add(component);
        }

        return textComponents;
    }

    public static void giveBook(Book book, Player player) {
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) itemStack.getItemMeta();
        meta.title(book.title());
        meta.author(book.author());
        meta.pages(book.pages());
        itemStack.setItemMeta(meta);
        player.getInventory().addItem(itemStack);
    }
}
