package com.semivanilla.help.menus;

import com.google.gson.*;
import com.semivanilla.help.manager.BookManager;
import net.kyori.adventure.inventory.Book;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

public abstract class Action {
    public abstract boolean run(Player player);

    public static class CommandAction extends Action {
        private String command;

        @Override
        public boolean run(Player player) {
            player.performCommand(command);
            return true;
        }
    }
    public static class BookAction extends Action {
        private String book;

        @Override
        public boolean run(Player player) {
            player.closeInventory();
            Book book = BookManager.getBookByName(this.book);
            if (book == null) {
                player.sendMessage(ChatColor.RED + "Could not find book \"" + this.book + "\"!");
                return true;
            }
            // System.out.println("Opening book " + this.book + " | " + book.examinableName());
            try {
                player.closeInventory();
                player.openBook(book);
                return false;
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Could not open book \"" + this.book + "\"!");
                e.printStackTrace();
                throw e;
            }
        }
    }

    public static class ActionSerializer implements JsonSerializer<Action>, JsonDeserializer<Action> {

        @Override
        public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString().toLowerCase();
            switch (type) {
                case "command":
                    return context.deserialize(jsonObject, CommandAction.class);
                case "book":
                    return context.deserialize(jsonObject, BookAction.class);
            }
            return null;
        }

        @Override
        public JsonElement serialize(Action src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = context.serialize(src).getAsJsonObject();
            jsonObject.addProperty("type", src.getClass().getSimpleName().replace("Action", "").toLowerCase());
            return jsonObject;
        }
    }
}
