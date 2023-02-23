package com.semivanilla.help.menus;

import com.google.gson.*;
import com.semivanilla.help.HelpPlugin;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.util.Legacy;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.Data;
import net.badbird5907.blib.util.CC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Data
public class MenuConfig {
    private String title;
    private int rows;
    private List<MenuItemConfig> items;

    private FillConfig fill;

    public Gui createGui() {
        return Gui.gui()
                .title(MiniMessage.miniMessage().deserialize(title))
                .rows(rows)
                .disableAllInteractions()
                .create();
    }
    private static final GuiItem defaultFill = ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.space().color(NamedTextColor.RED)).lore().asGuiItem();

    public void populate(Gui gui, Player player) {
        //System.out.println("Items: " + items.size());
        for (MenuItemConfig item : items) {
            //System.out.println(" - Item: " + item.name);
            GuiItem guiItem = item.asGuiItem(player, gui, this);
            gui.setItem(item.getSlot(), guiItem);
        }
        if (fill != null && fill.isEnabled()) {
            gui.getFiller().fill(fill.isUseDefault() ? defaultFill : fill.getItem().asGuiItem(player, gui, this));
        }
    }

    public void open(Player player) {
        try {
            //System.out.println("Creating GUI...");
            Gui gui = createGui();
            //System.out.println("Populating GUI...");
            populate(gui, player);
            //System.out.println("Opening GUI...");
            gui.open(player);
            //System.out.println("GUI Opened. (" + title + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Data
    public static class FillConfig {
        private MenuItemConfig item;
        boolean enabled, useDefault;
    }

    @Data
    public static class MenuItemConfig {
        private MenuItemConfig parent;
        private int slot;
        private String material;
        private String name;
        private String[] lore;
        private List<Action> actions;
        private List<ConditionalItem> conditionalItems;

        public static MenuItemConfig fromJsonObject(JsonObject object) {
            MenuItemConfig menuItemConfig = new MenuItemConfig();
            if (object.has("slot")) {
                menuItemConfig.setSlot(object.get("slot").getAsInt());
            }
            if (object.has("material")) {
                menuItemConfig.setMaterial(object.get("material").getAsString());
            }
            if (object.has("name")) {
                menuItemConfig.setName(object.get("name").getAsString());
            }
            if (object.has("lore")) {
                JsonArray lore = object.get("lore").getAsJsonArray();
                String[] loreArray = new String[lore.size()];
                for (int i = 0; i < lore.size(); i++) {
                    loreArray[i] = lore.get(i).getAsString();
                }
                menuItemConfig.setLore(loreArray);
            }
            if (object.has("actions")) {
                JsonArray actions = object.get("actions").getAsJsonArray();
                List<Action> actionList = new ArrayList<>();
                for (JsonElement action : actions) {
                    actionList.add(HelpPlugin.getGson().fromJson(action.getAsJsonObject(), Action.class));
                }
                menuItemConfig.setActions(actionList);
            }
            if (object.has("conditionalItems")) {
                JsonArray conditionalItems = object.get("conditionalItems").getAsJsonArray();
                List<ConditionalItem> conditionalItemList = new ArrayList<>();
                for (JsonElement conditionalItem : conditionalItems) {
                    conditionalItemList.add(HelpPlugin.getGson().fromJson(conditionalItem.getAsJsonObject(), ConditionalItem.class));
                }
                menuItemConfig.setConditionalItems(conditionalItemList);
            }
            return menuItemConfig;
        }

        public Material getMaterial() {
            String material = this.material == null ? (parent != null ? parent.material : null) : this.material;
            return Material.getMaterial(material);
        }

        @SuppressWarnings("deprecation")
        public GuiItem asGuiItem(Player player, Gui gui, MenuConfig config) {
            try {
                boolean hasParent = parent != null;
                String name = this.name == null ? (hasParent ? parent.name : "") : this.name;
                String[] lore = this.lore == null || this.lore.length == 0 ? (hasParent ? parent.lore : new String[]{}) : this.lore;
                List<Action> actions = getActions();
                List<ConditionalItem> conditionalItems = getConditionalItems();
                if (conditionalItems != null) {
                    //System.out.println("name: " + name + " | lore: " + Arrays.asList(lore) + " | actions: " + actions.size() + " | cond: " + conditionalItems.size());
                    for (ConditionalItem conditionalItem : conditionalItems) {
                        if (conditionalItem.getCondition().conditionIsMet(player, null)) {
                            MenuItemConfig i = conditionalItem.getItem();
                            if (i != this)
                                return conditionalItem.getItem().asGuiItem(player, gui, config);
                        }
                    }
                } else {
                    //System.out.println("name: " + name + " | lore: " + Arrays.asList(lore) + " | actions: " + actions.size() + " | cond: null");
                }

                List<Component> loreComponents = new ArrayList<>();
                for (String loreLine : lore) {
                    loreComponents.add(MiniMessage.miniMessage().deserialize(loreLine));
                }
                return ItemBuilder.from(getMaterial())
                        .name(MiniMessage.miniMessage().deserialize(name))
                        .lore(loreComponents)
                        .asGuiItem(event -> {
                            for (Action action : actions) {
                                action.run(((Player) event.getWhoClicked()));
                            }
                            //gui.setItem(event.getSlot(), asGuiItem(player,gui));
                            //gui.update();
                            config.open(player); // TODO: reopening the gui causes flickering, figure out how to update the item.
                        });
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public List<Action> getActions() {
            return this.actions == null || this.actions.isEmpty() ? (parent != null ? parent.getActions() : null) : this.actions;
        }

        public List<ConditionalItem> getConditionalItems() {
            return this.conditionalItems == null || this.conditionalItems.isEmpty() ? (parent != null ? parent.getConditionalItems() : null) : this.conditionalItems;
        }

        public static class Serializer implements JsonSerializer<MenuItemConfig>, JsonDeserializer<MenuItemConfig> {

            @Override
            public MenuItemConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                MenuItemConfig config = fromJsonObject(json.getAsJsonObject()); //context.deserialize(json, MenuItemConfig.class);
                if (config.getConditionalItems() != null) {
                    for (ConditionalItem conditionalItem : config.getConditionalItems()) {
                        conditionalItem.getItem().setParent(config);
                    }
                }
                return config;
            }

            @Override
            public JsonElement serialize(MenuItemConfig src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject object = context.serialize(src, typeOfSrc).getAsJsonObject();
                // remove parent from conditional items
                if (src.getConditionalItems() != null && !src.getConditionalItems().isEmpty()) {
                    for (JsonElement conditionalItems : object.get("conditionalItems").getAsJsonArray()) {
                        JsonObject conditionalItem = conditionalItems.getAsJsonObject();
                        conditionalItem.remove("parent");
                    }
                }
                return object;
            }
        }
    }

    @Data
    public static class ConditionalItem {
        private MenuItemConfig item;
        private Condition condition;
    }
}
