package com.semivanilla.help.manager;

import com.semivanilla.help.HelpPlugin;
import com.semivanilla.help.menus.Menus;
import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.blib.command.BukkitCommand;
import net.badbird5907.blib.util.CC;
import net.kyori.adventure.inventory.Book;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class CommandManager implements CommandExecutor {
    private CommandMap map;

    @Getter
    private static final CommandManager instance = new CommandManager();

    public CommandManager() {
        if (Bukkit.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) Bukkit.getServer().getPluginManager();
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                map = (CommandMap) field.get(manager);
            } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public void registerCommand(String command) {
        Constructor<BukkitCommand> cons = BukkitCommand.class.getDeclaredConstructor(String.class, CommandExecutor.class, Plugin.class);
        cons.setAccessible(true);
        org.bukkit.command.Command cmd = cons.newInstance(command, this, HelpPlugin.getInstance());
        map.register(HelpPlugin.getInstance().getName(), cmd);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!label.isEmpty()) {
                if (ConfigManager.getCommandBookMap().containsKey(label)) {
                    player.closeInventory();
                    Book book = BookManager.getBookByName(ConfigManager.getCommandBookMap().get(label));
                    if (book == null) {
                        player.sendMessage(CC.RED + "Book not found!");
                        return true;
                    }
                    player.closeInventory();
                    player.openBook(book);
                    return true;
                }
            }
            if (args.length == 0) {
                Menus.MAIN.open(player);
                return true;
            }
            String action = args[0].toLowerCase();
            switch (action) {
                case "help": {
                    player.closeInventory();
                    //player.openBook(BookManager.getHelp());
                    BookManager.giveBook(BookManager.getHelp(), player);
                    return true;
                }
                /*
                case "store": {
                    player.closeInventory();
                    player.openBook(BookManager.getStore());
                    return true;
                }
                case "map": {
                    player.closeInventory();
                    player.openBook(BookManager.getMap());
                    return true;
                }
                case "vote": {
                    player.closeInventory();
                    player.openBook(BookManager.getVote());
                    return true;
                }
                case "website": {
                    player.closeInventory();
                    player.openBook(BookManager.getWebsite());
                    return true;
                }
                case "discord": {
                    player.closeInventory();
                    player.openBook(BookManager.getDiscord());
                    return true;
                }
                case "rules": {
                    player.closeInventory();
                    player.openBook(BookManager.getRules());
                    return true;
                }
                 */
                default: {
                    player.closeInventory();
                    MenuManager.getInstance().open(Menus.MAIN, player);
                    return true;
                }
            }
        }
        return true;
    }
}
