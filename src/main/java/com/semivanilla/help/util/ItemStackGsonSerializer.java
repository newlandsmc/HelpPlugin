package com.semivanilla.help.util;

import com.google.gson.*;
import net.badbird5907.blib.util.CC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.*;

public class ItemStackGsonSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            String base64 = json.getAsString();
            return base64ToItemStack(base64);
        } else {
            JsonObject jsonObject = json.getAsJsonObject();
            String name = jsonObject.has("name") ? CC.translate(jsonObject.get("name").getAsString()) : null;
            String material = jsonObject.get("material").getAsString();
            int amount = jsonObject.has("amount") ? jsonObject.get("amount").getAsInt() : 1;
            Map<String, Integer> enchants = null;
            if (jsonObject.has("enchants")) {
                enchants = new HashMap<>();
                JsonObject enchantsObject = jsonObject.get("enchants").getAsJsonObject();
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : enchantsObject.entrySet()) {
                    String ench = stringJsonElementEntry.getKey();
                    int lvl = stringJsonElementEntry.getValue().getAsInt();
                    enchants.put(ench, lvl);
                }
            }
            List<String> lore = new ArrayList<>();
            if (jsonObject.has("lore") && jsonObject.get("lore").isJsonArray()) {
                JsonArray loreArray = jsonObject.get("lore").getAsJsonArray();
                for (JsonElement jsonElement : loreArray) {
                    lore.add(CC.translate(jsonElement.getAsString()));
                }
            }
            List<String> flags = new ArrayList<>();
            if (jsonObject.has("flags") && jsonObject.get("flags").isJsonArray()) {
                JsonArray flagsArray = jsonObject.get("flags").getAsJsonArray();
                for (JsonElement jsonElement : flagsArray) {
                    flags.add(jsonElement.getAsString());
                }
            }
            ItemStack item = new ItemStack(Material.valueOf(material), amount);
            ItemMeta meta = item.getItemMeta();
            if (name != null) {
                meta.setDisplayName(name);
            }
            for (String flag : flags) {
                meta.addItemFlags(ItemFlag.valueOf(flag));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            if (enchants != null) {
                for (Map.Entry<String, Integer> stringIntegerEntry : enchants.entrySet()) {
                    Enchantment enchantment = Enchantments.getByName(stringIntegerEntry.getKey());//Enchantment.getByName(stringIntegerEntry.getKey());
                    if (enchantment == null) continue;
                    item.addUnsafeEnchantment(enchantment, stringIntegerEntry.getValue());
                }
            }
            return item;
        }
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(itemStackToBase64(src));
    }
    public static ItemStack base64ToItemStack(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        return ItemStack.deserializeBytes(bytes);
    }
    public static String itemStackToBase64(ItemStack itemStack) {
        byte[] bytes = itemStack.serializeAsBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
