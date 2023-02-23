package com.semivanilla.help.menus;

import com.google.gson.*;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

public abstract class Condition {
    public abstract boolean conditionIsMet(Player player, Object args);

    public class PermissionCondition extends Condition {
        private String permission;

        @Override
        public boolean conditionIsMet(Player player, Object args) {
            return player.hasPermission(permission);
        }
    }


    public class AndCondition extends Condition {
        private Condition a, b;

        @Override
        public boolean conditionIsMet(Player player, Object args) {
            return a.conditionIsMet(player, args) && b.conditionIsMet(player, args);
        }
    }

    public class OrCondition extends Condition {
        private Condition a, b;

        @Override
        public boolean conditionIsMet(Player player, Object args) {
            return a.conditionIsMet(player, args) || b.conditionIsMet(player, args);
        }
    }

    public class NotCondition extends Condition {
        private Condition condition;

        @Override
        public boolean conditionIsMet(Player player, Object args) {
            return !condition.conditionIsMet(player, args);
        }
    }

    public static class ConditionSerializer implements JsonSerializer<Condition>, JsonDeserializer<Condition> {

        @Override
        public Condition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString().toLowerCase();
            switch (type) {
                case "permission":
                    return context.deserialize(jsonObject, PermissionCondition.class);
                case "and":
                    return context.deserialize(jsonObject, AndCondition.class);
                case "or":
                    return context.deserialize(jsonObject, OrCondition.class);
                case "not":
                    return context.deserialize(jsonObject, NotCondition.class);
            }
            return null;
        }

        @Override
        public JsonElement serialize(Condition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = context.serialize(src).getAsJsonObject();
            jsonObject.addProperty("type", src.getClass().getSimpleName().replace("Condition", "").toLowerCase());
            return jsonObject;
        }
    }
}
